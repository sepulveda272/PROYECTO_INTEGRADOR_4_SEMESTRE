/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.List;
import modelo.Lote;
import modelo.LoteDAO;

/**
 *
 * @author ADMIN
 */
public class LoteController {

    private final LoteDAO loteDAO;

    public LoteController() {
        this.loteDAO = new LoteDAO();
    }

    /* ======================= VALIDACIONES ======================= */

    private String validarCampos(double areaTotal, double areaSiembra, String estadoFenologico,
                                 String fechaSiembra, int idCultivo, int idLugar) {

        if (areaTotal <= 0) return "El área total debe ser mayor que 0.";
        if (areaSiembra < 0) return "El área de siembra no puede ser negativa.";
        if (areaSiembra > areaTotal) return "El área de siembra no puede ser mayor que el área total.";
        if (estadoFenologico == null || estadoFenologico.trim().isEmpty())
            return "El estado fenológico es obligatorio.";
        if (fechaSiembra == null || fechaSiembra.trim().isEmpty())
            return "La fecha de siembra es obligatoria.";
        if (idCultivo <= 0) return "Debe seleccionar un cultivo válido.";
        if (idLugar <= 0) return "Debe seleccionar un lugar de producción válido.";

        return null;
    }

    /* ============================ CREATE ============================ */

    /** Inserta un lote usando secuencia automática. */
    public int agregarLoteAuto(double areaTotal, double areaSiembra, String estadoFenologico,
                           String fechaSiembra, String fechaEliminacion,
                           int idCultivo, int idLugar) {
        
        String err = validarCampos(areaTotal, areaSiembra, estadoFenologico, fechaSiembra, idCultivo, idLugar);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        try {
            Lote l = new Lote();
            l.setArea_total(areaTotal);
            l.setArea_siembra(areaSiembra);
            l.setEstado_fenologico(estadoFenologico);
            l.setFecha_siembra(fechaSiembra);
            l.setFecha_eliminacion(fechaEliminacion);
            l.setId_cultivo(idCultivo);
            l.setId_lugar(idLugar);

            int nuevoId = loteDAO.insertarLoteAuto(l);
            if (nuevoId > 0) System.out.println("✅ Lote creado con número " + nuevoId + ".");
            else             System.out.println("❌ Error al crear el lote.");
            return nuevoId;

        } catch (IllegalArgumentException iae) {
            // <- aquí llega el mensaje de formato de fecha
            System.out.println("❌ " + iae.getMessage());
            return -1;
        }
    }

    /* ============================= READ ============================= */

    /** Lista todos los lotes registrados. */
    public List<Lote> listarLotes() {
        List<Lote> lista = loteDAO.listarLote();
        if (lista.isEmpty()) System.out.println("ℹ️ No hay lotes registrados.");
        else System.out.println("✅ Total lotes: " + lista.size());
        return lista;
    }

    /* ============================ UPDATE ============================ */

    public boolean actualizarLote(int numeroLote, double areaTotal, double areaSiembra, String estadoFenologico,
                                  String fechaSiembra, String fechaEliminacion, int idCultivo, int idLugar) {

        String err = validarCampos(areaTotal, areaSiembra, estadoFenologico, fechaSiembra, idCultivo, idLugar);
        if (err != null) { System.out.println("❌ " + err); return false; }

        if (!loteDAO.existeLote(numeroLote)) {
            System.out.println("⚠️ No existe un lote con número " + numeroLote + ".");
            return false;
        }

        try {
            Lote l = new Lote();
            l.setNumero_lote(numeroLote);
            l.setArea_total(areaTotal);
            l.setArea_siembra(areaSiembra);
            l.setEstado_fenologico(estadoFenologico);
            l.setFecha_siembra(fechaSiembra);
            l.setFecha_eliminacion(fechaEliminacion);
            l.setId_cultivo(idCultivo);
            l.setId_lugar(idLugar);

            boolean ok = loteDAO.actualizarLote(l);
            if (ok) System.out.println("✅ Lote actualizado correctamente (N° " + numeroLote + ").");
            else    System.out.println("❌ Error al actualizar el lote (N° " + numeroLote + ").");
            return ok;

        } catch (IllegalArgumentException iae) {
            System.out.println("❌ " + iae.getMessage());
            return false;
        }
    }

    /* ============================ DELETE ============================ */

    public boolean eliminarLote(int numeroLote) {
        if (!loteDAO.existeLote(numeroLote)) {
            System.out.println("⚠️ No existe un lote con número " + numeroLote + ".");
            return false;
        }

        boolean ok = loteDAO.eliminarLote(numeroLote);
        if (ok) System.out.println("✅ Lote eliminado correctamente.");
        else System.out.println("❌ No se pudo eliminar el lote (verifique dependencias).");
        return ok;
    }

    /* ============================ EXISTS ============================ */

    public boolean existeLote(int numeroLote) {
        return loteDAO.existeLote(numeroLote);
    }
}
