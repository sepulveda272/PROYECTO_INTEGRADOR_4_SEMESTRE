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
public class InspeccionFitosanitariaDAO {
    private Connection conexion;
    private static final String TABLE = "INSPECCION_FITOSANITARIA";

    private void validarReglas(InspeccionFitosanitaria i) {
        if (i.getPlantas_revisadas() < 0) {
            throw new IllegalArgumentException("plantas_revisadas debe ser >= 0");
        }
        if (i.getPlantas_afectadas() > i.getPlantas_revisadas()) {
            throw new IllegalArgumentException("plantas_afectadas no puede ser mayor que plantas_revisadas");
        }
        if (i.getNivel_alerta() == null || i.getNivel_alerta().trim().isEmpty()) {
            throw new IllegalArgumentException("nivel_alerta es obligatorio");
        }
    }

    /* ===================== CREATE ===================== */

    public boolean insertar(InspeccionFitosanitaria i) {
        validarReglas(i);

        String sql = "INSERT INTO INSPECCION_FITOSANITARIA (ID_INSPECCION, PLANTAS_REVISADAS, PLANTAS_AFECTADAS, FECHA_INSPECCION, NIVEL_ALERTA, NUMERO_LOTE, ID_TECNICO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, i.getId_inspeccion());
            ps.setInt(2, i.getPlantas_revisadas());
            ps.setInt(3, i.getPlantas_afectadas());
            ps.setString(4, i.getFecha_inspeccion()); // DATE
            ps.setString(5, i.getNivel_alerta());
            ps.setInt(6, i.getId_lote());     // en tu POJO: Id_lote -> columna NUMERO_LOTE
            ps.setInt(7, i.getId_tecnico());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // para ver FK/PK/CK si fallan
            return false;
        }
    }

    /* ===================== READ ===================== */

    public List<InspeccionFitosanitaria> listar() {
        List<InspeccionFitosanitaria> lista = new ArrayList<>();

        String sql = "SELECT ID_INSPECCION, PLANTAS_REVISADAS, PLANTAS_AFECTADAS, " +
                "FECHA_INSPECCION, NIVEL_ALERTA, NUMERO_LOTE, ID_TECNICO " +
                "FROM INSPECCION_FITOSANITARIA ORDER BY ID_INSPECCION";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InspeccionFitosanitaria i = new InspeccionFitosanitaria();
                i.setId_inspeccion(rs.getInt("ID_INSPECCION"));
                i.setPlantas_revisadas(rs.getInt("PLANTAS_REVISADAS"));
                i.setPlantas_afectadas(rs.getInt("PLANTAS_AFECTADAS"));

                Date f = rs.getDate("FECHA_INSPECCION");
                i.setFecha_inspeccion(f != null ? f.toString() : null); // "yyyy-MM-dd"

                i.setNivel_alerta(rs.getString("NIVEL_ALERTA"));
                i.setId_lote(rs.getInt("NUMERO_LOTE"));
                i.setId_tecnico(rs.getInt("ID_TECNICO"));

                lista.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizar(InspeccionFitosanitaria i) {
        validarReglas(i);

        String sql = "UPDATE INSPECCION_FITOSANITARIA SET " +
                "PLANTAS_REVISADAS = ?, " +
                "PLANTAS_AFECTADAS = ?, " +
                "FECHA_INSPECCION = ?, " +
                "NIVEL_ALERTA = ?, " +
                "NUMERO_LOTE = ?, " +
                "ID_TECNICO = ? " +
                "WHERE ID_INSPECCION = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, i.getPlantas_revisadas());
            ps.setInt(2, i.getPlantas_afectadas());
            ps.setString(3, i.getFecha_inspeccion());
            ps.setString(4, i.getNivel_alerta());
            ps.setInt(5, i.getId_lote());
            ps.setInt(6, i.getId_tecnico());
            ps.setInt(7, i.getId_inspeccion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean eliminar(int idInspeccion) {
        String sql = "DELETE FROM INSPECCION_FITOSANITARIA WHERE ID_INSPECCION = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInspeccion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeInspeccion(int idInspeccion) {
        String sql = "SELECT COUNT(*) FROM INSPECCION_FITOSANITARIA WHERE ID_INSPECCION = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInspeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existeLote(int numeroLote) {
        String sql = "SELECT COUNT(*) FROM LOTE WHERE NUMERO_LOTE = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroLote);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existeTecnico(int idTecnico) {
        String sql = "SELECT COUNT(*) FROM TECNICO_OFICIAL WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idTecnico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
