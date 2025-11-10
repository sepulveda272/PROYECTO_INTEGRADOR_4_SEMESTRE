/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import modelo.Observaciones;
import modelo.ObservacionesDAO;
/**
 *
 * @author ADMIN
 */
public class ObservacionesController {
    private final ObservacionesDAO observacionesDAO;
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int MAX_OBS = 1000;

    public ObservacionesController() {
        this.observacionesDAO = new ObservacionesDAO();
    }

    /* ===================== CREATE ===================== */

    public boolean agregarObservacion(
            int idObservacion,
            String fechaObservacion,   // "yyyy-MM-dd"
            String observaciones,
            int idInspeccion,
            int idFuncionario
    ) {
        // Validaciones de UI
        String error = validarCampos(fechaObservacion, observaciones);
        if (error != null) { System.out.println("❌ " + error); return false; }

        // Duplicado por PK
        if (observacionesDAO.existeObservacion(idObservacion)) {
            System.out.println("❌ Ya existe una observación con ID " + idObservacion + ".");
            return false;
        }

        // Verificar FKs
        if (!observacionesDAO.existeInspeccion(idInspeccion)) {
            System.out.println("❌ No existe INSPECCION con ID " + idInspeccion + ".");
            return false;
        }
        if (!observacionesDAO.existeFuncionario(idFuncionario)) {
            System.out.println("❌ No existe FUNCIONARIO con ID " + idFuncionario + ".");
            return false;
        }

        // Construcción del modelo
        Observaciones o = new Observaciones();
        o.setId_observacion(idObservacion);
        o.setFecha_observacion(normalizaFecha(fechaObservacion));
        o.setObservaciones(observaciones != null ? observaciones.trim() : null);
        o.setId_inspeccion(idInspeccion);
        o.setId_funcionario(idFuncionario);

        boolean ok = observacionesDAO.insertar(o);
        if (ok) System.out.println("✅ Observación agregada (ID: " + idObservacion + ").");
        else    System.out.println("❌ Error al agregar la observación.");
        return ok;
    }

    /* ===================== READ ===================== */

    public List<Observaciones> listarObservaciones() {
        List<Observaciones> lista = observacionesDAO.listar();
        if (lista.isEmpty()) System.out.println("⚠️ No hay observaciones registradas.");
        else                 System.out.println("✅ Se encontraron " + lista.size() + " observaciones.");
        return lista;
    }

    // Atajo útil para la pantalla de detalle de una Inspección
    public List<Observaciones> listarObservacionesPorInspeccion(int idInspeccion) {
        if (!observacionesDAO.existeInspeccion(idInspeccion)) {
            System.out.println("⚠️ La inspección " + idInspeccion + " no existe. Se retorna lista vacía.");
            return java.util.Collections.emptyList();
        }
        List<Observaciones> lista = observacionesDAO.listarPorInspeccion(idInspeccion);
        if (lista.isEmpty()) System.out.println("ℹ️ La inspección " + idInspeccion + " no tiene observaciones.");
        else                 System.out.println("✅ Se encontraron " + lista.size() + " observaciones para la inspección " + idInspeccion + ".");
        return lista;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarObservacion(
            int idObservacion,
            String fechaObservacion,
            String observaciones,
            int idInspeccion,
            int idFuncionario
    ) {
        // Validaciones de UI
        String error = validarCampos(fechaObservacion, observaciones);
        if (error != null) { System.out.println("❌ " + error); return false; }

        // Debe existir
        if (!observacionesDAO.existeObservacion(idObservacion)) {
            System.out.println("⚠️ No existe observación con ID " + idObservacion + ". Nada que actualizar.");
            return false;
        }

        // Verificar FKs
        if (!observacionesDAO.existeInspeccion(idInspeccion)) {
            System.out.println("❌ No existe INSPECCION con ID " + idInspeccion + ".");
            return false;
        }
        if (!observacionesDAO.existeFuncionario(idFuncionario)) {
            System.out.println("❌ No existe FUNCIONARIO con ID " + idFuncionario + ".");
            return false;
        }

        Observaciones o = new Observaciones();
        o.setId_observacion(idObservacion);
        o.setFecha_observacion(normalizaFecha(fechaObservacion));
        o.setObservaciones(observaciones != null ? observaciones.trim() : null);
        o.setId_inspeccion(idInspeccion);
        o.setId_funcionario(idFuncionario);

        boolean ok = observacionesDAO.actualizar(o);
        if (ok) System.out.println("✅ Observación actualizada (ID: " + idObservacion + ").");
        else    System.out.println("❌ Error al actualizar la observación (ID: " + idObservacion + ").");
        return ok;
    }

    /* ===================== DELETE ===================== */

    public boolean eliminarObservacion(int idObservacion) {
        if (!observacionesDAO.existeObservacion(idObservacion)) {
            System.out.println("⚠️ No existe observación con ID " + idObservacion + ". Nada que eliminar.");
            return false;
        }

        boolean ok = observacionesDAO.eliminar(idObservacion);
        if (ok) System.out.println("✅ Observación eliminada (ID: " + idObservacion + ").");
        else    System.out.println("❌ No se pudo eliminar la observación (ID: " + idObservacion + ").");
        return ok;
    }

    /* ===================== EXISTS (expuesto a UI) ===================== */

    public boolean existeIdObservacion(int idObservacion) {
        return observacionesDAO.existeObservacion(idObservacion);
    }

    /* ===================== VALIDACIONES INTERNAS ===================== */

    private String validarCampos(String fechaObservacion, String observaciones) {
        if (fechaObservacion == null || fechaObservacion.trim().isEmpty()) {
            return "El campo 'fecha_observacion' es obligatorio.";
        }
        if (!esFechaISOValida(fechaObservacion)) {
            return "La 'fecha_observacion' debe tener formato yyyy-MM-dd.";
        }
        if (observaciones == null || observaciones.trim().isEmpty()) {
            return "El campo 'observaciones' es obligatorio.";
        }
        String obsTrim = observaciones.trim();
        if (obsTrim.length() > MAX_OBS) {
            return "El campo 'observaciones' no puede superar " + MAX_OBS + " caracteres. Actual: " + obsTrim.length();
        }
        return null;
    }

    private boolean esFechaISOValida(String fecha) {
        try {
            LocalDate.parse(fecha, ISO);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String normalizaFecha(String fecha) {
        if (fecha == null) return null;
        String f = fecha.trim();
        return f.isEmpty() ? null : f;
    }
}
