/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.List;
import modelo.Observaciones;
import modelo.ObservacionesDAO;
/**
 *
 * @author ADMIN
 */
public class ObservacionesController {
    private final ObservacionesDAO observacionesDAO;

    public ObservacionesController() {
        this.observacionesDAO = new ObservacionesDAO();
    }

    /* ===================== READ ===================== */

    /** Lista todas las observaciones (si lo necesitas para una tabla general). */
    public List<Observaciones> listarTodas() {
        return observacionesDAO.listar();
    }

    /**
     * Lista observaciones de una inspección.
     * OJO: aquí ya viene el nombre del funcionario en Observaciones.nombreFuncionario
     * gracias al JOIN del DAO.
     */
    public List<Observaciones> listarPorInspeccion(int idInspeccion) {
        return observacionesDAO.listarPorInspeccion(idInspeccion);
    }

    /* ===================== CREATE ===================== */
    /**
     * Crea una nueva observación.
     * @return null si todo OK; mensaje de error si algo falla.
     */
    public String crear(String fechaObs,
                        String textoObs,
                        int idInspeccion,
                        int idFuncionario) {

        Observaciones o = new Observaciones();
        o.setFecha_observacion(fechaObs);      // "yyyy-MM-dd"
        o.setObservaciones(textoObs);
        o.setId_inspeccion(idInspeccion);
        o.setId_funcionario(idFuncionario);

        try {
            boolean ok = observacionesDAO.insertar(o);
            if (ok) {
                System.out.println("✅ Observación creada con ID " + o.getId_observacion());
                return null; // todo OK
            } else {
                return "No se pudo insertar la observación (revise la base de datos).";
            }
        } catch (IllegalArgumentException ex) {
            // Mensajes de validarReglas o de formato de fecha
            return ex.getMessage();
        }
    }

    /* ===================== UPDATE ===================== */
    /**
     * Actualiza una observación existente.
     * @return null si todo OK; mensaje de error si algo falla.
     */
    public String actualizar(int idObservacion,
                             String fechaObs,
                             String textoObs,
                             int idInspeccion,
                             int idFuncionario) {

        Observaciones o = new Observaciones();
        o.setId_observacion(idObservacion);
        o.setFecha_observacion(fechaObs);
        o.setObservaciones(textoObs);
        o.setId_inspeccion(idInspeccion);
        o.setId_funcionario(idFuncionario);

        try {
            boolean ok = observacionesDAO.actualizar(o);
            if (ok) {
                System.out.println("✅ Observación actualizada: " + idObservacion);
                return null;
            } else {
                return "No se encontró la observación a actualizar.";
            }
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }

    /* ===================== DELETE ===================== */
    /**
     * Elimina una observación por ID.
     * @return null si todo OK; mensaje de error si algo falla.
     */
    public String eliminar(int idObservacion) {
        boolean ok = observacionesDAO.eliminar(idObservacion);
        if (ok) {
            System.out.println("✅ Observación eliminada: " + idObservacion);
            return null;
        } else {
            return "No se pudo eliminar la observación (tal vez no existe).";
        }
    }

    /* ===================== HELPERS ===================== */

    public boolean existeObservacion(int idObservacion) {
        return observacionesDAO.existeObservacion(idObservacion);
    }
}
