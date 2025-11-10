/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.List;
import modelo.Cultivo;
import modelo.CultivoDAO;

/**
 *
 * @author ADMIN
 */
public class CultivoController {
    private final CultivoDAO cultivoDAO;

    public CultivoController() {
        this.cultivoDAO = new CultivoDAO();
    }

    /* ======================= Validaciones ======================= */
    private String validarCampos(String nombreEspecie, String variedad) {
        if (nombreEspecie == null || nombreEspecie.trim().isEmpty())
            return "El campo 'nombre_especie' es obligatorio.";
        if (variedad == null || variedad.trim().isEmpty())
            return "El campo 'variedad' es obligatorio.";
        return null;
    }

    /* ============================ CREATE ============================ */

    /** Alta automática (toma ID de la secuencia del DAO). Retorna ID generado (>0) o -1. */
    public int agregarCultivoAuto(String nombreEspecie, String variedad, String descripcion) {
        String err = validarCampos(nombreEspecie, variedad);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        Cultivo c = new Cultivo();
        c.setNombre_especie(nombreEspecie);
        c.setVariedad(variedad);
        c.setDescripcion(descripcion);

        int nuevoId = cultivoDAO.insertarCultivoAuto(c);
        if (nuevoId > 0) System.out.println("✅ Cultivo agregado con ID " + nuevoId + " (secuencia).");
        else             System.out.println("❌ Error al agregar el cultivo (ID automático).");
        return nuevoId;
    }

    /* ============================= READ ============================= */

    /** Lista todos los cultivos. */
    public List<Cultivo> listarCultivos() {
        List<Cultivo> lista = cultivoDAO.listarCultivos();
        if (lista.isEmpty()) System.out.println("ℹ️ No hay cultivos registrados.");
        else                 System.out.println("✅ Total cultivos: " + lista.size());
        return lista;
    }

    /* ============================ UPDATE ============================ */

    public boolean actualizarCultivo(int idCultivo, String nombreEspecie, String variedad, String descripcion) {
        String err = validarCampos(nombreEspecie, variedad);
        if (err != null) { System.out.println("❌ " + err); return false; }

        if (!cultivoDAO.existeCultivo(idCultivo)) {
            System.out.println("⚠️ No existe un cultivo con ID " + idCultivo + ". Nada que actualizar.");
            return false;
        }

        Cultivo c = new Cultivo();
        c.setId_cultivo(idCultivo);
        c.setNombre_especie(nombreEspecie);
        c.setVariedad(variedad);
        c.setDescripcion(descripcion);

        boolean ok = cultivoDAO.actualizarCultivo(c);
        if (ok) System.out.println("✅ Cultivo actualizado (ID: " + idCultivo + ").");
        else    System.out.println("❌ Error al actualizar el cultivo (ID: " + idCultivo + ").");
        return ok;
    }

    /* ============================ DELETE ============================ */

    /** Elimina físicamente. Si hay FKs (por ejemplo, LOTE) fallará y el DAO lo reporta. */
    public boolean eliminarCultivo(int idCultivo) {
        if (!cultivoDAO.existeCultivo(idCultivo)) {
            System.out.println("⚠️ No existe un cultivo con ID " + idCultivo + ". Nada que eliminar.");
            return false;
        }
        boolean ok = cultivoDAO.eliminarCultivo(idCultivo);
        if (ok) System.out.println("✅ Cultivo eliminado (ID: " + idCultivo + ").");
        else    System.out.println("❌ No se pudo eliminar el cultivo. Revise dependencias (FKs).");
        return ok;
    }

    /* ============================ EXISTS ============================ */

    public boolean existeIdCultivo(int idCultivo) {
        return cultivoDAO.existeCultivo(idCultivo);
    }
}
