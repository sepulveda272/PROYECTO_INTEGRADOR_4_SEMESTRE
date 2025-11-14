/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CultivoDAO {
    private final Connection conexion;

    public CultivoDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== VALIDACIONES ===================== */
    private void validarReglas(Cultivo c) {
        if (c == null) throw new IllegalArgumentException("El cultivo no puede ser null.");
        if (isBlank(c.getNombre_especie())) throw new IllegalArgumentException("nombre_especie es obligatorio.");
        if (isBlank(c.getVariedad()))       throw new IllegalArgumentException("variedad es obligatoria.");
        // descripcion es opcional
    }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /* ===================== SECUENCIA (opcional) ===================== */
    // Si quieres conservar este helper, que sea vía FUNCIÓN (no SELECT NEXTVAL)
    public int siguienteIdCultivo() {
        final String sql = "{ ? = call fn_next_cultivo_id() }";
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

    /* ===================== CREATE ===================== */

    // Inserta con ID provisto (respeta tu método original)
    public boolean insertarCultivo(Cultivo c) {
        validarReglas(c);
        final String sql = "{ call sp_insertar_cultivo_con_id(?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, c.getId_cultivo());
            cs.setString(2, c.getNombre_especie());
            cs.setString(3, c.getVariedad());
            if (isBlank(c.getDescripcion())) cs.setNull(4, Types.VARCHAR); else cs.setString(4, c.getDescripcion());
            cs.registerOutParameter(5, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(5) > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar cultivo (SP con ID): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Inserta auto (devuelve ID vía OUT)
    public int insertarCultivoAuto(Cultivo c) {
        validarReglas(c);
        final String sql = "{ call sp_insertar_cultivo(?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, c.getNombre_especie());
            cs.setString(2, c.getVariedad());
            if (isBlank(c.getDescripcion())) cs.setNull(3, Types.VARCHAR); else cs.setString(3, c.getDescripcion());
            cs.registerOutParameter(4, Types.INTEGER); // p_id_generado
            cs.execute();
            int id = cs.getInt(4);
            c.setId_cultivo(id);
            return id;
        } catch (SQLException e) {
            System.err.println("Error al insertar cultivo (SP auto): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */

    public List<Cultivo> listarCultivos() {
        List<Cultivo> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_cultivos(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando cultivos (SP): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Cultivo buscarPorId(int idCultivo) {
        final String sql = "{ call sp_buscar_cultivo_por_id(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idCultivo);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando cultivo por ID (SP): " + e.getMessage());
            e.printStackTrace();
        }
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
        final String sql = "{ call sp_actualizar_cultivo(?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, c.getId_cultivo());
            cs.setString(2, c.getNombre_especie());
            cs.setString(3, c.getVariedad());
            if (isBlank(c.getDescripcion())) cs.setNull(4, Types.VARCHAR); else cs.setString(4, c.getDescripcion());
            cs.registerOutParameter(5, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(5) > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cultivo (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean eliminarCultivo(int idCultivo) {
        final String sql = "{ call sp_eliminar_cultivo(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idCultivo);
            cs.registerOutParameter(2, Types.INTEGER); // p_code
            cs.registerOutParameter(3, Types.VARCHAR); // p_msg
            cs.execute();
            int code = cs.getInt(2);
            String msg = cs.getString(3);
            if (code == 0) return true;
            System.err.println("❌ " + msg);
            return false;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cultivo (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== EXISTS ===================== */

    public boolean existeCultivo(int idCultivo) {
        final String sql = "{ call sp_existe_cultivo(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idCultivo);
            cs.registerOutParameter(2, Types.INTEGER); // p_exists
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error en existeCultivo (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

