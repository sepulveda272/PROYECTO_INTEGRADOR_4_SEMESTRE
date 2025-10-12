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
public class LugarProduccionDAO {
    private Connection conexion;

    public LugarProduccionDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }
    
    // Insertar un productor en la base de datos (CREATE)
    public boolean insertarLugarProduccion(LugarProduccion lugarProduccion) {
        String sql = "INSERT INTO LUGAR_PRODUCCION " +
                    "(ID_LUGAR, DEPARTAMENTO, MUNICIPIO, VEREDA, CANTIDAD_MAXIMA, ID_PRODUCTOR) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

           // ID del lugar
           ps.setInt(1, lugarProduccion.getId_lugar());

           // Departamento
           ps.setString(2, lugarProduccion.getDepartamento());

           // Municipio
           ps.setString(3, lugarProduccion.getMunicipio());

           // Vereda
           ps.setString(4, lugarProduccion.getVereda());

           // Cantidad máxima
           ps.setInt(5, lugarProduccion.getCantidad_maxima());

           // ID del productor (FK)
           ps.setInt(6, lugarProduccion.getId_productor());

           // Ejecutar y retornar resultado
           return ps.executeUpdate() > 0;

        } catch (SQLException e) {
           e.printStackTrace();
           return false;
        }
    }



    // Listar todos los lugares de produccion con el estado activo (READ - Lista completa)
    public List<LugarProduccion> listarLugaresProduccion() {
        List<LugarProduccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM LUGAR_PRODUCCION";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LugarProduccion lugar = new LugarProduccion();

                lugar.setId_lugar(rs.getInt("ID_LUGAR"));
                lugar.setDepartamento(rs.getString("DEPARTAMENTO"));
                lugar.setMunicipio(rs.getString("MUNICIPIO"));
                lugar.setVereda(rs.getString("VEREDA"));
                lugar.setCantidad_maxima(rs.getInt("CANTIDAD_MAXIMA"));
                lugar.setId_productor(rs.getInt("ID_PRODUCTOR"));

                lista.add(lugar);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Actualizar un lugar (UPDATE)
    public boolean actualizarLugarProduccion(LugarProduccion lugar) {
        String sql = "UPDATE LUGAR_PRODUCCION SET " +
                     "DEPARTAMENTO = ?, " +
                     "MUNICIPIO = ?, " +
                     "VEREDA = ?, " +
                     "CANTIDAD_MAXIMA = ?, " +
                     "ID_PRODUCTOR = ? " +
                     "WHERE ID_LUGAR = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            // Campos obligatorios
            ps.setString(1, lugar.getDepartamento());
            ps.setString(2, lugar.getMunicipio());
            ps.setString(3, lugar.getVereda());
            ps.setInt(4, lugar.getCantidad_maxima());
            ps.setInt(5, lugar.getId_productor());

            // WHERE
            ps.setInt(6, lugar.getId_lugar());

            // Ejecutar actualización
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Eliminar un lugar (DELETE)
    public boolean eliminarLugarProduccion(int id_lugar) {
        String sql = "DELETE FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id_lugar);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean existeLugarPro(int idLugar) {
        String sql = "SELECT COUNT(*) FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
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
