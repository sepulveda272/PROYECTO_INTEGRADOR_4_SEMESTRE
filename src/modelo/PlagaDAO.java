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

    /* ===================== INSERTAR (SP + TRIGGER) ===================== */

    /**
     * Inserta una nueva plaga usando el procedimiento almacenado pr_insertar_plaga.
     * El id_plaga se genera en la BD mediante fn_generar_plaga + tr_plaga_bi.
     */
    public boolean insertarPlaga(Plaga plaga) {
        final String sql = "{ call pr_insertar_plaga(?,?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setString(1, plaga.getNombre_cientifico());
            cs.setString(2, plaga.getNombre_comun());

            if (plaga.getDescripcion() != null && !plaga.getDescripcion().trim().isEmpty()) {
                cs.setString(3, plaga.getDescripcion().trim());
            } else {
                cs.setNull(3, Types.VARCHAR);
            }

            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();

            int nuevoId = cs.getInt(4);
            plaga.setId_plaga(nuevoId);
            return true;

        } catch (SQLException e) {
            System.err.println("Error insertando plaga (pr_insertar_plaga): " + e.getMessage());
            return false;
        }
    }

    /* ===================== ACTUALIZAR (SP) ===================== */

    /**
     * Actualiza los datos de una plaga existente mediante pr_actualizar_plaga.
     */
    public boolean actualizarPlaga(Plaga plaga) {
        final String sql = "{ call pr_actualizar_plaga(?,?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setInt(1, plaga.getId_plaga());
            cs.setString(2, plaga.getNombre_cientifico());
            cs.setString(3, plaga.getNombre_comun());

            if (plaga.getDescripcion() != null && !plaga.getDescripcion().trim().isEmpty()) {
                cs.setString(4, plaga.getDescripcion().trim());
            } else {
                cs.setNull(4, Types.VARCHAR);
            }

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error actualizando plaga (pr_actualizar_plaga): " + e.getMessage());
            return false;
        }
    }

    /* ===================== ELIMINAR (SP) ===================== */

    /**
     * Elimina una plaga únicamente si no está referenciada en la tabla afectado.
     */
    public boolean eliminarPlaga(int idPlaga) {
        // Primero verificamos si está referenciada
        if (tieneReferenciasEnAfectado(idPlaga)) {
            System.out.println("No se puede eliminar la plaga. Está referenciada en la tabla afectado.");
            return false;
        }

        final String sql = "{ call pr_eliminar_plaga(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idPlaga);
            cs.execute();
            return true;

        } catch (SQLIntegrityConstraintViolationException fk) {
            System.err.println("No se puede eliminar la plaga (ID " + idPlaga +
                    "): está referenciada por otras tablas. " + fk.getMessage());
            return false;

        } catch (SQLException e) {
            System.err.println("Error eliminando plaga (pr_eliminar_plaga): " + e.getMessage());
            return false;
        }
    }

    /* ===================== OBTENER POR ID ===================== */

    /**
     * Obtiene una plaga por su ID.
     */
    public Plaga obtenerPlagaPorId(int idPlaga) {
        String sql = "SELECT id_plaga, nombre_cientifica, nombre_comun, descripcion " +
                     "FROM plagas WHERE id_plaga = ?";

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
        String sql = "SELECT id_plaga, nombre_cientifica, nombre_comun, descripcion " +
                     "FROM plagas ORDER BY id_plaga";

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