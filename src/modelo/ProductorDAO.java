/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import modelo.ConexionBD;
/**
 *
 * @author ADMIN
 */
public class ProductorDAO {
    private final Connection conexion;

    public ProductorDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== SECUENCIA ===================== */
    /** Obtiene el siguiente id_productor desde la secuencia Oracle. 
     *  OJO: estás usando seq_producto; si renombraste a seq_productor, cambia aquí.
     */
    public int siguienteIdProductor() {
        String sql = "SELECT seq_productor.NEXTVAL FROM dual"; // <-- usa el nombre que nos diste
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_producto: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /* ===================== AUTH / LOGIN (BCrypt) ===================== */
    /** Retorna ID_PRODUCTOR si credenciales OK y ACTIVO; si no, null. */
    public Integer validarProductor(String correo, String passwordPlano) {
        String sql = "SELECT ID_PRODUCTOR, PASSWORD FROM PRODUCTOR WHERE CORREO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID_PRODUCTOR");
                    String hash = rs.getString("PASSWORD");
                    if (hash != null && BCrypt.checkpw(passwordPlano, hash)) {
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar productor: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /* ===================== CREATE ===================== */
    /** Inserta usando el id_productor recibido; hashea la contraseña. */
    public boolean insertarProductor(Productor p) {
        String sql = "INSERT INTO PRODUCTOR (ID_PRODUCTOR, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "DIRECCION, CELULAR, CORREO, PASSWORD) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // ESTADO -> DEFAULT 'ACTIVO'
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, p.getId_productor());
            ps.setLong(2, p.getNumero_identificacion());
            ps.setString(3, p.getTipo_identificacion());
            ps.setString(4, p.getPrimer_nombre());
            if (isBlank(p.getSegundo_nombre())) ps.setNull(5, Types.VARCHAR); else ps.setString(5, p.getSegundo_nombre());
            ps.setString(6, p.getPrimer_apellido());
            if (isBlank(p.getSegundo_apellido())) ps.setNull(7, Types.VARCHAR); else ps.setString(7, p.getSegundo_apellido());
            ps.setString(8, p.getDireccion());
            ps.setLong(9, p.getCelular());
            ps.setString(10, p.getCorreo());

            // Hash de contraseña
            String hash = BCrypt.hashpw(p.getPassword(), BCrypt.gensalt(12));
            ps.setString(11, hash);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Unique de numero_identificacion
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_PROD_IDENT")) {
                System.err.println("❌ Ya existe un productor con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar productor: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta autogenerando ID con secuencia. Retorna el ID generado (>0) o -1 si falla. */
    /** Inserta usando el procedimiento almacenado. Retorna el ID generado (>0) o -1 si falla. */
    public int insertarProductorAuto(Productor p) {
        String sql = "{ call sp_insertar_productor(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, p.getNumero_identificacion());
            cs.setString(2, p.getTipo_identificacion());
            cs.setString(3, p.getPrimer_nombre());
            cs.setString(4, p.getSegundo_nombre());   // OJO: tu columna es NOT NULL
            cs.setString(5, p.getPrimer_apellido());
            cs.setString(6, p.getSegundo_apellido()); // NOT NULL también
            cs.setString(7, p.getDireccion());
            cs.setLong(8, p.getCelular());
            cs.setString(9, p.getCorreo());

            // Hash de contraseña en Java
            String hash = BCrypt.hashpw(p.getPassword(), BCrypt.gensalt(12));
            cs.setString(10, hash);

            // Normalmente "ACTIVO"
            cs.setString(11, p.getEstado());

            // OUT: ID generado por el SP
            cs.registerOutParameter(12, Types.INTEGER);

            cs.execute();

            int nuevoId = cs.getInt(12);
            p.setId_productor(nuevoId);
            return nuevoId;

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_PROD_IDENT")) {
                System.err.println("❌ Ya existe un productor con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar productor (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */
    /** Lista solo ACTIVO; no expone password. */
    public List<Productor> listarProductoresActivos() {
        List<Productor> lista = new ArrayList<>();
        String sql = "SELECT ID_PRODUCTOR, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "DIRECCION, CELULAR, CORREO, ESTADO "
                   + "FROM PRODUCTOR WHERE ESTADO = 'ACTIVO' ORDER BY ID_PRODUCTOR";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRowSinPassword(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Lista todos (activos e inactivos); no expone password. */
    public List<Productor> listarProductores() {
        List<Productor> lista = new ArrayList<>();
        String sql = "SELECT ID_PRODUCTOR, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "DIRECCION, CELULAR, CORREO, ESTADO "
                   + "FROM PRODUCTOR ORDER BY ID_PRODUCTOR";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRowSinPassword(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Productor mapRowSinPassword(ResultSet rs) throws SQLException {
        Productor p = new Productor();
        p.setId_productor(rs.getInt("ID_PRODUCTOR"));
        p.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
        p.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
        p.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
        p.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
        p.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
        p.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
        p.setDireccion(rs.getString("DIRECCION"));
        p.setCelular(rs.getLong("CELULAR"));
        p.setCorreo(rs.getString("CORREO"));
        p.setEstado(rs.getString("ESTADO"));
        return p;
    }

    /* ===================== UPDATE ===================== */
    /** Actualiza datos generales; NO cambia la contraseña. */
    public boolean actualizarProductor(Productor p) {
        String sql = "UPDATE PRODUCTOR SET "
                   + "NUMERO_IDENTIFICACION = ?, "
                   + "TIPO_IDENTIFICACION = ?, "
                   + "PRIMER_NOMBRE = ?, "
                   + "SEGUNDO_NOMBRE = ?, "
                   + "PRIMER_APELLIDO = ?, "
                   + "SEGUNDO_APELLIDO = ?, "
                   + "DIRECCION = ?, "
                   + "CELULAR = ?, "
                   + "CORREO = ?, "
                   + "ESTADO = ? "
                   + "WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, p.getNumero_identificacion());
            ps.setString(2, p.getTipo_identificacion());
            ps.setString(3, p.getPrimer_nombre());
            if (isBlank(p.getSegundo_nombre())) ps.setNull(4, Types.VARCHAR); else ps.setString(4, p.getSegundo_nombre());
            ps.setString(5, p.getPrimer_apellido());
            if (isBlank(p.getSegundo_apellido())) ps.setNull(6, Types.VARCHAR); else ps.setString(6, p.getSegundo_apellido());
            ps.setString(7, p.getDireccion());
            ps.setLong(8, p.getCelular());
            ps.setString(9, p.getCorreo());
            ps.setString(10, p.getEstado());
            ps.setInt(11, p.getId_productor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_PROD_IDENT")) {
                System.err.println("❌ Ya existe un productor con ese numero_identificacion.");
            } else {
                System.err.println("Error al actualizar productor: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    /** Cambia contraseña: guarda hash BCrypt. */
    public boolean actualizarPassword(int idProductor, String nuevoPasswordPlano) {
        if (nuevoPasswordPlano == null || nuevoPasswordPlano.isEmpty()) {
            System.err.println("❌ password no puede ser vacío.");
            return false;
        }
        String sql = "UPDATE PRODUCTOR SET PASSWORD = ? WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String hash = BCrypt.hashpw(nuevoPasswordPlano, BCrypt.gensalt(12));
            ps.setString(1, hash);
            ps.setInt(2, idProductor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== DELETE (soft) ===================== */
    public boolean eliminarProductor(int idProductor) {
        String sql = "UPDATE PRODUCTOR SET ESTADO = 'INACTIVO' WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== EXISTS ===================== */
    public boolean existeProductorActivo(int idProductor) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOR WHERE ID_PRODUCTOR = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existeProductor(int idProductor) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOR WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /* ===================== Helpers ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
