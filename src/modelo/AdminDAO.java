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
public class AdminDAO {
    private Connection conexion;

    public AdminDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    public Integer validarAdmin(String correo, String password) {
        String sql = "SELECT ID_ADMIN FROM ADMIN WHERE CORREO = ? AND PASSWORD = ?";

        try (Connection conn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID_ADMIN"); // ✅ Retorna el ID si las credenciales son correctas
            }

        } catch (SQLException e) {
            System.err.println("Error al validar el administrador: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // ❌ Retorna null si no hay coincidencia o el estado no es ACTIVO
    }
}
