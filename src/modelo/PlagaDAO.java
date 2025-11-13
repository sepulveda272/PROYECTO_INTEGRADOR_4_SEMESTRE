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
    private final Connection conexion;

    public PlagaDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== SECUENCIA ===================== */
    /**
     * Obtiene el siguiente id_plaga desde la secuencia Oracle seq_plaga.
     */
    public int siguienteIdPlaga() {
        String sql = "SELECT seq_plaga.NEXTVAL FROM dual";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo siguiente id_plaga: " + e.getMessage());
        }
        return -1; // valor de error
    }

    /* ===================== INSERTAR ===================== */
    /**
     * Inserta una nueva plaga usando la secuencia para id_plaga.
     */
    public boolean insertarPlaga(Plaga plaga) {
        String sql = "INSERT INTO plagas (id_plaga, nombre_cientifica, nombre_comun, descripcion) "
                   + "VALUES (?, ?, ?, ?)";

        int nuevoId = siguienteIdPlaga();
        if (nuevoId == -1) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, nuevoId);
            ps.setString(2, plaga.getNombre_cientifico());
            ps.setString(3, plaga.getNombre_comun());
            ps.setString(4, plaga.getDescripcion());
            int filas = ps.executeUpdate();

            if (filas > 0) {
                plaga.setId_plaga(nuevoId); // actualizar el objeto en memoria
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error insertando plaga: " + e.getMessage());
        }
        return false;
    }

    /* ===================== ACTUALIZAR ===================== */
    /**
     * Actualiza los datos de una plaga existente.
     */
    public boolean actualizarPlaga(Plaga plaga) {
        String sql = "UPDATE plagas "
                   + "SET nombre_cientifica = ?, "
                   + "    nombre_comun = ?, "
                   + "    descripcion = ? "
                   + "WHERE id_plaga = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, plaga.getNombre_cientifico());
            ps.setString(2, plaga.getNombre_comun());
            ps.setString(3, plaga.getDescripcion());
            ps.setInt(4, plaga.getId_plaga());

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando plaga: " + e.getMessage());
            return false;
        }
    }

    /* ===================== VALIDAR REFERENCIAS ===================== */
    /**
     * Verifica si la plaga está siendo usada en la tabla afectado.
     * Si COUNT(*) > 0 significa que está referenciada y NO se debería eliminar.
     */
    public boolean tieneReferenciasEnAfectado(int idPlaga) {
        String sql = "SELECT COUNT(*) FROM afectado WHERE id_plaga = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPlaga);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cantidad = rs.getInt(1);
                    return cantidad > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verificando referencias en afectado: " + e.getMessage());
        }
        return false;
    }

    /* ===================== ELIMINAR ===================== */
    /**
     * Elimina una plaga únicamente si no está referenciada en la tabla afectado.
     */
    public boolean eliminarPlaga(int idPlaga) {
        // Primero verificamos si está referenciada
        if (tieneReferenciasEnAfectado(idPlaga)) {
            System.out.println("No se puede eliminar la plaga. Está referenciada en la tabla afectado.");
            return false;
        }

        String sql = "DELETE FROM plagas WHERE id_plaga = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPlaga);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando plaga: " + e.getMessage());
            return false;
        }
    }

    /* ===================== OBTENER POR ID ===================== */
    /**
     * Obtiene una plaga por su ID.
     */
    public Plaga obtenerPlagaPorId(int idPlaga) {
        String sql = "SELECT id_plaga, nombre_cientifica, nombre_comun, descripcion "
                   + "FROM plagas WHERE id_plaga = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPlaga);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Plaga plaga = new Plaga();
                    plaga.setId_plaga(rs.getInt("id_plaga"));
                    plaga.setNombre_cientifico(rs.getString("nombre_cientifica"));
                    plaga.setNombre_comun(rs.getString("nombre_comun"));
                    plaga.setDescripcion(rs.getString("descripcion"));
                    return plaga;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo plaga por id: " + e.getMessage());
        }
        return null;
    }

    /* ===================== LISTAR TODAS ===================== */
    /**
     * Lista todas las plagas registradas.
     */
    public List<Plaga> listarPlagas() {
        List<Plaga> lista = new ArrayList<>();
        String sql = "SELECT id_plaga, nombre_cientifica, nombre_comun, descripcion "
                   + "FROM plagas ORDER BY id_plaga";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plaga plaga = new Plaga();
                plaga.setId_plaga(rs.getInt("id_plaga"));
                plaga.setNombre_cientifico(rs.getString("nombre_cientifica"));
                plaga.setNombre_comun(rs.getString("nombre_comun"));
                plaga.setDescripcion(rs.getString("descripcion"));

                lista.add(plaga);
            }
        } catch (SQLException e) {
            System.err.println("Error listando plagas: " + e.getMessage());
        }
        return lista;
    }
}
