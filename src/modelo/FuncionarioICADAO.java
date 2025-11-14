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

public class FuncionarioICADAO {
    private Connection conexion;

    public FuncionarioICADAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== SECUENCIA (opcional helper) ===================== */
    public int siguienteIdFuncionario() {
        final String sql = "{ ? = call fn_next_funcionario_id() }";
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
    public Integer validarFuncionario(String correo, String passwordPlano) {
        final String sql = "{ call sp_get_login_funcionario(?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, correo);
            cs.registerOutParameter(2, Types.INTEGER); // id
            cs.registerOutParameter(3, Types.VARCHAR); // hash
            cs.registerOutParameter(4, Types.VARCHAR); // estado
            cs.execute();

            Integer id = (Integer) cs.getObject(2);
            String hash = cs.getString(3);
            String estado = cs.getString(4);

            if (id != null && hash != null && "ACTIVO".equalsIgnoreCase(estado)
                    && BCrypt.checkpw(passwordPlano, hash)) {
                return id;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error en login (SP): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /* ===================== CREATE ===================== */
    public boolean insertarFuncionario(FuncionarioICA f) {
        final String sql = "{ call sp_insertar_funcionario_con_id(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1,  f.getId_funcionario());
            cs.setLong(2, f.getNumero_identificacion());
            cs.setString(3, f.getTipo_identificacion());
            cs.setString(4, f.getPrimer_nombre());
            if (isBlank(f.getSegundo_nombre())) cs.setNull(5, Types.VARCHAR); else cs.setString(5, f.getSegundo_nombre());
            cs.setString(6, f.getPrimer_apellido());
            if (isBlank(f.getSegundo_apellido())) cs.setNull(7, Types.VARCHAR); else cs.setString(7, f.getSegundo_apellido());
            cs.setLong(8,  f.getCelular());
            cs.setString(9,  f.getCorreo());
            cs.setString(10, BCrypt.hashpw(f.getPassword(), BCrypt.gensalt(12)));
            cs.setString(11, isBlank(f.getEstado()) ? "ACTIVO" : f.getEstado());
            cs.registerOutParameter(12, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(12) > 0;
        } catch (SQLException e) {
            String msg = (e.getMessage()==null?"":e.getMessage().toUpperCase());
            if (msg.contains("UQ_FUN_IDENT") || msg.contains("ORA-00001")) {
                System.err.println("❌ Ya existe un funcionario con ese numero_identificacion/correo.");
            } else {
                System.err.println("Error al insertar funcionario (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public int insertarFuncionarioAuto(FuncionarioICA f) {
        final String sql = "{ call sp_insertar_funcionario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, f.getNumero_identificacion());
            cs.setString(2, f.getTipo_identificacion());
            cs.setString(3, f.getPrimer_nombre());
            if (isBlank(f.getSegundo_nombre())) cs.setNull(4, Types.VARCHAR); else cs.setString(4, f.getSegundo_nombre());
            cs.setString(5, f.getPrimer_apellido());
            if (isBlank(f.getSegundo_apellido())) cs.setNull(6, Types.VARCHAR); else cs.setString(6, f.getSegundo_apellido());
            cs.setLong(7, f.getCelular());
            cs.setString(8, f.getCorreo());
            cs.setString(9, BCrypt.hashpw(f.getPassword(), BCrypt.gensalt(12)));
            cs.setString(10, isBlank(f.getEstado()) ? "ACTIVO" : f.getEstado());
            cs.registerOutParameter(11, Types.INTEGER); // p_id_generado
            cs.execute();

            int id = cs.getInt(11);
            f.setId_funcionario(id);
            return id;
        } catch (SQLException e) {
            System.err.println("Error al insertar funcionario (SP auto): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */
    public List<FuncionarioICA> listarFuncionariosActivos() {
        List<FuncionarioICA> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_funcionarios_activos(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando funcionarios activos (SP): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<FuncionarioICA> listarFuncionarios() {
        List<FuncionarioICA> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_funcionarios(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando funcionarios (SP): " + e.getMessage());
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
        final String sql = "{ call sp_actualizar_funcionario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1,  f.getId_funcionario());
            cs.setLong(2, f.getNumero_identificacion());
            cs.setString(3, f.getTipo_identificacion());
            cs.setString(4, f.getPrimer_nombre());
            if (isBlank(f.getSegundo_nombre())) cs.setNull(5, Types.VARCHAR); else cs.setString(5, f.getSegundo_nombre());
            cs.setString(6, f.getPrimer_apellido());
            if (isBlank(f.getSegundo_apellido())) cs.setNull(7, Types.VARCHAR); else cs.setString(7, f.getSegundo_apellido());
            cs.setLong(8, f.getCelular());
            cs.setString(9, f.getCorreo());
            cs.setString(10, f.getEstado());
            cs.registerOutParameter(11, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(11) > 0;
        } catch (SQLException e) {
            String msg = (e.getMessage()==null?"":e.getMessage().toUpperCase());
            if (msg.contains("UQ_FUN_IDENT") || msg.contains("ORA-00001")) {
                System.err.println("❌ Ya existe un funcionario con ese numero_identificacion/correo.");
            } else {
                System.err.println("Error al actualizar funcionario (SP): " + e.getMessage());
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
        final String sql = "{ call sp_actualizar_password_func(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idFuncionario);
            cs.setString(2, BCrypt.hashpw(nuevoPasswordPlano, BCrypt.gensalt(12)));
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return cs.getInt(3) > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar password (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE (lógico con verificación FK) ===================== */
    public boolean desactivarFuncionarioSiNoReferenciado(int idFuncionario) {
        if (!existeFuncionario(idFuncionario)) {
            System.err.println("⚠️ No existe el funcionario con id " + idFuncionario + ".");
            return false;
        }
        final String sql = "{ call sp_inactivar_funcionario(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idFuncionario);
            cs.registerOutParameter(2, Types.INTEGER); // p_code
            cs.registerOutParameter(3, Types.VARCHAR); // p_msg
            cs.execute();
            int code = cs.getInt(2);
            String msg = cs.getString(3);
            if (code == 0) return true;
            System.err.println("❌ " + msg);
            return false;
        } catch (SQLException e) {
            System.err.println("Error al inactivar funcionario (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */
    public boolean existeFuncionarioActivo(int idFuncionario) {
        final String sql = "{ call sp_existe_funcionario_activo(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idFuncionario);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error en existeFuncionarioActivo (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeFuncionario(int idFuncionario) {
        final String sql = "{ call sp_existe_funcionario(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idFuncionario);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error en existeFuncionario (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== Helpers ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}