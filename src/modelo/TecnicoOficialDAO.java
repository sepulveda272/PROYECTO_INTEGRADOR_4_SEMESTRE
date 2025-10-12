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
public class TecnicoOficialDAO {
    private Connection conexion;

    public TecnicoOficialDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    public Integer validarTecnico(String correo, String password) {
        String sql = "SELECT NUMERO_REGISTRO FROM TECNICO_OFICIAL WHERE CORREO = ? AND PASSWORD = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("NUMERO_REGISTRO"); // ✅ Retorna el ID si las credenciales son correctas
            }

        } catch (SQLException e) {
            System.err.println("Error al validar tecnico: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // ❌ Retorna null si no hay coincidencia o el estado no es ACTIVO
    }

    
    // Insertar un productor en la base de datos (CREATE)
    public boolean insertarTecnico(TecnicoOficial tecnicoOficial) {
        String sql = "INSERT INTO TECNICO_OFICIAL (NUMERO_REGISTRO, NUMERO_IDENTIFICACION, TIPO_IDENTIFICACION, " +
                 "PRIMER_NOMBRE, SEGUNDO_NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, " +
                 "DIRECCION, CELULAR, CORREO, PASSWORD) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, tecnicoOficial.getNumero_registro());
            ps.setLong(2, tecnicoOficial.getNumero_identificacion());
            ps.setString(3, tecnicoOficial.getTipo_identificacion());
            ps.setString(4, tecnicoOficial.getPrimer_nombre());

            // 👇 Si el segundo nombre está vacío o nulo, lo insertamos como NULL
            if (tecnicoOficial.getSegundo_nombre() == null || tecnicoOficial.getSegundo_nombre().trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, tecnicoOficial.getSegundo_nombre());
            }

            ps.setString(6, tecnicoOficial.getPrimer_apellido());

            // 👇 Si el segundo apellido está vacío o nulo, lo insertamos como NULL
            if (tecnicoOficial.getSegundo_apellido() == null || tecnicoOficial.getSegundo_apellido().trim().isEmpty()) {
                ps.setNull(7, java.sql.Types.VARCHAR);
            } else {
                ps.setString(7, tecnicoOficial.getSegundo_apellido());
            }

            ps.setString(8, tecnicoOficial.getDireccion());
            ps.setLong(9, tecnicoOficial.getCelular());
            ps.setString(10, tecnicoOficial.getCorreo());
            ps.setString(11, tecnicoOficial.getPassword());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // Listar todos los productos con el estado activo (READ - Lista completa)
    public List<TecnicoOficial> listarTecnicosActivos() {
        List<TecnicoOficial> lista = new ArrayList<>();
        String sql = "SELECT * FROM TECNICO_OFICIAL";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TecnicoOficial tecnicoOficial = new TecnicoOficial();
                tecnicoOficial.setNumero_registro(rs.getInt("NUMERO_REGISTRO"));
                tecnicoOficial.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
                tecnicoOficial.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
                tecnicoOficial.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
                tecnicoOficial.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
                tecnicoOficial.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
                tecnicoOficial.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
                tecnicoOficial.setDireccion(rs.getString("DIRECCION"));
                tecnicoOficial.setCelular(rs.getLong("CELULAR"));
                tecnicoOficial.setCorreo(rs.getString("CORREO"));
                tecnicoOficial.setPassword(rs.getString("PASSWORD"));
                tecnicoOficial.setEstado(rs.getString("ESTADO")); // por si quieres mostrarlo en la vista

                lista.add(tecnicoOficial);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Actualizar un productor (UPDATE)
    public boolean actualizarTecnico(TecnicoOficial tecnicoOficial) {
        String sql = "UPDATE TECNICO_OFICIAL SET " +
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
                     "WHERE NUMERO_REGISTRO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, tecnicoOficial.getNumero_identificacion());
            ps.setString(2, tecnicoOficial.getTipo_identificacion());
            ps.setString(3, tecnicoOficial.getPrimer_nombre());

            // Campo opcional: segundo nombre
            if (tecnicoOficial.getSegundo_nombre() == null || tecnicoOficial.getSegundo_nombre().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, tecnicoOficial.getSegundo_nombre());
            }

            ps.setString(5, tecnicoOficial.getPrimer_apellido());

            // Campo opcional: segundo apellido
            if (tecnicoOficial.getSegundo_apellido() == null || tecnicoOficial.getSegundo_apellido().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, tecnicoOficial.getSegundo_apellido());
            }

            ps.setString(7, tecnicoOficial.getDireccion());
            ps.setLong(8, tecnicoOficial.getCelular());
            ps.setString(9, tecnicoOficial.getCorreo());
            ps.setString(10, tecnicoOficial.getEstado());

            // WHERE
            ps.setInt(11, tecnicoOficial.getNumero_registro());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Eliminar un productor (DELETE)
    public boolean eliminarTecnico(int numero_registro) {
        String sql = "UPDATE TECNICO_OFICIAL SET ESTADO = 'INACTIVO' WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numero_registro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
