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
import java.time.format.ResolverStyle;
import java.time.format.DateTimeParseException;

/**
 *
 * @author ADMIN
 */
public class InspeccionFitosanitariaDAO {

    private final Connection conexion;

    public InspeccionFitosanitariaDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== VALIDACIONES ===================== */

    private void validarReglas(InspeccionFitosanitaria i) {
        if (i == null) throw new IllegalArgumentException("La inspección no puede ser null.");

        if (i.getPlantas_revisadas() < 0)
            throw new IllegalArgumentException("plantas_revisadas debe ser >= 0.");
        if (i.getPlantas_afectadas() < 0)
            throw new IllegalArgumentException("plantas_afectadas debe ser >= 0.");
        if (i.getPlantas_afectadas() > i.getPlantas_revisadas())
            throw new IllegalArgumentException("plantas_afectadas no puede ser mayor que plantas_revisadas.");

        if (isBlank(i.getFecha_inspeccion()))
            throw new IllegalArgumentException("fecha_inspeccion es obligatoria (yyyy-MM-dd).");

        if (i.getNumero_lote() <= 0)
            throw new IllegalArgumentException("numero_lote debe ser un ID válido (>0).");

        if (i.getId_tecnico() <= 0)
            throw new IllegalArgumentException("id_tecnico debe ser un ID válido (>0).");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /* ===================== EXISTS / FKs ===================== */

    public boolean existeInspeccion(int idInspeccion) {
        final String sql = "SELECT COUNT(*) FROM INSPECCION_FITOSANITARIA WHERE ID_INSPECCION = ?";
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

    public boolean existeLote(int numeroLote) {
        final String sql = "SELECT COUNT(*) FROM LOTE WHERE NUMERO_LOTE = ?";
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

    public boolean existeTecnico(int idTecnico) {
        final String sql = "SELECT COUNT(*) FROM TECNICO_OFICIAL WHERE NUMERO_REGISTRO = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idTecnico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===================== CREATE (SP + TRIGGER) ===================== */

    /**
     * Inserta una inspección usando el procedimiento almacenado pr_insertar_inspeccion.
     * El ID_INSPECCION se genera en la BD mediante fn_generar_inspeccion + tr_inspeccion_bi.
     * Retorna el ID generado (>0) o -1 si falla.
     */
    public int insertarInspeccionAuto(InspeccionFitosanitaria i) {
        validarReglas(i);

        if (!existeLote(i.getNumero_lote()))
            throw new IllegalArgumentException("El numero_lote " + i.getNumero_lote() + " no existe.");
        if (!existeTecnico(i.getId_tecnico()))
            throw new IllegalArgumentException("El id_tecnico " + i.getId_tecnico() + " no existe.");

        final String sql = "{ call pr_insertar_inspeccion(?,?,?,?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setInt(1, i.getPlantas_revisadas());
            cs.setInt(2, i.getPlantas_afectadas());
            cs.setDate(3, toSqlDateOrNullStrict(i.getFecha_inspeccion()));
            cs.setInt(4, i.getNumero_lote());
            cs.setInt(5, i.getId_tecnico());

            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();

            int nuevoId = cs.getInt(6);
            i.setId_inspeccion(nuevoId);
            return nuevoId;

        } catch (SQLException e) {
            System.err.println("Error al insertar inspección (pr_insertar_inspeccion): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */

    /**
     * Lista inspecciones con nombre del técnico.
     * La incidencia puede calcularse en la UI usando los helpers si lo necesitas.
     */
    public List<InspeccionFitosanitaria> listarInspeccionesConTecnicoEIncidencia() {
        List<InspeccionFitosanitaria> lista = new ArrayList<>();
        final String sql =
            "SELECT i.ID_INSPECCION, i.PLANTAS_REVISADAS, i.PLANTAS_AFECTADAS, i.FECHA_INSPECCION, " +
            "       i.NIVEL_ALERTA, i.NUMERO_LOTE, i.ID_TECNICO, " +
            "       REGEXP_REPLACE(TRIM(NVL(t.PRIMER_NOMBRE,'') || ' ' || NVL(t.SEGUNDO_NOMBRE,'') || ' ' || " +
            "                       NVL(t.PRIMER_APELLIDO,'') || ' ' || NVL(t.SEGUNDO_APELLIDO,'')), ' +', ' ') AS NOMBRE_TECNICO " +
            "FROM INSPECCION_FITOSANITARIA i " +
            "JOIN TECNICO_OFICIAL t ON t.NUMERO_REGISTRO = i.ID_TECNICO " +
            "ORDER BY i.ID_INSPECCION";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InspeccionFitosanitaria i = new InspeccionFitosanitaria();
                i.setId_inspeccion(rs.getInt("ID_INSPECCION"));
                i.setPlantas_revisadas(rs.getInt("PLANTAS_REVISADAS"));
                i.setPlantas_afectadas(rs.getInt("PLANTAS_AFECTADAS"));
                Date f = rs.getDate("FECHA_INSPECCION");
                i.setFecha_inspeccion(f != null ? f.toString() : null);
                i.setNivel_alerta(rs.getString("NIVEL_ALERTA"));
                i.setNumero_lote(rs.getInt("NUMERO_LOTE"));
                i.setId_tecnico(rs.getInt("ID_TECNICO"));
                i.setNombre_tecnico(rs.getString("NOMBRE_TECNICO"));

                lista.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private InspeccionFitosanitaria mapRow(ResultSet rs) throws SQLException {
        InspeccionFitosanitaria i = new InspeccionFitosanitaria();
        i.setId_inspeccion(rs.getInt("ID_INSPECCION"));
        i.setPlantas_revisadas(rs.getInt("PLANTAS_REVISADAS"));
        i.setPlantas_afectadas(rs.getInt("PLANTAS_AFECTADAS"));
        Date f = rs.getDate("FECHA_INSPECCION");
        i.setFecha_inspeccion(f != null ? f.toString() : null); // "yyyy-MM-dd"
        i.setNivel_alerta(rs.getString("NIVEL_ALERTA"));
        i.setNumero_lote(rs.getInt("NUMERO_LOTE"));
        i.setId_tecnico(rs.getInt("ID_TECNICO"));
        return i;
    }

    /* ===================== UPDATE (SP) ===================== */

    public boolean actualizarInspeccion(InspeccionFitosanitaria i) {
        validarReglas(i);

        if (!existeInspeccion(i.getId_inspeccion()))
            throw new IllegalArgumentException("No existe la inspección con ID " + i.getId_inspeccion());
        if (!existeLote(i.getNumero_lote()))
            throw new IllegalArgumentException("El numero_lote " + i.getNumero_lote() + " no existe.");
        if (!existeTecnico(i.getId_tecnico()))
            throw new IllegalArgumentException("El id_tecnico " + i.getId_tecnico() + " no existe.");

        final String sql = "{ call pr_actualizar_inspeccion(?,?,?,?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setInt(1, i.getId_inspeccion());
            cs.setInt(2, i.getPlantas_revisadas());
            cs.setInt(3, i.getPlantas_afectadas());
            cs.setDate(4, toSqlDateOrNullStrict(i.getFecha_inspeccion()));
            cs.setInt(5, i.getNumero_lote());
            cs.setInt(6, i.getId_tecnico());

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar inspección (pr_actualizar_inspeccion): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE (SP) ===================== */

    /**
     * Elimina una inspección.
     * Retorna null si todo fue bien, o un mensaje de error si no se pudo.
     */
    public String eliminarInspeccion(int idInspeccion) {

        if (!existeInspeccion(idInspeccion)) {
            return "No existe una inspección con ese ID.";
        }

        final String sql = "{ call pr_eliminar_inspeccion(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idInspeccion);
            cs.execute();
            return null;

        } catch (SQLException e) {
            // ORA-02292: integrity constraint violated - child record found
            if (e.getErrorCode() == 2292) {
                return "No se puede eliminar la inspección porque tiene observaciones asociadas.";
            }

            System.err.println("Error al eliminar inspección (pr_eliminar_inspeccion): " + e.getMessage());
            e.printStackTrace();
            return "Error al eliminar inspección: " + e.getMessage();
        }
    }

    /* ===================== FECHAS (estricto yyyy-MM-dd) ===================== */

    private static final DateTimeFormatter FMT_YMD =
        DateTimeFormatter.ofPattern("uuuu-MM-dd")
                         .withResolverStyle(ResolverStyle.STRICT);

    private java.sql.Date toSqlDateOrNullStrict(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            LocalDate ld = LocalDate.parse(t, FMT_YMD); // valida formato y fecha real
            return java.sql.Date.valueOf(ld);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                "Formato de fecha inválido: \"" + s + "\". Usa yyyy-MM-dd (ej. 2025-11-09)."
            );
        }
    }

    /* ===================== HELPERS INCIDENCIA / NIVEL (para la UI) ===================== */

    public double calcularIncidencia(int revisadas, int afectadas) {
        if (revisadas <= 0) return 0.0;            // evita división por cero
        if (afectadas < 0) afectadas = 0;
        if (afectadas > revisadas) afectadas = revisadas;
        return (afectadas * 100.0) / revisadas;    // porcentaje
    }

    public String calcularNivelPorIncidencia(double inc) {
        if (inc == 0.0)                  return "Sin incidencia";
        else if (inc > 0 && inc <= 5)    return "Muy baja";
        else if (inc > 5 && inc <= 10)   return "Baja";
        else if (inc > 10 && inc <= 20)  return "Moderada";
        else if (inc > 20 && inc <= 40)  return "Alta";
        else if (inc > 40 && inc <= 60)  return "Muy alta";
        else                             return "Critica"; // >60
    }
}
