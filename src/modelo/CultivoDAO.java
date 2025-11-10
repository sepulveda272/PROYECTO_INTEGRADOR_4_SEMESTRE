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
public class CultivoDAO {
    private final Connection conexion;

    public CultivoDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== VALIDACIONES ===================== */

    private void validarReglas(Cultivo c) {
        if (c == null) throw new IllegalArgumentException("El cultivo no puede ser null.");
        if (isBlank(c.getNombre_especie())) {
            throw new IllegalArgumentException("nombre_especie es obligatorio.");
        }
        if (isBlank(c.getVariedad())) {
            throw new IllegalArgumentException("variedad es obligatoria.");
        }
        // descripcion es opcional
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /* ===================== SECUENCIA ===================== */

    /** Obtiene el siguiente ID desde la secuencia Oracle. */
    public int siguienteIdCultivo() {
        final String sql = "SELECT seq_cultivo.NEXTVAL FROM dual"; // <-- cambia si tu secuencia tiene otro nombre
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_cultivo: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // señal de error
    }

    /* ===================== CREATE ===================== */

    /** Inserta con ID provisto (no autogenerado). */
    public boolean insertarCultivo(Cultivo c) {
        validarReglas(c);

        final String sql = "INSERT INTO CULTIVO (ID_CULTIVO, NOMBRE_ESPECIE, VARIEDAD, DESCRIPCION) "
                         + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, c.getId_cultivo());
            ps.setString(2, c.getNombre_especie());
            ps.setString(3, c.getVariedad());
            if (isBlank(c.getDescripcion())) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, c.getDescripcion());
            }
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar cultivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta autogenerando ID desde la secuencia. Retorna el ID generado (>0) o -1 si falla. */
    public int insertarCultivoAuto(Cultivo c) {
        validarReglas(c);

        final int nuevoId = siguienteIdCultivo();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de la secuencia.");
            return -1;
        }
        c.setId_cultivo(nuevoId);
        boolean ok = insertarCultivo(c);
        return ok ? nuevoId : -1;
    }

    /* ===================== READ ===================== */

    public List<Cultivo> listarCultivos() {
        List<Cultivo> lista = new ArrayList<>();
        final String sql = "SELECT ID_CULTIVO, NOMBRE_ESPECIE, VARIEDAD, DESCRIPCION "
                         + "FROM CULTIVO "
                         + "ORDER BY ID_CULTIVO";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Búsqueda puntual por ID. Retorna null si no existe. */
    public Cultivo buscarPorId(int idCultivo) {
        final String sql = "SELECT ID_CULTIVO, NOMBRE_ESPECIE, VARIEDAD, DESCRIPCION "
                         + "FROM CULTIVO WHERE ID_CULTIVO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCultivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Cultivo mapRow(ResultSet rs) throws SQLException {
        Cultivo c = new Cultivo();
        c.setId_cultivo(rs.getInt("ID_CULTIVO"));
        c.setNombre_especie(rs.getString("NOMBRE_ESPECIE"));
        c.setVariedad(rs.getString("VARIEDAD"));
        c.setDescripcion(rs.getString("DESCRIPCION"));
        return c;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarCultivo(Cultivo c) {
        validarReglas(c);

        final String sql = "UPDATE CULTIVO SET "
                         + "NOMBRE_ESPECIE = ?, "
                         + "VARIEDAD = ?, "
                         + "DESCRIPCION = ? "
                         + "WHERE ID_CULTIVO = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, c.getNombre_especie());
            ps.setString(2, c.getVariedad());
            if (isBlank(c.getDescripcion())) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, c.getDescripcion());
            }
            ps.setInt(4, c.getId_cultivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cultivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean eliminarCultivo(int idCultivo) {
        final String sql = "DELETE FROM CULTIVO WHERE ID_CULTIVO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCultivo);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            // FK en tablas dependientes (LOTE, etc.)
            System.err.println("❌ No se puede eliminar el cultivo: está referenciado por otras tablas. " + fk.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cultivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeCultivo(int idCultivo) {
        final String sql = "SELECT COUNT(*) FROM CULTIVO WHERE ID_CULTIVO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCultivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
