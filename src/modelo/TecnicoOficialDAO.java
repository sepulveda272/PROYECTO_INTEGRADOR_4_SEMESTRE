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
public class TecnicoOficialDAO {
    private final Connection conexion;

    public TecnicoOficialDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== SECUENCIA ===================== */

    /** Obtiene el siguiente numero_registro desde la secuencia Oracle. */
    public int siguienteNumeroRegistro() {
        String sql = "SELECT seq_tecnico.NEXTVAL FROM dual";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_tecnico: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /* ===================== AUTH / LOGIN (BCrypt) ===================== */

    public Integer validarTecnico(String correo, String passwordPlano) {
        String sql = "SELECT NUMERO_REGISTRO, PASSWORD FROM TECNICO_OFICIAL WHERE CORREO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("NUMERO_REGISTRO");
                    String hash = rs.getString("PASSWORD");
                    if (hash != null && BCrypt.checkpw(passwordPlano, hash)) {
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar tecnico: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /* ===================== CREATE ===================== */

    public boolean insertarTecnico(TecnicoOficial t) {
        String sql = "INSERT INTO TECNICO_OFICIAL (NUMERO_REGISTRO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "DIRECCION, CELULAR, CORREO, PASSWORD) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, t.getNumero_registro());
            ps.setLong(2, t.getNumero_identificacion());
            ps.setString(3, t.getTipo_identificacion());
            ps.setString(4, t.getPrimer_nombre());
            if (isBlank(t.getSegundo_nombre())) ps.setNull(5, Types.VARCHAR); else ps.setString(5, t.getSegundo_nombre());
            ps.setString(6, t.getPrimer_apellido());
            if (isBlank(t.getSegundo_apellido())) ps.setNull(7, Types.VARCHAR); else ps.setString(7, t.getSegundo_apellido());
            ps.setString(8, t.getDireccion());
            ps.setLong(9, t.getCelular());
            ps.setString(10, t.getCorreo());
            ps.setString(11, BCrypt.hashpw(t.getPassword(), BCrypt.gensalt(12)));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_TEC_IDENT")) {
                System.err.println("❌ Ya existe un técnico con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar tecnico: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public int insertarTecnicoAuto(TecnicoOficial t) {
        int nuevoId = siguienteNumeroRegistro();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de seq_tecnico.");
            return -1;
        }
        t.setNumero_registro(nuevoId);
        boolean ok = insertarTecnico(t);
        return ok ? nuevoId : -1;
    }

    /* ===================== READ ===================== */

    public List<TecnicoOficial> listarTecnicosActivos() {
        List<TecnicoOficial> lista = new ArrayList<>();
        String sql = "SELECT NUMERO_REGISTRO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "DIRECCION, CELULAR, CORREO, ESTADO "
                   + "FROM TECNICO_OFICIAL WHERE ESTADO = 'ACTIVO' ORDER BY NUMERO_REGISTRO";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRowSinPassword(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<TecnicoOficial> listarTecnicos() {
        List<TecnicoOficial> lista = new ArrayList<>();
        String sql = "SELECT NUMERO_REGISTRO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "DIRECCION, CELULAR, CORREO, ESTADO "
                   + "FROM TECNICO_OFICIAL ORDER BY NUMERO_REGISTRO";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRowSinPassword(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private TecnicoOficial mapRowSinPassword(ResultSet rs) throws SQLException {
        TecnicoOficial t = new TecnicoOficial();
        t.setNumero_registro(rs.getInt("NUMERO_REGISTRO"));
        t.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
        t.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
        t.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
        t.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
        t.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
        t.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
        t.setDireccion(rs.getString("DIRECCION"));
        t.setCelular(rs.getLong("CELULAR"));
        t.setCorreo(rs.getString("CORREO"));
        t.setEstado(rs.getString("ESTADO"));
        return t;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarTecnico(TecnicoOficial t) {
        String sql = "UPDATE TECNICO_OFICIAL SET "
                   + "NUMERO_IDENTIFICACION = ?, TIPO_IDENTIFICACION = ?, "
                   + "PRIMER_NOMBRE = ?, SEGUNDO_NOMBRE = ?, PRIMER_APELLIDO = ?, SEGUNDO_APELLIDO = ?, "
                   + "DIRECCION = ?, CELULAR = ?, CORREO = ?, ESTADO = ? "
                   + "WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, t.getNumero_identificacion());
            ps.setString(2, t.getTipo_identificacion());
            ps.setString(3, t.getPrimer_nombre());
            if (isBlank(t.getSegundo_nombre())) ps.setNull(4, Types.VARCHAR); else ps.setString(4, t.getSegundo_nombre());
            ps.setString(5, t.getPrimer_apellido());
            if (isBlank(t.getSegundo_apellido())) ps.setNull(6, Types.VARCHAR); else ps.setString(6, t.getSegundo_apellido());
            ps.setString(7, t.getDireccion());
            ps.setLong(8, t.getCelular());
            ps.setString(9, t.getCorreo());
            ps.setString(10, t.getEstado());
            ps.setInt(11, t.getNumero_registro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_TEC_IDENT")) {
                System.err.println("❌ Ya existe un técnico con ese numero_identificacion.");
            } else {
                System.err.println("Error al actualizar tecnico: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPassword(int numeroRegistro, String nuevoPasswordPlano) {
        if (nuevoPasswordPlano == null || nuevoPasswordPlano.isEmpty()) {
            System.err.println("❌ password no puede ser vacío.");
            return false;
        }
        String sql = "UPDATE TECNICO_OFICIAL SET PASSWORD = ? WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String hash = BCrypt.hashpw(nuevoPasswordPlano, BCrypt.gensalt(12));
            ps.setString(1, hash);
            ps.setInt(2, numeroRegistro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== DELETE (Lógico, con validación FK) ===================== */

    /** Cuenta inspecciones que referencian al técnico. */
    private int contarInspeccionesPorTecnico(int numeroRegistro) {
        final String sql = "SELECT COUNT(*) FROM INSPECCION_FITOSANITARIA WHERE ID_TECNICO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Inactiva SOLO si no está referenciado por inspecciones. */
    public boolean desactivarTecnicoSiNoReferenciado(int numeroRegistro) {
        if (!existeTecnico(numeroRegistro)) {
            System.err.println("⚠️ No existe el técnico con número de registro " + numeroRegistro + ".");
            return false;
        }

        int refs = contarInspeccionesPorTecnico(numeroRegistro);
        if (refs < 0) {
            System.err.println("Error verificando referencias de inspecciones.");
            return false;
        }
        if (refs > 0) {
            System.err.println("❌ No se puede inactivar: el técnico está referenciado por " + refs + " inspección(es).");
            return false;
        }

        final String sql = "UPDATE TECNICO_OFICIAL SET ESTADO = 'INACTIVO' WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroRegistro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar técnico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeTecnicoActivo(int numeroRegistro) {
        String sql = "SELECT COUNT(*) FROM TECNICO_OFICIAL WHERE NUMERO_REGISTRO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroRegistro);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existeTecnico(int numeroRegistro) {
        String sql = "SELECT COUNT(*) FROM TECNICO_OFICIAL WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroRegistro);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /* ===================== Helpers ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
