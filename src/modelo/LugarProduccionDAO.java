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
    private final Connection conexion;

    public LugarProduccionDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== Helpers ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private void validarReglas(LugarProduccion lp) {
        if (lp == null) throw new IllegalArgumentException("El lugar no puede ser null.");
        if (isBlank(lp.getDepartamento())) throw new IllegalArgumentException("departamento es obligatorio.");
        if (isBlank(lp.getMunicipio()))    throw new IllegalArgumentException("municipio es obligatorio.");
        if (isBlank(lp.getVereda()))       throw new IllegalArgumentException("vereda es obligatoria.");
        if (lp.getCantidad_maxima() <= 0)  throw new IllegalArgumentException("cantidad_maxima debe ser > 0.");
        if (lp.getId_productor() <= 0)     throw new IllegalArgumentException("id_productor debe ser > 0.");
    }

    /* ===================== SECUENCIA ===================== */

    /** Obtiene el siguiente id_lugar desde la secuencia Oracle. */
    public int siguienteIdLugar() {
        final String sql = "SELECT seq_lugar.NEXTVAL FROM dual";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_lugar: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== EXISTS / FKs ===================== */

    public boolean existeLugar(int idLugar) {
        final String sql = "SELECT COUNT(*) FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean existeProductor(int idProductor) {
        final String sql = "SELECT COUNT(*) FROM PRODUCTOR WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== CREATE ===================== */

    /** Inserta con ID provisto. */
    public boolean insertarLugar(LugarProduccion lp) {
        validarReglas(lp);

        if (!existeProductor(lp.getId_productor())) {
            throw new IllegalArgumentException("El id_productor " + lp.getId_productor() + " no existe.");
        }

        final String sql = "INSERT INTO LUGAR_PRODUCCION (" +
                "ID_LUGAR, DEPARTAMENTO, MUNICIPIO, VEREDA, CANTIDAD_MAXIMA, ID_PRODUCTOR) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, lp.getId_lugar());
            ps.setString(2, lp.getDepartamento().trim());
            ps.setString(3, lp.getMunicipio().trim());
            ps.setString(4, lp.getVereda().trim());
            ps.setInt(5, lp.getCantidad_maxima());
            ps.setInt(6, lp.getId_productor());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar lugar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta autogenerando ID por secuencia. Retorna el ID generado (>0) o -1 si falla. */
    public int insertarLugarAuto(LugarProduccion lp) {
        validarReglas(lp);

        if (!existeProductor(lp.getId_productor())) {
            throw new IllegalArgumentException("El id_productor " + lp.getId_productor() + " no existe.");
        }

        final int nuevoId = siguienteIdLugar();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de seq_lugar.");
            return -1;
        }
        lp.setId_lugar(nuevoId);
        boolean ok = insertarLugar(lp);
        return ok ? nuevoId : -1;
    }

    /* ===================== READ ===================== */

    public List<LugarProduccion> listarLugaresProduccion() {
        List<LugarProduccion> lista = new ArrayList<>();
        final String sql = "SELECT ID_LUGAR, DEPARTAMENTO, MUNICIPIO, VEREDA, CANTIDAD_MAXIMA, ID_PRODUCTOR " +
                           "FROM LUGAR_PRODUCCION ORDER BY ID_LUGAR";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Lista con el nombre completo del productor (útil para la vista/tabla). */
    public List<LugarProduccion> listarLugaresConProductor() {
        List<LugarProduccion> lista = new ArrayList<>();
        final String sql =
            "SELECT lp.ID_LUGAR, lp.DEPARTAMENTO, lp.MUNICIPIO, lp.VEREDA, lp.CANTIDAD_MAXIMA, lp.ID_PRODUCTOR, " +
            "       REGEXP_REPLACE(TRIM( NVL(p.PRIMER_NOMBRE,'') || ' ' || NVL(p.SEGUNDO_NOMBRE,'') || ' ' || " +
            "                               NVL(p.PRIMER_APELLIDO,'') || ' ' || NVL(p.SEGUNDO_APELLIDO,'') ), ' +',' ') AS PRODUCTOR_NOMBRE " +
            "FROM LUGAR_PRODUCCION lp " +
            "JOIN PRODUCTOR p ON p.ID_PRODUCTOR = lp.ID_PRODUCTOR " +
            "ORDER BY lp.ID_LUGAR";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LugarProduccion lp = new LugarProduccion();
                lp.setId_lugar(rs.getInt("ID_LUGAR"));
                lp.setDepartamento(rs.getString("DEPARTAMENTO"));
                lp.setMunicipio(rs.getString("MUNICIPIO"));
                lp.setVereda(rs.getString("VEREDA"));
                lp.setCantidad_maxima(rs.getInt("CANTIDAD_MAXIMA"));
                lp.setId_productor(rs.getInt("ID_PRODUCTOR"));
                // Campo opcional para la vista:
                try {
                    lp.setProductor_nombre(rs.getString("PRODUCTOR_NOMBRE"));
                } catch (Exception ignore) {}
                lista.add(lp);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private LugarProduccion mapRow(ResultSet rs) throws SQLException {
        LugarProduccion lp = new LugarProduccion();
        lp.setId_lugar(rs.getInt("ID_LUGAR"));
        lp.setDepartamento(rs.getString("DEPARTAMENTO"));
        lp.setMunicipio(rs.getString("MUNICIPIO"));
        lp.setVereda(rs.getString("VEREDA"));
        lp.setCantidad_maxima(rs.getInt("CANTIDAD_MAXIMA"));
        lp.setId_productor(rs.getInt("ID_PRODUCTOR"));
        return lp;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarLugar(LugarProduccion lp) {
        validarReglas(lp);
        if (!existeLugar(lp.getId_lugar())) {
            throw new IllegalArgumentException("No existe el lugar con ID " + lp.getId_lugar());
        }
        if (!existeProductor(lp.getId_productor())) {
            throw new IllegalArgumentException("El id_productor " + lp.getId_productor() + " no existe.");
        }

        final String sql = "UPDATE LUGAR_PRODUCCION SET " +
                "DEPARTAMENTO = ?, " +
                "MUNICIPIO = ?, " +
                "VEREDA = ?, " +
                "CANTIDAD_MAXIMA = ?, " +
                "ID_PRODUCTOR = ? " +
                "WHERE ID_LUGAR = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, lp.getDepartamento().trim());
            ps.setString(2, lp.getMunicipio().trim());
            ps.setString(3, lp.getVereda().trim());
            ps.setInt(4, lp.getCantidad_maxima());
            ps.setInt(5, lp.getId_productor());
            ps.setInt(6, lp.getId_lugar());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar lugar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    /** Elimina físicamente. Falla si hay FKs (p.ej., LOTE.ID_LUGAR). */
    public boolean eliminarLugar(int idLugar) {
        final String sql = "DELETE FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException fk) {
            System.err.println("❌ No se puede eliminar el lugar: está referenciado por otras tablas. " + fk.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error al eliminar lugar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
