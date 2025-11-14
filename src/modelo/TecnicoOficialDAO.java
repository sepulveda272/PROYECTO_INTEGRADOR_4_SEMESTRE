/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class TecnicoOficialDAO {
    private final Connection conexion;

    public TecnicoOficialDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== SECUENCIA (función) ===================== */
    public int siguienteNumeroRegistro() {
        final String sql = "{ ? = call fn_obtener_tecnico_id() }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo ID (función): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== AUTH / LOGIN (BCrypt) ===================== */
    public Integer validarTecnico(String correo, String passwordPlano) {
        String sql = "SELECT NUMERO_REGISTRO, PASSWORD "
                   + "FROM TECNICO_OFICIAL "
                   + "WHERE CORREO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("NUMERO_REGISTRO");
                    String hash = rs.getString("PASSWORD");
                    if (hash != null && BCrypt.checkpw(passwordPlano, hash)) return id;
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
        final String sql = "{ call sp_insertar_tecnico_id(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, t.getNumero_registro());
            cs.setLong(2, t.getNumero_identificacion());
            cs.setString(3, t.getTipo_identificacion());
            cs.setString(4, t.getPrimer_nombre());
            cs.setString(5, t.getSegundo_nombre());     // OJO: en tu tabla es NOT NULL
            cs.setString(6, t.getPrimer_apellido());
            if (isBlank(t.getSegundo_apellido())) cs.setNull(7, Types.VARCHAR); else cs.setString(7, t.getSegundo_apellido());
            cs.setString(8, t.getDireccion());
            cs.setLong(9, t.getCelular());
            cs.setString(10, t.getCorreo());
            cs.setString(11, BCrypt.hashpw(t.getPassword(), BCrypt.gensalt(12)));
            cs.setString(12, t.getEstado()); // puede venir null → trigger pone ACTIVO
            cs.registerOutParameter(13, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(13) > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_TEC_IDENT")) {
                System.err.println("❌ Ya existe un técnico con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar tecnico (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public int insertarTecnicoAuto(TecnicoOficial t) {
        final String sql = "{ call sp_insertar_tecnico(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, t.getNumero_identificacion());
            cs.setString(2, t.getTipo_identificacion());
            cs.setString(3, t.getPrimer_nombre());
            cs.setString(4, t.getSegundo_nombre());      // NOT NULL según tu tabla
            cs.setString(5, t.getPrimer_apellido());
            if (isBlank(t.getSegundo_apellido())) cs.setNull(6, Types.VARCHAR); else cs.setString(6, t.getSegundo_apellido());
            cs.setString(7, t.getDireccion());
            cs.setLong(8, t.getCelular());
            cs.setString(9, t.getCorreo());
            cs.setString(10, BCrypt.hashpw(t.getPassword(), BCrypt.gensalt(12)));
            cs.setString(11, t.getEstado());             // puede venir null
            cs.registerOutParameter(12, Types.INTEGER);  // p_id_generado
            cs.execute();

            int nuevoId = cs.getInt(12);
            t.setNumero_registro(nuevoId);
            return nuevoId;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_TEC_IDENT")) {
                System.err.println("❌ Ya existe un técnico con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar tecnico auto (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */
    public List<TecnicoOficial> listarTecnicosActivos() {
        List<TecnicoOficial> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_tecnicos_activos(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<TecnicoOficial> listarTecnicos() {
        List<TecnicoOficial> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_tecnicos(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRowSinPassword(rs));
            }
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
        final String sql = "{ call sp_actualizar_tecnico(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, t.getNumero_registro());
            cs.setLong(2, t.getNumero_identificacion());
            cs.setString(3, t.getTipo_identificacion());
            cs.setString(4, t.getPrimer_nombre());
            cs.setString(5, t.getSegundo_nombre());
            cs.setString(6, t.getPrimer_apellido());
            if (isBlank(t.getSegundo_apellido())) cs.setNull(7, Types.VARCHAR); else cs.setString(7, t.getSegundo_apellido());
            cs.setString(8, t.getDireccion());
            cs.setLong(9, t.getCelular());
            cs.setString(10, t.getCorreo());
            cs.setString(11, t.getEstado());
            cs.registerOutParameter(12, Types.INTEGER);   // p_filas
            cs.execute();
            return cs.getInt(12) > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_TEC_IDENT")) {
                System.err.println("❌ Ya existe un técnico con ese numero_identificacion.");
            } else {
                System.err.println("Error al actualizar tecnico (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPassword(int numeroRegistro, String nuevoPasswordPlano) {
        if (isBlank(nuevoPasswordPlano)) {
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

    /* ===================== DELETE (lógico, validado) ===================== */
    public boolean desactivarTecnicoSiNoReferenciado(int numeroRegistro) {
        final String sql = "{ call sp_inactivar_tecnico(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, numeroRegistro);
            cs.registerOutParameter(2, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error al inactivar técnico (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */
    public boolean existeTecnico(int numeroRegistro) {
        final String sql = "{ call sp_existe_tecnico(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, numeroRegistro);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean existeTecnicoActivo(int numeroRegistro) {
        final String sql = "SELECT COUNT(*) FROM TECNICO_OFICIAL "
                         + "WHERE NUMERO_REGISTRO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroRegistro);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== Helpers ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
