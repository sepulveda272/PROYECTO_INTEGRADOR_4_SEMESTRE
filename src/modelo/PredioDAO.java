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
public class PredioDAO {
    private Connection conexion;

    public PredioDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }
    
    // 🟢 INSERTAR un predio (CREATE)
    public boolean insertarPredio(Predio predio) {
        String sql = "INSERT INTO PREDIO " +
                     "(ID_PREDIO, NOMBRE_PREDIO, AREA_TOTAL, NOMBRE_PROPIETARIO, " +
                     "DIRECCION, COORDENADAS_LAT, COORDENADAS_LON, ID_LUGAR) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, predio.getId_predio());
            ps.setString(2, predio.getNombre_predio());
            ps.setDouble(3, predio.getArea_total());
            ps.setString(4, predio.getNombre_propietario());
            ps.setString(5, predio.getDireccion());
            ps.setDouble(6, predio.getCoordenadas_lat());
            ps.setDouble(7, predio.getCoordenadas_lon());
            ps.setInt(8, predio.getId_lugar());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar predio: " + e.getMessage());
            return false;
        }
    }

    // 🟡 LISTAR todos los predios (READ)
    public List<Predio> listarPredios() {
        List<Predio> lista = new ArrayList<>();
        String sql = "SELECT * FROM PREDIO";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Predio predio = new Predio();
                predio.setId_predio(rs.getInt("ID_PREDIO"));
                predio.setNombre_predio(rs.getString("NOMBRE_PREDIO"));
                predio.setArea_total(rs.getDouble("AREA_TOTAL"));
                predio.setNombre_propietario(rs.getString("NOMBRE_PROPIETARIO"));
                predio.setDireccion(rs.getString("DIRECCION"));
                predio.setCoordenadas_lat(rs.getDouble("COORDENADAS_LAT"));
                predio.setCoordenadas_lon(rs.getDouble("COORDENADAS_LON"));
                predio.setId_lugar(rs.getInt("ID_LUGAR"));
                predio.setEstado(rs.getString("ESTADO"));
                
                lista.add(predio);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar predios: " + e.getMessage());
        }

        return lista;
    }

    // 🟠 ACTUALIZAR un predio (UPDATE)
    public boolean actualizarPredio(Predio predio) {
        String sql = "UPDATE PREDIO SET " +
                     "NOMBRE_PREDIO = ?, " +
                     "AREA_TOTAL = ?, " +
                     "NOMBRE_PROPIETARIO = ?, " +
                     "DIRECCION = ?, " +
                     "COORDENADAS_LAT = ?, " +
                     "COORDENADAS_LON = ?, " +
                     "ID_LUGAR = ?, " +
                     "ESTADO = ? " +
                     "WHERE ID_PREDIO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, predio.getNombre_predio());
            ps.setDouble(2, predio.getArea_total());
            ps.setString(3, predio.getNombre_propietario());
            ps.setString(4, predio.getDireccion());
            ps.setDouble(5, predio.getCoordenadas_lat());
            ps.setDouble(6, predio.getCoordenadas_lon());
            ps.setInt(7, predio.getId_lugar());
            ps.setString(8, predio.getEstado());
            ps.setInt(9, predio.getId_predio());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar predio: " + e.getMessage());
            return false;
        }
    }

    // 🔴 ELIMINAR un predio (DELETE)
    public boolean eliminarPredio(int id_predio) {
        String sql = "UPDATE PREDIO SET ESTADO = 'INACTIVO' WHERE ID_PREDIO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id_predio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar predio: " + e.getMessage());
            return false;
        }
    }
}
