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
public class FuncionarioICADAO {
    private Connection conexion;

    public FuncionarioICADAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }
    /* ===================== SECUENCIA ===================== */

    /** Obtiene el siguiente id_funcionario desde la secuencia Oracle. */
    public int siguienteIdFuncionario() {
        String sql = "SELECT seq_funcionario.NEXTVAL FROM dual";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_funcionario: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /* ===================== AUTH / LOGIN (BCrypt) ===================== */

    public Integer validarFuncionario(String correo, String passwordPlano) {
        String sql = "SELECT ID_FUNCIONARIO, PASSWORD "
                   + "FROM FUNCIONARIO_ICA "
                   + "WHERE CORREO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID_FUNCIONARIO");
                    String hash = rs.getString("PASSWORD");
                    if (hash != null && BCrypt.checkpw(passwordPlano, hash)) {
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar funcionario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /* ===================== CREATE ===================== */

    public boolean insertarFuncionario(FuncionarioICA f) {
        String sql = "INSERT INTO FUNCIONARIO_ICA ("
                   + "ID_FUNCIONARIO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "CELULAR, CORREO, PASSWORD, ESTADO) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, f.getId_funcionario());
            ps.setLong(2, f.getNumero_identificacion());
            ps.setString(3, f.getTipo_identificacion());
            ps.setString(4, f.getPrimer_nombre());

            // Segundo nombre opcional
            if (isBlank(f.getSegundo_nombre())) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, f.getSegundo_nombre());
            }

            ps.setString(6, f.getPrimer_apellido());

            // Segundo apellido opcional
            if (isBlank(f.getSegundo_apellido())) {
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setString(7, f.getSegundo_apellido());
            }

            ps.setLong(8, f.getCelular());
            ps.setString(9, f.getCorreo());
            // Guardar HASH de la contraseña
            ps.setString(10, BCrypt.hashpw(f.getPassword(), BCrypt.gensalt(12)));
            ps.setString(11, f.getEstado() != null ? f.getEstado() : "ACTIVO");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage() != null &&
                e.getMessage().toUpperCase().contains("UQ_FUN_IDENT")) {
                System.err.println("❌ Ya existe un funcionario con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar funcionario: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public int insertarFuncionarioAuto(FuncionarioICA f) {
        int nuevoId = siguienteIdFuncionario();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de seq_funcionario.");
            return -1;
        }
        f.setId_funcionario(nuevoId);
        boolean ok = insertarFuncionario(f);
        return ok ? nuevoId : -1;
    }

    /* ===================== READ ===================== */

    public List<FuncionarioICA> listarFuncionariosActivos() {
        List<FuncionarioICA> lista = new ArrayList<>();
        String sql = "SELECT ID_FUNCIONARIO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "CELULAR, CORREO, ESTADO "
                   + "FROM FUNCIONARIO_ICA "
                   + "WHERE ESTADO = 'ACTIVO' "
                   + "ORDER BY ID_FUNCIONARIO";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<FuncionarioICA> listarFuncionarios() {
        List<FuncionarioICA> lista = new ArrayList<>();
        String sql = "SELECT ID_FUNCIONARIO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, "
                   + "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, "
                   + "CELULAR, CORREO, ESTADO "
                   + "FROM FUNCIONARIO_ICA "
                   + "ORDER BY ID_FUNCIONARIO";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private FuncionarioICA mapRowSinPassword(ResultSet rs) throws SQLException {
        FuncionarioICA f = new FuncionarioICA();
        f.setId_funcionario(rs.getInt("ID_FUNCIONARIO"));
        f.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
        f.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
        f.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
        f.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
        f.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
        f.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
        f.setCelular(rs.getLong("CELULAR"));
        f.setCorreo(rs.getString("CORREO"));
        f.setEstado(rs.getString("ESTADO"));
        return f;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarFuncionario(FuncionarioICA f) {
        String sql = "UPDATE FUNCIONARIO_ICA SET "
                   + "NUMERO_IDENTIFICACION = ?, "
                   + "TIPO_IDENTIFICACION = ?, "
                   + "PRIMER_NOMBRE = ?, "
                   + "SEGUNDO_NOMBRE = ?, "
                   + "PRIMER_APELLIDO = ?, "
                   + "SEGUNDO_APELLIDO = ?, "
                   + "CELULAR = ?, "
                   + "CORREO = ?, "
                   + "ESTADO = ? "
                   + "WHERE ID_FUNCIONARIO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, f.getNumero_identificacion());
            ps.setString(2, f.getTipo_identificacion());
            ps.setString(3, f.getPrimer_nombre());

            if (isBlank(f.getSegundo_nombre())) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, f.getSegundo_nombre());
            }

            ps.setString(5, f.getPrimer_apellido());

            if (isBlank(f.getSegundo_apellido())) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, f.getSegundo_apellido());
            }

            ps.setLong(7, f.getCelular());
            ps.setString(8, f.getCorreo());
            ps.setString(9, f.getEstado());
            ps.setInt(10, f.getId_funcionario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage() != null &&
                e.getMessage().toUpperCase().contains("UQ_FUN_IDENT")) {
                System.err.println("❌ Ya existe un funcionario con ese numero_identificacion.");
            } else {
                System.err.println("Error al actualizar funcionario: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPassword(int idFuncionario, String nuevoPasswordPlano) {
        if (nuevoPasswordPlano == null || nuevoPasswordPlano.isEmpty()) {
            System.err.println("❌ password no puede ser vacío.");
            return false;
        }
        String sql = "UPDATE FUNCIONARIO_ICA SET PASSWORD = ? WHERE ID_FUNCIONARIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String hash = BCrypt.hashpw(nuevoPasswordPlano, BCrypt.gensalt(12));
            ps.setString(1, hash);
            ps.setInt(2, idFuncionario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

     /* ===================== DELETE (Lógico, con validación FK) ===================== */

    /** Cuenta observaciones que referencian al funcionario. */
    private int contarObservacionePorFuncionario(int idFuncionario) {
        final String sql = "SELECT COUNT(*) "
                         + "FROM OBSERVACIONES "
                         + "WHERE ID_FUNCIONARIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Inactiva SOLO si no está referenciado por observaciones/inspecciones. */
    public boolean desactivarFuncionarioSiNoReferenciado(int idFuncionario) {
        if (!existeFuncionario(idFuncionario)) {
            System.err.println("⚠️ No existe el funcionario con id " + idFuncionario + ".");
            return false;
        }

        int refs = contarObservacionePorFuncionario(idFuncionario);
        if (refs < 0) {
            System.err.println("Error verificando referencias de observaciones.");
            return false;
        }
        if (refs > 0) {
            System.err.println("❌ No se puede inactivar: el funcionario está referenciado por "
                               + refs + " observación(es).");
            return false;
        }

        final String sql = "UPDATE FUNCIONARIO_ICA SET ESTADO = 'INACTIVO' WHERE ID_FUNCIONARIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar funcionario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeFuncionarioActivo(int idFuncionario) {
        String sql = "SELECT COUNT(*) FROM FUNCIONARIO_ICA "
                   + "WHERE ID_FUNCIONARIO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeFuncionario(int idFuncionario) {
        String sql = "SELECT COUNT(*) FROM FUNCIONARIO_ICA WHERE ID_FUNCIONARIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===================== Helpers ===================== */

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
