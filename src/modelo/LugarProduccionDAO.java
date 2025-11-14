/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LugarProduccionDAO {
    private final Connection conexion;

    public LugarProduccionDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== Helpers & Validaciones ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private void validarReglas(LugarProduccion lp) {
        if (lp == null) throw new IllegalArgumentException("El lugar no puede ser null.");
        if (isBlank(lp.getDepartamento())) throw new IllegalArgumentException("departamento es obligatorio.");
        if (isBlank(lp.getMunicipio()))    throw new IllegalArgumentException("municipio es obligatorio.");
        if (isBlank(lp.getVereda()))       throw new IllegalArgumentException("vereda es obligatoria.");
        if (lp.getCantidad_maxima() <= 0)  throw new IllegalArgumentException("cantidad_maxima debe ser > 0.");
        if (lp.getId_productor() <= 0)     throw new IllegalArgumentException("id_productor debe ser > 0.");
    }

    /* ===================== SECUENCIA (función) ===================== */
    public int siguienteIdLugar() {
        final String sql = "{ ? = call fn_obtener_lugar_id() }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo ID (función): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== EXISTS / FKs (SQL directo) ===================== */
    public boolean existeLugar(int idLugar) {
        final String sql = "SELECT COUNT(*) FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean existeProductor(int idProductor) {
        final String sql = "SELECT COUNT(*) FROM PRODUCTOR WHERE ID_PRODUCTOR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== CREATE ===================== */
    public boolean insertarLugar(LugarProduccion lp) {
        validarReglas(lp);
        if (!existeProductor(lp.getId_productor()))
            throw new IllegalArgumentException("El id_productor " + lp.getId_productor() + " no existe.");

        final String sql = "{ call sp_insertar_lugar_id(?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, lp.getId_lugar());
            cs.setString(2, lp.getDepartamento().trim());
            cs.setString(3, lp.getMunicipio().trim());
            cs.setString(4, lp.getVereda().trim());
            cs.setInt(5, lp.getCantidad_maxima());
            cs.setInt(6, lp.getId_productor());
            cs.registerOutParameter(7, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(7) > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar lugar (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int insertarLugarAuto(LugarProduccion lp) {
        validarReglas(lp);
        if (!existeProductor(lp.getId_productor()))
            throw new IllegalArgumentException("El id_productor " + lp.getId_productor() + " no existe.");

        final String sql = "{ call sp_insertar_lugar(?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, lp.getDepartamento().trim());
            cs.setString(2, lp.getMunicipio().trim());
            cs.setString(3, lp.getVereda().trim());
            cs.setInt(4, lp.getCantidad_maxima());
            cs.setInt(5, lp.getId_productor());
            cs.registerOutParameter(6, Types.INTEGER); // p_id_generado
            cs.execute();

            int nuevoId = cs.getInt(6);
            lp.setId_lugar(nuevoId);
            return nuevoId;
        } catch (SQLException e) {
            System.err.println("❌ No se pudo insertar lugar (SP): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */
    public List<LugarProduccion> listarLugaresProduccion() {
        List<LugarProduccion> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_lugares(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<LugarProduccion> listarLugaresConProductor() {
        List<LugarProduccion> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_lugares_prod(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    LugarProduccion lp = mapRow(rs);
                    try { lp.setProductor_nombre(rs.getString("PRODUCTOR_NOMBRE")); } catch (Exception ignore) {}
                    lista.add(lp);
                }
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
        if (!existeLugar(lp.getId_lugar()))
            throw new IllegalArgumentException("No existe el lugar con ID " + lp.getId_lugar());
        if (!existeProductor(lp.getId_productor()))
            throw new IllegalArgumentException("El id_productor " + lp.getId_productor() + " no existe.");

        final String sql = "{ call sp_actualizar_lugar(?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, lp.getId_lugar());
            cs.setString(2, lp.getDepartamento().trim());
            cs.setString(3, lp.getMunicipio().trim());
            cs.setString(4, lp.getVereda().trim());
            cs.setInt(5, lp.getCantidad_maxima());
            cs.setInt(6, lp.getId_productor());
            cs.registerOutParameter(7, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(7) > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar lugar (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */
    public boolean eliminarLugar(int idLugar) {
        final String sql = "{ call sp_eliminar_lugar(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idLugar);
            cs.registerOutParameter(2, Types.INTEGER); // p_filas (0 si hay referencias)
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar lugar (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

