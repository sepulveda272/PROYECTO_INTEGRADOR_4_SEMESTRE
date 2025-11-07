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
public class LoteDAO {
    private Connection conexion;

    public LoteDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== REGLAS DE NEGOCIO ===================== */

    private void validarReglas(Lote l) {
        if (l.getArea_total() <= 0) {
            throw new IllegalArgumentException("area_total debe ser > 0");
        }
        if (l.getArea_siembra() < 0) {
            throw new IllegalArgumentException("area_siembra debe ser >= 0");
        }
        if (l.getArea_siembra() > l.getArea_total()) {
            throw new IllegalArgumentException("area_siembra no puede ser mayor que area_total");
        }
        if (l.getEstado_fenologico() == null || l.getEstado_fenologico().trim().isEmpty()) {
            throw new IllegalArgumentException("estado_fenologico es obligatorio");
        }
        if (l.getFecha_siembra() == null || l.getFecha_siembra().trim().isEmpty()) {
            throw new IllegalArgumentException("fecha_siembra es obligatoria");
        }
    }

    /* ===================== CREATE ===================== */

    public boolean insertar(Lote l) {
        validarReglas(l);

        String sql = "INSERT INTO LOTE ("
                + "NUMERO_LOTE, AREA_TOTAL, AREA_SIEMBRA, ESTADO_FENOLOGICO, "
                + "FECHA_SIEMBRA, FECHA_ELIMINACION, ID_CULTIVO, ID_LUGAR"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, l.getNumero_lote());
            ps.setDouble(2, l.getArea_total());
            ps.setDouble(3, l.getArea_siembra());
            ps.setString(4, l.getEstado_fenologico());
            // Manejo como String (igual que en tu ejemplo de InspeccionFitosanitaria)
            ps.setString(5, l.getFecha_siembra());

            if (l.getFecha_eliminacion() != null && !l.getFecha_eliminacion().trim().isEmpty()) {
                ps.setString(6, l.getFecha_eliminacion());
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.setInt(7, l.getId_cultivo());
            ps.setInt(8, l.getId_lugar());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // para ver errores de PK/FK/CK
            return false;
        }
    }

    /* ===================== READ ===================== */

    public List<Lote> listar() {
        List<Lote> lista = new ArrayList<>();

        String sql = "SELECT NUMERO_LOTE, AREA_TOTAL, AREA_SIEMBRA, "
                + "ESTADO_FENOLOGICO, FECHA_SIEMBRA, FECHA_ELIMINACION, "
                + "ID_CULTIVO, ID_LUGAR "
                + "FROM LOTE ORDER BY NUMERO_LOTE";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lote l = new Lote();

                l.setNumero_lote(rs.getInt("NUMERO_LOTE"));
                l.setArea_total(rs.getDouble("AREA_TOTAL"));
                l.setArea_siembra(rs.getDouble("AREA_SIEMBRA"));
                l.setEstado_fenologico(rs.getString("ESTADO_FENOLOGICO"));

                Date fSiembra = rs.getDate("FECHA_SIEMBRA");
                l.setFecha_siembra(fSiembra != null ? fSiembra.toString() : null); // "yyyy-MM-dd"

                Date fEliminacion = rs.getDate("FECHA_ELIMINACION");
                l.setFecha_eliminacion(fEliminacion != null ? fEliminacion.toString() : null);

                l.setId_cultivo(rs.getInt("ID_CULTIVO"));
                l.setId_lugar(rs.getInt("ID_LUGAR"));

                lista.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizar(Lote l) {
        validarReglas(l);

        String sql = "UPDATE LOTE SET "
                + "AREA_TOTAL = ?, "
                + "AREA_SIEMBRA = ?, "
                + "ESTADO_FENOLOGICO = ?, "
                + "FECHA_SIEMBRA = ?, "
                + "FECHA_ELIMINACION = ?, "
                + "ID_CULTIVO = ?, "
                + "ID_LUGAR = ? "
                + "WHERE NUMERO_LOTE = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, l.getArea_total());
            ps.setDouble(2, l.getArea_siembra());
            ps.setString(3, l.getEstado_fenologico());
            ps.setString(4, l.getFecha_siembra());

            if (l.getFecha_eliminacion() != null && !l.getFecha_eliminacion().trim().isEmpty()) {
                ps.setString(5, l.getFecha_eliminacion());
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, l.getId_cultivo());
            ps.setInt(7, l.getId_lugar());
            ps.setInt(8, l.getNumero_lote());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean eliminar(int numeroLote) {
        String sql = "DELETE FROM LOTE WHERE NUMERO_LOTE = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroLote);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeLote(int numeroLote) {
        String sql = "SELECT COUNT(*) FROM LOTE WHERE NUMERO_LOTE = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroLote);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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

    public boolean existeLugarProduccion(int idLugar) {
        String sql = "SELECT COUNT(*) FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
