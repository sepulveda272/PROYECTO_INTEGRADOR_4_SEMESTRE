/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.ConexionBD;
/**
 *
 * @author ADMIN
 */
public class ProductorDAO {
    private Connection conexion;

    public ProductorDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    public Integer validarProductor(String correo, String password) {
        String sql = "SELECT ID_PRODUCTOR FROM PRODUCTOR WHERE CORREO = ? AND PASSWORD = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID_PRODUCTOR"); // ✅ Retorna el ID si las credenciales son correctas
            }

        } catch (SQLException e) {
            System.err.println("Error al validar productor: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // ❌ Retorna null si no hay coincidencia o el estado no es ACTIVO
    }

    
    // Insertar un productor en la base de datos (CREATE)
    public boolean insertarProductor(Productor productor) {
        String sql = "INSERT INTO PRODUCTOR (ID_PRODUCTOR, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, " +
                 "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, " +
                 "DIRECCION, CELULAR, CORREO, PASSWORD) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, productor.getId_productor());
            ps.setLong(2, productor.getNumero_identificacion());
            ps.setString(3, productor.getTipo_identificacion());
            ps.setString(4, productor.getPrimer_nombre());

            // 👇 Si el segundo nombre está vacío o nulo, lo insertamos como NULL
            if (productor.getSegundo_nombre() == null || productor.getSegundo_nombre().trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, productor.getSegundo_nombre());
            }

            ps.setString(6, productor.getPrimer_apellido());

            // 👇 Si el segundo apellido está vacío o nulo, lo insertamos como NULL
            if (productor.getSegundo_apellido() == null || productor.getSegundo_apellido().trim().isEmpty()) {
                ps.setNull(7, java.sql.Types.VARCHAR);
            } else {
                ps.setString(7, productor.getSegundo_apellido());
            }

            ps.setString(8, productor.getDireccion());
            ps.setLong(9, productor.getCelular());
            ps.setString(10, productor.getCorreo());
            ps.setString(11, productor.getPassword());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // Listar todos los productos con el estado activo (READ - Lista completa)
    public List<Productor> listarProductoresActivos() {
        List<Productor> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOR";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Productor productor = new Productor();
                productor.setId_productor(rs.getInt("ID_PRODUCTOR"));
                productor.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
                productor.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
                productor.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
                productor.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
                productor.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
                productor.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
                productor.setDireccion(rs.getString("DIRECCION"));
                productor.setCelular(rs.getLong("CELULAR"));
                productor.setCorreo(rs.getString("CORREO"));
                productor.setPassword(rs.getString("PASSWORD"));
                productor.setEstado(rs.getString("ESTADO")); // por si quieres mostrarlo en la vista

                lista.add(productor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Actualizar un productor (UPDATE)
    public boolean actualizarProductor(Productor productor) {
        String sql = "UPDATE PRODUCTOR SET " +
                     "NUMERO_IDENTIFICACION = ?, " +
                     "TIPO_IDENTIFICACION = ?, " +
                     "PRIMER_NOMBRE = ?, " +
                     "SEGUNDO_NOMBRE = ?, " +
                     "PRIMER_APELLIDO = ?, " +
                     "SEGUNDO_APELLIDO = ?, " +
                     "DIRECCION = ?, " +
                     "CELULAR = ?, " +
                     "CORREO = ?, " +
                     "ESTADO = ? " +
                     "WHERE ID_PRODUCTOR = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, productor.getNumero_identificacion());
            ps.setString(2, productor.getTipo_identificacion());
            ps.setString(3, productor.getPrimer_nombre());

            // Campo opcional: segundo nombre
            if (productor.getSegundo_nombre() == null || productor.getSegundo_nombre().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, productor.getSegundo_nombre());
            }

            ps.setString(5, productor.getPrimer_apellido());

            // Campo opcional: segundo apellido
            if (productor.getSegundo_apellido() == null || productor.getSegundo_apellido().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, productor.getSegundo_apellido());
            }

            ps.setString(7, productor.getDireccion());
            ps.setLong(8, productor.getCelular());
            ps.setString(9, productor.getCorreo());
            ps.setString(10, productor.getEstado());

            // WHERE
            ps.setInt(11, productor.getId_productor());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Eliminar un productor (DELETE)
    public boolean eliminarProductor(int id_productor) {
        String sql = "UPDATE PRODUCTOR SET ESTADO = 'INACTIVO' WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id_productor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean existeProductorActivo(int idProductor) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOR WHERE ID_PRODUCTOR = ? AND ESTADO = 'ACTIVO'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
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
