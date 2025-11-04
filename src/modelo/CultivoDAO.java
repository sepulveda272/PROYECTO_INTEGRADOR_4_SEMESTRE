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
public class CultivoDAO {
    private Connection conexion;

    public CultivoDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    // CREATE
    public boolean insertarCultivo(Cultivo c) {
        String sql = "INSERT INTO CULTIVO (ID_CULTIVO, NOMBRE_ESPECIE, VARIEDAD, DESCRIPCION) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, c.getId_cultivo());
            ps.setString(2, c.getNombre_especie());
            ps.setString(3, c.getVariedad());
            if (c.getDescripcion() == null || c.getDescripcion().trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, c.getDescripcion());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ: lista completa
    public List<Cultivo> listarCultivos() {
        List<Cultivo> lista = new ArrayList<>();
        String sql = "SELECT ID_CULTIVO, NOMBRE_ESPECIE, VARIEDAD, DESCRIPCION FROM CULTIVO ORDER BY ID_CULTIVO";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cultivo c = new Cultivo();
                c.setId_cultivo(rs.getInt("ID_CULTIVO"));
                c.setNombre_especie(rs.getString("NOMBRE_ESPECIE"));
                c.setVariedad(rs.getString("VARIEDAD"));
                c.setDescripcion(rs.getString("DESCRIPCION"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    

    // UPDATE
    public boolean actualizarCultivo(Cultivo c) {
        String sql = "UPDATE CULTIVO SET " +
                "NOMBRE_ESPECIE = ?, " +
                "VARIEDAD = ?, " +
                "DESCRIPCION = ? " +
                "WHERE ID_CULTIVO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, c.getNombre_especie());
            ps.setString(2, c.getVariedad());
            if (c.getDescripcion() == null || c.getDescripcion().trim().isEmpty()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, c.getDescripcion());
            }
            ps.setInt(4, c.getId_cultivo());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean eliminarCultivo(int idCultivo) {
        String sql = "DELETE FROM CULTIVO WHERE ID_CULTIVO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCultivo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // EXISTS por ID
    public boolean existeCultivo(int idCultivo) {
        String sql = "SELECT COUNT(*) FROM CULTIVO WHERE ID_CULTIVO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCultivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
