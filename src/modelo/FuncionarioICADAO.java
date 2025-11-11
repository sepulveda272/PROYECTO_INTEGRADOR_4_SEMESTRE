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
    
    /** Obtiene el siguiente idFuncionario desde la secuencia Oracle. */
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

    public Integer validarFuncionario(String correo, String passwordPlano) {
        String sql = "SELECT ID_FUNCIONARIO, PASSWORD FROM FUNCIONARIO_ICA WHERE CORREO = ? AND ESTADO = 'ACTIVO'";
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
            System.err.println("Error al validar tecnico: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    // Insertar un productor en la base de datos (CREATE)
    public boolean insertarFuncionario(FuncionarioICA funcionario) {
        String sql = "INSERT INTO FUNCIONARIO_ICA (" +
                     "ID_FUNCIONARIO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, " +
                     "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, " +
                     "CELULAR, CORREO, PASSWORD, ESTADO) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, funcionario.getId_funcionario());
            ps.setLong(2, funcionario.getNumero_identificacion());
            ps.setString(3, funcionario.getTipo_identificacion());
            ps.setString(4, funcionario.getPrimer_nombre());

            // Segundo nombre opcional
            if (funcionario.getSegundo_nombre()== null || funcionario.getSegundo_nombre().trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, funcionario.getSegundo_nombre());
            }

            ps.setString(6, funcionario.getPrimer_apellido());

            // Segundo apellido opcional
            if (funcionario.getSegundo_apellido()== null || funcionario.getSegundo_apellido().trim().isEmpty()) {
                ps.setNull(7, java.sql.Types.VARCHAR);
            } else {
                ps.setString(7, funcionario.getSegundo_apellido());
            }

            ps.setLong(8, funcionario.getCelular());
            ps.setString(9, funcionario.getCorreo());
            ps.setString(10, funcionario.getPassword());
            ps.setString(11, funcionario.getEstado() != null ? funcionario.getEstado() : "ACTIVO");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int insertarFuncionarioAuto(FuncionarioICA funcionario) {
        int nuevoId = siguienteIdFuncionario();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de seq_funcionario.");
            return -1;
        }
        funcionario.setId_funcionario(nuevoId);
        boolean ok = insertarFuncionario(funcionario);
        return ok ? nuevoId : -1;
    }




    // Listar todos los productos con el estado activo (READ - Lista completa)
    public List<FuncionarioICA> listarFuncionarios() {
        List<FuncionarioICA> lista = new ArrayList<>();
        String sql = "SELECT * FROM FUNCIONARIO_ICA ORDER BY ID_FUNCIONARIO";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FuncionarioICA funcionario = new FuncionarioICA();
                funcionario.setId_funcionario(rs.getInt("ID_FUNCIONARIO"));
                funcionario.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
                funcionario.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
                funcionario.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
                funcionario.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
                funcionario.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
                funcionario.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
                funcionario.setCelular(rs.getLong("CELULAR"));
                funcionario.setCorreo(rs.getString("CORREO"));
                funcionario.setPassword(rs.getString("PASSWORD"));
                funcionario.setEstado(rs.getString("ESTADO"));

                lista.add(funcionario);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Actualizar un productor (UPDATE)
    public boolean actualizarFuncionario(FuncionarioICA funcionario) {
        String sql = "UPDATE FUNCIONARIO_ICA SET " +
                     "NUMERO_IDENTIFICACION = ?, " +
                     "TIPO_IDENTIFICACION = ?, " +
                     "PRIMER_NOMBRE = ?, " +
                     "SEGUNDO_NOMBRE = ?, " +
                     "PRIMER_APELLIDO = ?, " +
                     "SEGUNDO_APELLIDO = ?, " +
                     "CELULAR = ?, " +
                     "CORREO = ?, " +
                     "ESTADO = ? " +
                     "WHERE ID_FUNCIONARIO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, funcionario.getNumero_identificacion());
            ps.setString(2, funcionario.getTipo_identificacion());
            ps.setString(3, funcionario.getPrimer_nombre());

            // Segundo nombre opcional
            if (funcionario.getSegundo_nombre()== null || funcionario.getSegundo_nombre().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, funcionario.getSegundo_nombre());
            }

            ps.setString(5, funcionario.getPrimer_apellido());

            // Segundo apellido opcional
            if (funcionario.getSegundo_apellido()== null || funcionario.getSegundo_apellido().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, funcionario.getSegundo_apellido());
            }

            ps.setLong(7, funcionario.getCelular());
            ps.setString(8, funcionario.getCorreo());
            ps.setString(9, funcionario.getEstado());
            ps.setInt(10, funcionario.getId_funcionario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR LÓGICAMENTE (DELETE → INACTIVO)
    public boolean eliminarFuncionario(int idFuncionario) {
        String sql = "UPDATE FUNCIONARIO_ICA SET ESTADO = 'INACTIVO' WHERE ID_FUNCIONARIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean existeFuncionarioActivo(int idFuncionario) {
        String sql = "SELECT COUNT(*) FROM FUNCIONARIO_ICA WHERE ID_FUNCIONARIO = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true si existe y está activo
                }
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
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true si existe y está activo
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
