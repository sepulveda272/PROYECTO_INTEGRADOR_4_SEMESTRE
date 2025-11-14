/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ObservacionesDAO {

    private final Connection conexion;

    public ObservacionesDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== FECHAS (estricto yyyy-MM-dd) ===================== */

    private static final DateTimeFormatter FMT_YMD =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                             .withResolverStyle(ResolverStyle.STRICT);

    /** Convierte String "yyyy-MM-dd" a java.sql.Date, validando fecha real */
    private java.sql.Date toSqlDateOrNullStrict(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            LocalDate ld = LocalDate.parse(t, FMT_YMD); // valida formato y fecha real
            return java.sql.Date.valueOf(ld);
        } catch (DateTimeParseException ex) {
            // Mensaje CLARO para UI
            throw new IllegalArgumentException(
                "Formato de fecha inválido: \"" + s + "\". Usa yyyy-MM-dd (ej. 2025-11-09)."
            );
        }
    }

    /** Convierte java.sql.Date a String "yyyy-MM-dd" usando el mismo formato */
    private String toYmdOrNull(java.sql.Date d) {
        if (d == null) return null;
        return d.toLocalDate().format(FMT_YMD);
    }

    /* ===================== REGLAS DE NEGOCIO ===================== */

    private void validarReglas(Observaciones o) {
        if (o == null) {
            throw new IllegalArgumentException("La observación no puede ser null.");
        }

        // fecha_observacion
        if (o.getFecha_observacion() == null || o.getFecha_observacion().trim().isEmpty()) {
            throw new IllegalArgumentException("fecha_observacion es obligatoria.");
        }
        // valida que la fecha tenga formato y sea una fecha real
        toSqlDateOrNullStrict(o.getFecha_observacion());

        // texto observaciones
        if (o.getObservaciones() == null || o.getObservaciones().trim().isEmpty()) {
            throw new IllegalArgumentException("observaciones es obligatorio.");
        }
        if (o.getObservaciones().length() > 1000) {
            throw new IllegalArgumentException("observaciones no puede superar 1000 caracteres.");
        }

        // FK inspeccion
        if (o.getId_inspeccion() <= 0) {
            throw new IllegalArgumentException("id_inspeccion debe ser > 0.");
        }
        if (!existeInspeccion(o.getId_inspeccion())) {
            throw new IllegalArgumentException("La inspección indicada no existe.");
        }

        // FK funcionario
        if (o.getId_funcionario() <= 0) {
            throw new IllegalArgumentException("id_funcionario debe ser > 0.");
        }
        if (!existeFuncionario(o.getId_funcionario())) {
            throw new IllegalArgumentException("El funcionario indicado no existe.");
        }
    }

    /* ===================== CREATE (SP + TRIGGER) ===================== */

    /**
     * Inserta una observación usando el procedimiento almacenado pr_insertar_observacion.
     * El ID_OBSERVACION se genera en la BD mediante fn_generar_observacion + tr_observacion_bi.
     */
    public boolean insertar(Observaciones o) {
        validarReglas(o);

        final String sql = "{ call pr_insertar_observacion(?,?,?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setDate(1, toSqlDateOrNullStrict(o.getFecha_observacion()));
            cs.setString(2, o.getObservaciones());
            cs.setInt(3, o.getId_inspeccion());
            cs.setInt(4, o.getId_funcionario());

            // OUT: id_observacion generado
            cs.registerOutParameter(5, Types.INTEGER);

            cs.execute();

            int nuevoId = cs.getInt(5);
            o.setId_observacion(nuevoId); // reflejar en el objeto
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar observación (pr_insertar_observacion): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== READ ===================== */

    public List<Observaciones> listar() {
        List<Observaciones> lista = new ArrayList<>();

        String sql = "SELECT o.ID_OBSERVACION, o.FECHA_OBSERVACION, o.OBSERVACIONES, "
                   + "       o.ID_INSPECCION, o.ID_FUNCIONARIO, "
                   + "       f.PRIMER_NOMBRE, f.PRIMER_APELLIDO "
                   + "FROM   OBSERVACIONES o "
                   + "JOIN   FUNCIONARIO_ICA f ON o.ID_FUNCIONARIO = f.ID_FUNCIONARIO "
                   + "ORDER BY o.ID_OBSERVACION";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Observaciones o = new Observaciones();

                o.setId_observacion(rs.getInt("ID_OBSERVACION"));

                java.sql.Date f = rs.getDate("FECHA_OBSERVACION");
                o.setFecha_observacion(toYmdOrNull(f)); // "yyyy-MM-dd"

                o.setObservaciones(rs.getString("OBSERVACIONES"));
                o.setId_inspeccion(rs.getInt("ID_INSPECCION"));
                o.setId_funcionario(rs.getInt("ID_FUNCIONARIO"));

                // Construimos el nombre del funcionario
                String nombre   = rs.getString("PRIMER_NOMBRE");
                String apellido = rs.getString("PRIMER_APELLIDO");
                String nombreCompleto = ((nombre != null) ? nombre : "") + " " +
                                        ((apellido != null) ? apellido : "");
                o.setNombre_Funcionario(nombreCompleto.trim());

                lista.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Lista observaciones de una inspección, incluyendo el nombre del funcionario.
     */
    public List<Observaciones> listarPorInspeccion(int idInspeccion) {
        List<Observaciones> lista = new ArrayList<>();

        String sql = "SELECT o.ID_OBSERVACION, o.FECHA_OBSERVACION, o.OBSERVACIONES, "
                   + "       o.ID_INSPECCION, o.ID_FUNCIONARIO, "
                   + "       f.PRIMER_NOMBRE, f.PRIMER_APELLIDO "
                   + "FROM   OBSERVACIONES o "
                   + "JOIN   FUNCIONARIO_ICA f ON o.ID_FUNCIONARIO = f.ID_FUNCIONARIO "
                   + "WHERE  o.ID_INSPECCION = ? "
                   + "ORDER BY o.FECHA_OBSERVACION";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInspeccion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Observaciones o = new Observaciones();

                    o.setId_observacion(rs.getInt("ID_OBSERVACION"));

                    java.sql.Date f = rs.getDate("FECHA_OBSERVACION");
                    o.setFecha_observacion(toYmdOrNull(f));

                    o.setObservaciones(rs.getString("OBSERVACIONES"));
                    o.setId_inspeccion(rs.getInt("ID_INSPECCION"));
                    o.setId_funcionario(rs.getInt("ID_FUNCIONARIO"));

                    String nombre = rs.getString("PRIMER_NOMBRE");
                    String apellido = rs.getString("PRIMER_APELLIDO");
                    String nombreCompleto = (nombre != null ? nombre : "")
                                           + " "
                                           + (apellido != null ? apellido : "");
                    o.setNombre_Funcionario(nombreCompleto.trim());

                    lista.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /* ===================== UPDATE (SP) ===================== */

    public boolean actualizar(Observaciones o) {
        validarReglas(o);

        if (!existeObservacion(o.getId_observacion())) {
            throw new IllegalArgumentException("No existe la observación con ID " + o.getId_observacion());
        }

        final String sql = "{ call pr_actualizar_observacion(?,?,?,?,?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setInt(1, o.getId_observacion());
            cs.setDate(2, toSqlDateOrNullStrict(o.getFecha_observacion()));
            cs.setString(3, o.getObservaciones());
            cs.setInt(4, o.getId_inspeccion());
            cs.setInt(5, o.getId_funcionario());

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar observación (pr_actualizar_observacion): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE (SP) ===================== */

    public boolean eliminar(int idObservacion) {

        if (!existeObservacion(idObservacion)) {
            return false;
        }

        final String sql = "{ call pr_eliminar_observacion(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idObservacion);
            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar observación (pr_eliminar_observacion): " + e.getMessage());
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