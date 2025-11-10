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
    
    /* ===================== Helpers & Validaciones ===================== */

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private void validarReglas(Predio p) {
        if (p == null) throw new IllegalArgumentException("El predio no puede ser null.");

        if (isBlank(p.getNombre_predio()))     throw new IllegalArgumentException("nombre_predio es obligatorio.");
        if (p.getArea_total() <= 0)            throw new IllegalArgumentException("area_total debe ser > 0.");
        if (isBlank(p.getNombre_propietario()))throw new IllegalArgumentException("nombre_propietario es obligatorio.");
        if (isBlank(p.getDireccion()))         throw new IllegalArgumentException("direccion es obligatoria.");

        // coordenadas: rango básico
        if (p.getCoordenadas_lat() < -90 || p.getCoordenadas_lat() > 90)
            throw new IllegalArgumentException("coordenadas_lat fuera de rango (-90..90).");
        if (p.getCoordenadas_lon() < -180 || p.getCoordenadas_lon() > 180)
            throw new IllegalArgumentException("coordenadas_lon fuera de rango (-180..180).");

        if (p.getId_lugar() <= 0)
            throw new IllegalArgumentException("id_lugar debe ser > 0.");

        // estado: ACTIVO/INACTIVO (si viene nulo lo permite y la BD pone DEFAULT 'ACTIVO')
        if (p.getEstado() != null) {
            String e = p.getEstado().trim().toUpperCase();
            if (!e.equals("ACTIVO") && !e.equals("INACTIVO")) {
                throw new IllegalArgumentException("estado debe ser ACTIVO o INACTIVO.");
            }
        }
    }
    
    /* ===================== SECUENCIA ===================== */

    /** Obtiene el siguiente id_predio desde la secuencia Oracle. */
    public int siguienteIdPredio() {
        final String sql = "SELECT seq_predio.NEXTVAL FROM dual";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_predio: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /* ===================== EXISTS / FKs ===================== */

    public boolean existePredio(int idPredio) {
        final String sql = "SELECT COUNT(*) FROM PREDIO WHERE ID_PREDIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPredio);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean existeLugar(int idLugar) {
        final String sql = "SELECT COUNT(*) FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    
    /* ===================== CREATE ===================== */

    /** Inserta con ID provisto. */
    public boolean insertarPredio(Predio p) {
        validarReglas(p);
        if (!existeLugar(p.getId_lugar()))
            throw new IllegalArgumentException("El id_lugar " + p.getId_lugar() + " no existe.");

        final String sql = "INSERT INTO PREDIO (" +
                "ID_PREDIO, NOMBRE_PREDIO, AREA_TOTAL, NOMBRE_PROPIETARIO, DIRECCION, " +
                "COORDENADAS_LAT, COORDENADAS_LON, ESTADO, ID_LUGAR) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NVL(?, 'ACTIVO'), ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, p.getId_predio());
            ps.setString(2, p.getNombre_predio().trim());
            ps.setDouble(3, p.getArea_total());
            ps.setString(4, p.getNombre_propietario().trim());
            ps.setString(5, p.getDireccion().trim());
            ps.setDouble(6, p.getCoordenadas_lat());
            ps.setDouble(7, p.getCoordenadas_lon());
            if (isBlank(p.getEstado())) ps.setNull(8, Types.VARCHAR); else ps.setString(8, p.getEstado().trim().toUpperCase());
            ps.setInt(9, p.getId_lugar());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar predio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta autogenerando ID por secuencia. Retorna el ID generado (>0) o -1 si falla. */
    public int insertarPredioAuto(Predio p) {
        validarReglas(p);
        if (!existeLugar(p.getId_lugar()))
            throw new IllegalArgumentException("El id_lugar " + p.getId_lugar() + " no existe.");

        final int nuevoId = siguienteIdPredio();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de seq_predio.");
            return -1;
        }
        p.setId_predio(nuevoId);
        boolean ok = insertarPredio(p);
        return ok ? nuevoId : -1;
    }

    /** Devuelve predios + etiqueta del lugar (Depto - Municipio (Vereda)). Útil para la vista. */
    public List<Predio> listarPrediosConLugar() {
        List<Predio> lista = new ArrayList<>();
        final String sql =
            "SELECT p.ID_PREDIO, p.NOMBRE_PREDIO, p.AREA_TOTAL, p.NOMBRE_PROPIETARIO, p.DIRECCION, " +
            "       p.COORDENADAS_LAT, p.COORDENADAS_LON, p.ESTADO, p.ID_LUGAR, " +
            "       (lp.DEPARTAMENTO || ' - ' || lp.MUNICIPIO || ' (' || lp.VEREDA || ')') AS LUGAR_LABEL " +
            "FROM PREDIO p " +
            "JOIN LUGAR_PRODUCCION lp ON lp.ID_LUGAR = p.ID_LUGAR " +
            "ORDER BY p.ID_PREDIO";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Predio p = mapRow(rs);
                try { p.setLugar_label(rs.getString("LUGAR_LABEL")); } catch (Exception ignore) {}
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
    
    private Predio mapRow(ResultSet rs) throws SQLException {
        Predio p = new Predio();
        p.setId_predio(rs.getInt("ID_PREDIO"));
        p.setNombre_predio(rs.getString("NOMBRE_PREDIO"));
        p.setArea_total(rs.getDouble("AREA_TOTAL"));
        p.setNombre_propietario(rs.getString("NOMBRE_PROPIETARIO"));
        p.setDireccion(rs.getString("DIRECCION"));
        p.setCoordenadas_lat(rs.getDouble("COORDENADAS_LAT"));
        p.setCoordenadas_lon(rs.getDouble("COORDENADAS_LON"));
        p.setEstado(rs.getString("ESTADO"));
        p.setId_lugar(rs.getInt("ID_LUGAR"));
        return p;
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
