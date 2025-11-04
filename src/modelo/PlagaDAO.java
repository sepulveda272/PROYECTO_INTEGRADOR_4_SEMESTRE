/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ADMIN
 */
public class PlagaDAO {
    private Connection conexion;

    public PlagaDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection(); // NO cierres esta conexión aquí
    }

    // CREATE: insertar una plaga (con ID explícito, igual que tu otro DAO)
    public boolean insertarPlaga(Plaga plaga) {
        String sql = "INSERT INTO PLAGAS " +
                     "(ID_PLAGA, NOMBRE_CIENTIFICA, NOMBRE_COMUN, DESCRIPCION) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, plaga.getId_plaga());
            ps.setString(2, plaga.getNombre_cientifico());
            ps.setString(3, plaga.getNombre_comun());

            if (plaga.getDescripcion() == null || plaga.getDescripcion().trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, plaga.getDescripcion());
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ: listar todas las plagas
    public List<Plaga> listarPlagas() {
        List<Plaga> lista = new ArrayList<>();
        String sql = "SELECT ID_PLAGA, NOMBRE_CIENTIFICA, NOMBRE_COMUN, DESCRIPCION FROM PLAGAS ORDER BY ID_PLAGA";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plaga p = new Plaga();
                p.setId_plaga(rs.getInt("ID_PLAGA"));
                p.setNombre_cientifico(rs.getString("NOMBRE_CIENTIFICA"));
                p.setNombre_comun(rs.getString("NOMBRE_COMUN"));
                p.setDescripcion(rs.getString("DESCRIPCION"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // READ: buscar por ID
    /*public Plaga buscarPorId(int idPlaga) {
        String sql = "SELECT ID_PLAGA, NOMBRE_CIENTIFICA, NOMBRE_COMUN, DESCRIPCION FROM PLAGAS WHERE ID_PLAGA = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPlaga);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Plaga p = new Plaga();
                    p.setId_plaga(rs.getInt("ID_PLAGA"));
                    p.setNombre_cientifico(rs.getString("NOMBRE_CIENTIFICO"));
                    p.setNombre_comun(rs.getString("NOMBRE_COMUN"));
                    p.setDescripcion(rs.getString("DESCRIPCION"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    // READ: buscar por nombre científico (exacto)
    /*public Plaga buscarPorNombreCientifico(String nombreCientifico) {
        String sql = "SELECT ID_PLAGA, NOMBRE_CIENTIFICA, NOMBRE_COMUN, DESCRIPCION FROM PLAGAS WHERE UPPER(NOMBRE_CIENTIFICA) = UPPER(?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreCientifico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Plaga p = new Plaga();
                    p.setId_plaga(rs.getInt("ID_PLAGA"));
                    p.setNombre_cientifico(rs.getString("NOMBRE_CIENTIFICA"));
                    p.setNombre_comun(rs.getString("NOMBRE_COMUN"));
                    p.setDescripcion(rs.getString("DESCRIPCION"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    // UPDATE
    public boolean actualizarPlaga(Plaga plaga) {
        String sql = "UPDATE PLAGAS SET " +
                     "NOMBRE_CIENTIFICA = ?, " +
                     "NOMBRE_COMUN = ?, " +
                     "DESCRIPCION = ? " +
                     "WHERE ID_PLAGA = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, plaga.getNombre_cientifico());
            ps.setString(2, plaga.getNombre_comun());

            if (plaga.getDescripcion() == null || plaga.getDescripcion().trim().isEmpty()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, plaga.getDescripcion());
            }

            ps.setInt(4, plaga.getId_plaga());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean eliminarPlaga(int idPlaga) {
        String sql = "DELETE FROM PLAGAS WHERE ID_PLAGA = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPlaga);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // EXISTS por ID
    public boolean existePlaga(int idPlaga) {
        String sql = "SELECT COUNT(*) FROM PLAGAS WHERE ID_PLAGA = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPlaga);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // EXISTS por nombre científico (evitar duplicados)
    public boolean existeNombreCientifico(String nombreCientifico) {
        String sql = "SELECT COUNT(*) FROM PLAGAS WHERE UPPER(NOMBRE_CIENTIFICA) = UPPER(?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreCientifico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
