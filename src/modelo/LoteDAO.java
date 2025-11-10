/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
/**
 *
 * @author ADMIN
 */
public class LoteDAO {
    private final Connection conexion;

    public LoteDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== VALIDACIONES ===================== */

    private void validarReglas(Lote l) {
        if (l.getArea_total() <= 0) throw new IllegalArgumentException("area_total debe ser > 0.");
        if (l.getArea_siembra() < 0) throw new IllegalArgumentException("area_siembra debe ser >= 0.");
        if (l.getArea_siembra() > l.getArea_total())
            throw new IllegalArgumentException("area_siembra no puede ser mayor que area_total.");
        if (isBlank(l.getEstado_fenologico()))
            throw new IllegalArgumentException("estado_fenologico es obligatorio.");
        if (isBlank(l.getFecha_siembra()))
            throw new IllegalArgumentException("fecha_siembra es obligatoria (yyyy-MM-dd).");
        // fecha_eliminacion es opcional
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /* ===================== SECUENCIA ===================== */

    /** Obtiene el siguiente numero_lote desde la secuencia Oracle. */
    public int siguienteNumeroLote() {
        final String sql = "SELECT seq_lote.NEXTVAL FROM dual";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL de seq_lote: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // señal de error
    }

    /* ===================== EXISTS / FKs ===================== */

    public boolean existeLote(int numeroLote) {
        final String sql = "SELECT COUNT(*) FROM LOTE WHERE NUMERO_LOTE = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroLote);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existeCultivo(int idCultivo) {
        final String sql = "SELECT COUNT(*) FROM CULTIVO WHERE ID_CULTIVO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCultivo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean existeLugarProduccion(int idLugar) {
        final String sql = "SELECT COUNT(*) FROM LUGAR_PRODUCCION WHERE ID_LUGAR = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLugar);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /* ===================== CREATE ===================== */

    /** Inserta con ID provisto (no autogenerado). */
    public boolean insertarLote(Lote l) {
        validarReglas(l);

        // Validar FKs explícitamente para mejores mensajes
        if (!existeCultivo(l.getId_cultivo())) {
            throw new IllegalArgumentException("El id_cultivo " + l.getId_cultivo() + " no existe.");
        }
        if (!existeLugarProduccion(l.getId_lugar())) {
            throw new IllegalArgumentException("El id_lugar " + l.getId_lugar() + " no existe.");
        }

        final String sql = "INSERT INTO LOTE (" +
                "NUMERO_LOTE, AREA_TOTAL, AREA_SIEMBRA, ESTADO_FENOLOGICO, " +
                "FECHA_SIEMBRA, FECHA_ELIMINACION, ID_CULTIVO, ID_LUGAR) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, l.getNumero_lote());
            ps.setDouble(2, l.getArea_total());
            ps.setDouble(3, l.getArea_siembra());
            ps.setString(4, l.getEstado_fenologico());

            ps.setDate(5, toSqlDateOrNullStrict(l.getFecha_siembra()));
            java.sql.Date fElim = toSqlDateOrNullStrict(l.getFecha_eliminacion());
            if (fElim != null) ps.setDate(6, fElim); else ps.setNull(6, Types.DATE);

            ps.setInt(7, l.getId_cultivo());
            ps.setInt(8, l.getId_lugar());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar lote: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta autogenerando NUMERO_LOTE por secuencia. Retorna el ID generado (>0) o -1 si falla. */
    public int insertarLoteAuto(Lote l) {
        validarReglas(l);

        if (!existeCultivo(l.getId_cultivo())) {
            throw new IllegalArgumentException("El id_cultivo " + l.getId_cultivo() + " no existe.");
        }
        if (!existeLugarProduccion(l.getId_lugar())) {
            throw new IllegalArgumentException("El id_lugar " + l.getId_lugar() + " no existe.");
        }

        final int nuevoId = siguienteNumeroLote();
        if (nuevoId <= 0) {
            System.err.println("❌ No se pudo obtener NEXTVAL de la secuencia.");
            return -1;
        }
        l.setNumero_lote(nuevoId);
        boolean ok = insertarLote(l);
        return ok ? nuevoId : -1;
    }

    /* ===================== READ ===================== */

    public List<Lote> listarLote() {
        List<Lote> lista = new ArrayList<>();
        final String sql = "SELECT NUMERO_LOTE, AREA_TOTAL, AREA_SIEMBRA, ESTADO_FENOLOGICO, " +
                "FECHA_SIEMBRA, FECHA_ELIMINACION, ID_CULTIVO, ID_LUGAR " +
                "FROM LOTE ORDER BY NUMERO_LOTE";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Lote mapRow(ResultSet rs) throws SQLException {
        Lote l = new Lote();
        l.setNumero_lote(rs.getInt("NUMERO_LOTE"));
        l.setArea_total(rs.getDouble("AREA_TOTAL"));
        l.setArea_siembra(rs.getDouble("AREA_SIEMBRA"));
        l.setEstado_fenologico(rs.getString("ESTADO_FENOLOGICO"));

        Date fSiembra = rs.getDate("FECHA_SIEMBRA");
        l.setFecha_siembra(fSiembra != null ? fSiembra.toString() : null); // "yyyy-MM-dd"

        Date fElim = rs.getDate("FECHA_ELIMINACION");
        l.setFecha_eliminacion(fElim != null ? fElim.toString() : null);

        l.setId_cultivo(rs.getInt("ID_CULTIVO"));
        l.setId_lugar(rs.getInt("ID_LUGAR"));
        return l;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarLote(Lote l) {
        validarReglas(l);

        // Validar FKs si cambian
        if (!existeCultivo(l.getId_cultivo())) {
            throw new IllegalArgumentException("El id_cultivo " + l.getId_cultivo() + " no existe.");
        }
        if (!existeLugarProduccion(l.getId_lugar())) {
            throw new IllegalArgumentException("El id_lugar " + l.getId_lugar() + " no existe.");
        }

        final String sql = "UPDATE LOTE SET " +
                "AREA_TOTAL = ?, " +
                "AREA_SIEMBRA = ?, " +
                "ESTADO_FENOLOGICO = ?, " +
                "FECHA_SIEMBRA = ?, " +
                "FECHA_ELIMINACION = ?, " +
                "ID_CULTIVO = ?, " +
                "ID_LUGAR = ? " +
                "WHERE NUMERO_LOTE = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, l.getArea_total());
            ps.setDouble(2, l.getArea_siembra());
            ps.setString(3, l.getEstado_fenologico());

            ps.setDate(4, toSqlDateOrNullStrict(l.getFecha_siembra()));
            java.sql.Date fElim = toSqlDateOrNullStrict(l.getFecha_eliminacion());
            if (fElim != null) ps.setDate(5, fElim); else ps.setNull(5, Types.DATE);

            ps.setInt(6, l.getId_cultivo());
            ps.setInt(7, l.getId_lugar());
            ps.setInt(8, l.getNumero_lote());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar lote: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean eliminarLote(int numeroLote) {
        final String sql = "DELETE FROM LOTE WHERE NUMERO_LOTE = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, numeroLote);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException fk) {
            // Por ejemplo, si INSPECCION_FITOSANITARIA.NUMERO_LOTE referencia este LOTE
            System.err.println("❌ No se puede eliminar el lote: está referenciado por otras tablas. " + fk.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error al eliminar lote: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    private static final DateTimeFormatter FMT_YMD =
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private java.sql.Date toSqlDateOrNullStrict(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            LocalDate ld = LocalDate.parse(t, FMT_YMD); // valida formato y fecha real (29/02, etc.)
            return java.sql.Date.valueOf(ld);
        } catch (DateTimeParseException ex) {
            // Mensaje CLARO para UI
            throw new IllegalArgumentException(
                "Formato de fecha inválido: \"" + s + "\". Usa yyyy-MM-dd (ej. 2025-11-09)."
            );
        }
    }

}
