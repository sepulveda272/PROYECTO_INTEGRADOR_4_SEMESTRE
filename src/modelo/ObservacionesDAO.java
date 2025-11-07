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
public class ObservacionesDAO {
    private Connection conexion;

    public ObservacionesDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== REGLAS DE NEGOCIO ===================== */

    private void validarReglas(Observaciones o) {
        if (o.getFecha_observacion() == null || o.getFecha_observacion().trim().isEmpty()) {
            throw new IllegalArgumentException("fecha_observacion es obligatoria");
        }
        if (o.getObservaciones() == null || o.getObservaciones().trim().isEmpty()) {
            throw new IllegalArgumentException("observaciones es obligatorio");
        }
        if (o.getObservaciones().length() > 1000) {
            throw new IllegalArgumentException("observaciones no puede superar 1000 caracteres");
        }
    }

    /* ===================== CREATE ===================== */

    public boolean insertar(Observaciones o) {
        validarReglas(o);

        String sql = "INSERT INTO OBSERVACIONES ("
                + "ID_OBSERVACION, FECHA_OBSERVACION, OBSERVACIONES, ID_INSPECCION, ID_FUNCIONARIO"
                + ") VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, o.getId_observacion());
            ps.setString(2, o.getFecha_observacion());        // DATE como String "yyyy-MM-dd"
            ps.setString(3, o.getObservaciones());
            ps.setInt(4, o.getId_inspeccion());
            ps.setInt(5, o.getId_funcionario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // para ver errores de PK/FK/CK
            return false;
        }
    }

    /* ===================== READ ===================== */

    public List<Observaciones> listar() {
        List<Observaciones> lista = new ArrayList<>();

        String sql = "SELECT ID_OBSERVACION, FECHA_OBSERVACION, OBSERVACIONES, "
                   + "ID_INSPECCION, ID_FUNCIONARIO "
                   + "FROM OBSERVACIONES ORDER BY ID_OBSERVACION";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Observaciones o = new Observaciones();

                o.setId_observacion(rs.getInt("ID_OBSERVACION"));

                Date f = rs.getDate("FECHA_OBSERVACION");
                o.setFecha_observacion(f != null ? f.toString() : null); // "yyyy-MM-dd"

                o.setObservaciones(rs.getString("OBSERVACIONES"));
                o.setId_inspeccion(rs.getInt("ID_INSPECCION"));
                o.setId_funcionario(rs.getInt("ID_FUNCIONARIO"));

                lista.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Opcional: listar por inspección (útil en la pantalla de detalles de una inspección)
    public List<Observaciones> listarPorInspeccion(int idInspeccion) {
        List<Observaciones> lista = new ArrayList<>();

        String sql = "SELECT ID_OBSERVACION, FECHA_OBSERVACION, OBSERVACIONES, "
                   + "ID_INSPECCION, ID_FUNCIONARIO "
                   + "FROM OBSERVACIONES "
                   + "WHERE ID_INSPECCION = ? "
                   + "ORDER BY FECHA_OBSERVACION";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInspeccion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Observaciones o = new Observaciones();

                    o.setId_observacion(rs.getInt("ID_OBSERVACION"));

                    Date f = rs.getDate("FECHA_OBSERVACION");
                    o.setFecha_observacion(f != null ? f.toString() : null);

                    o.setObservaciones(rs.getString("OBSERVACIONES"));
                    o.setId_inspeccion(rs.getInt("ID_INSPECCION"));
                    o.setId_funcionario(rs.getInt("ID_FUNCIONARIO"));

                    lista.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizar(Observaciones o) {
        validarReglas(o);

        String sql = "UPDATE OBSERVACIONES SET "
                + "FECHA_OBSERVACION = ?, "
                + "OBSERVACIONES = ?, "
                + "ID_INSPECCION = ?, "
                + "ID_FUNCIONARIO = ? "
                + "WHERE ID_OBSERVACION = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, o.getFecha_observacion());
            ps.setString(2, o.getObservaciones());
            ps.setInt(3, o.getId_inspeccion());
            ps.setInt(4, o.getId_funcionario());
            ps.setInt(5, o.getId_observacion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean eliminar(int idObservacion) {
        String sql = "DELETE FROM OBSERVACIONES WHERE ID_OBSERVACION = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idObservacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeObservacion(int idObservacion) {
        String sql = "SELECT COUNT(*) FROM OBSERVACIONES WHERE ID_OBSERVACION = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idObservacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeInspeccion(int idInspeccion) {
        String sql = "SELECT COUNT(*) FROM INSPECCION_FITOSANITARIA WHERE ID_INSPECCION = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInspeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeFuncionario(int idFuncionario) {
        String sql = "SELECT COUNT(*) FROM FUNCIONARIO_ICA WHERE ID_FUNCIONARIO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
