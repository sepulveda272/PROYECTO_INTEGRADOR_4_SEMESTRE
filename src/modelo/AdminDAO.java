/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author ADMIN
 */
public class AdminDAO {

    private final Connection conexion;

    public AdminDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    public Integer validarAdmin(String correo, String password) {

        final String sql = "{ call pr_validar_admin(?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setString(1, correo);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.INTEGER);

            cs.execute();

            int id = cs.getInt(3);
            if (cs.wasNull()) {
                return null; // no hubo coincidencia
            }
            return id;

        } catch (SQLException e) {
            System.err.println("Error al validar el administrador (pr_validar_admin): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
