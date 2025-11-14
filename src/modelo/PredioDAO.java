/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PredioDAO {
    private final Connection conexion;

    public PredioDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== Helpers & Validaciones ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private void validarReglas(Predio p) {
        if (p == null) throw new IllegalArgumentException("El predio no puede ser null.");
        if (isBlank(p.getNombre_predio())) throw new IllegalArgumentException("nombre_predio es obligatorio.");
        if (p.getArea_total() <= 0) throw new IllegalArgumentException("area_total debe ser > 0.");
        if (isBlank(p.getNombre_propietario())) throw new IllegalArgumentException("nombre_propietario es obligatorio.");
        if (isBlank(p.getDireccion())) throw new IllegalArgumentException("direccion es obligatoria.");
        if (p.getCoordenadas_lat() < -90 || p.getCoordenadas_lat() > 90)
            throw new IllegalArgumentException("coordenadas_lat fuera de rango (-90..90).");
        if (p.getCoordenadas_lon() < -180 || p.getCoordenadas_lon() > 180)
            throw new IllegalArgumentException("coordenadas_lon fuera de rango (-180..180).");
        if (p.getId_lugar() <= 0) throw new IllegalArgumentException("id_lugar debe ser > 0.");
        if (p.getEstado() != null) {
            String e = p.getEstado().trim().toUpperCase();
            if (!e.equals("ACTIVO") && !e.equals("INACTIVO"))
                throw new IllegalArgumentException("estado debe ser ACTIVO o INACTIVO.");
        }
    }

    /* ===================== SECUENCIA (función) ===================== */
    public int siguienteIdPredio() {
        final String sql = "{ ? = call fn_next_predio_id() }";
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

    /* ===================== EXISTS / FKs (SPs) ===================== */
    public boolean existePredio(int idPredio) {
        final String sql = "{ call sp_existe_predio(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idPredio);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean existeLugar(int idLugar) {
        final String sql = "{ call sp_existe_lugar(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idLugar);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* ===================== CREATE ===================== */
    public boolean insertarPredio(Predio p) {
        validarReglas(p);
        if (!existeLugar(p.getId_lugar()))
            throw new IllegalArgumentException("El id_lugar " + p.getId_lugar() + " no existe.");

        final String sql = "{ call sp_insertar_predio_con_id(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, p.getId_predio());
            cs.setString(2, p.getNombre_predio().trim());
            cs.setDouble(3, p.getArea_total());
            cs.setString(4, p.getNombre_propietario().trim());
            cs.setString(5, p.getDireccion().trim());
            cs.setDouble(6, p.getCoordenadas_lat());
            cs.setDouble(7, p.getCoordenadas_lon());
            if (isBlank(p.getEstado())) cs.setNull(8, Types.VARCHAR); else cs.setString(8, p.getEstado().trim().toUpperCase());
            cs.setInt(9, p.getId_lugar());
            cs.registerOutParameter(10, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(10) > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar predio (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int insertarPredioAuto(Predio p) {
        validarReglas(p);
        if (!existeLugar(p.getId_lugar()))
            throw new IllegalArgumentException("El id_lugar " + p.getId_lugar() + " no existe.");

        final String sql = "{ call sp_insertar_predio(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, p.getNombre_predio().trim());
            cs.setDouble(2, p.getArea_total());
            cs.setString(3, p.getNombre_propietario().trim());
            cs.setString(4, p.getDireccion().trim());
            cs.setDouble(5, p.getCoordenadas_lat());
            cs.setDouble(6, p.getCoordenadas_lon());
            if (isBlank(p.getEstado())) cs.setNull(7, Types.VARCHAR); else cs.setString(7, p.getEstado().trim().toUpperCase());
            cs.setInt(8, p.getId_lugar());
            cs.registerOutParameter(9, Types.INTEGER); // p_id_generado
            cs.execute();

            int nuevoId = cs.getInt(9);
            p.setId_predio(nuevoId);
            return nuevoId;
        } catch (SQLException e) {
            System.err.println("Error al insertar predio auto (SP): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */
    public List<Predio> listarPrediosConLugar() {
        List<Predio> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_predios_con_lugar(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            // Si tu driver no soporta Types.REF_CURSOR, usa OracleTypes.CURSOR
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Predio p = mapRow(rs);
                    try { p.setLugar_label(rs.getString("LUGAR_LABEL")); } catch (Exception ignore) {}
                    lista.add(p);
                }
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

    /* ===================== UPDATE ===================== */
    public boolean actualizarPredio(Predio predio) {
        validarReglas(predio);
        final String sql = "{ call sp_actualizar_predio(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, predio.getId_predio());
            cs.setString(2, predio.getNombre_predio());
            cs.setDouble(3, predio.getArea_total());
            cs.setString(4, predio.getNombre_propietario());
            cs.setString(5, predio.getDireccion());
            cs.setDouble(6, predio.getCoordenadas_lat());
            cs.setDouble(7, predio.getCoordenadas_lon());
            cs.setInt(8, predio.getId_lugar());
            cs.setString(9, predio.getEstado());
            cs.registerOutParameter(10, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(10) > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar predio (SP): " + e.getMessage());
            return false;
        }
    }

    /* ===================== DELETE (soft) ===================== */
    public boolean eliminarPredio(int id_predio) {
        final String sql = "{ call sp_inactivar_predio(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, id_predio);
            cs.registerOutParameter(2, Types.INTEGER); // filas afectadas
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al inactivar predio (SP): " + e.getMessage());
            return false;
        }
    }
}

