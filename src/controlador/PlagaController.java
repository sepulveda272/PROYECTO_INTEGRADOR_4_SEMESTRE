/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Plaga;
import modelo.PlagaDAO;
import java.util.List;
/**
 *
 * @author ADMIN
 */
public class PlagaController {
    private final PlagaDAO plagaDAO;

    public PlagaController() {
        this.plagaDAO = new PlagaDAO();
    }

    /**
     * Crear/insertar una plaga
     */
    public boolean agregarPlaga(int idPlaga, String nombreCientifico, String nombreComun, String descripcion) {
        // Validaciones mínimas
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            System.out.println("❌ El nombre científico es obligatorio.");
            return false;
        }
        if (nombreComun == null || nombreComun.trim().isEmpty()) {
            System.out.println("❌ El nombre común es obligatorio.");
            return false;
        }

        // Evitar duplicados por ID
        if (plagaDAO.existePlaga(idPlaga)) {
            System.out.println("❌ Ya existe una plaga con ID " + idPlaga + ".");
            return false;
        }

        // Evitar duplicados por nombre científico
        if (plagaDAO.existeNombreCientifico(nombreCientifico)) {
            System.out.println("❌ Ya existe una plaga con nombre científico: " + nombreCientifico + ".");
            return false;
        }

        Plaga p = new Plaga(
            idPlaga,
            nombreCientifico.trim(),
            nombreComun.trim(),
            (descripcion == null || descripcion.trim().isEmpty()) ? null : descripcion.trim()
        );

        boolean ok = plagaDAO.insertarPlaga(p);
        if (ok) System.out.println("✅ Plaga agregada con éxito.");
        else    System.out.println("❌ Error al agregar la plaga.");

        return ok;
    }
    
    // En PlagaController
    public boolean existeIdPlaga(int idPlaga) {
        return plagaDAO.existePlaga(idPlaga);
    }

    public boolean existeNombreCientificoDup(String nombreCientifico) {
        return plagaDAO.existeNombreCientifico(nombreCientifico);
    }


    /**
     * Listar todas las plagas
     */
    public List<Plaga> listarPlagas() {
        List<Plaga> plagas = plagaDAO.listarPlagas();

        if (plagas.isEmpty()) {
            System.out.println("⚠️ No hay plagas registradas.");
        } else {
            System.out.println("✅ Se encontraron " + plagas.size() + " plagas.");
        }

        return plagas;
    }

    /**
     * Actualizar una plaga existente
     */
    public boolean actualizarPlaga(int idPlaga, String nombreCientifico, String nombreComun, String descripcion) {
        if (!plagaDAO.existePlaga(idPlaga)) {
            System.out.println("❌ No existe la plaga con ID " + idPlaga + ". No se puede actualizar.");
            return false;
        }
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            System.out.println("❌ El nombre científico es obligatorio.");
            return false;
        }
        if (nombreComun == null || nombreComun.trim().isEmpty()) {
            System.out.println("❌ El nombre común es obligatorio.");
            return false;
        }

        Plaga p = new Plaga();
        p.setId_plaga(idPlaga);
        p.setNombre_cientifico(nombreCientifico.trim());
        p.setNombre_comun(nombreComun.trim());
        p.setDescripcion((descripcion == null || descripcion.trim().isEmpty()) ? null : descripcion.trim());

        boolean ok = plagaDAO.actualizarPlaga(p);

        if (ok) System.out.println("✅ Plaga actualizada correctamente (ID: " + idPlaga + ").");
        else    System.out.println("❌ Error al actualizar la plaga (ID: " + idPlaga + ").");

        return ok;
    }

    /**
     * Eliminar una plaga por ID
     */
    public void eliminarPlaga(int idPlaga) {
        boolean ok = plagaDAO.eliminarPlaga(idPlaga);

        if (ok) System.out.println("✅ Plaga eliminada correctamente (ID: " + idPlaga + ").");
        else    System.out.println("❌ Error al eliminar la plaga con ID: " + idPlaga + ".");
    }

    /**
     * Buscar plaga por ID (útil para la UI)
     */
    /*public Plaga buscarPlagaPorId(int idPlaga) {
        Plaga p = plagaDAO.buscarPorId(idPlaga);
        if (p == null) System.out.println("⚠️ No se encontró plaga con ID: " + idPlaga + ".");
        return p;
    }*/

    /**
     * Buscar plaga por nombre científico (exacto)
     */
    /*public Plaga buscarPlagaPorNombreCientifico(String nombreCientifico) {
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            System.out.println("⚠️ Nombre científico vacío.");
            return null;
        }
        Plaga p = plagaDAO.buscarPorNombreCientifico(nombreCientifico.trim());
        if (p == null) System.out.println("⚠️ No se encontró plaga con nombre científico: " + nombreCientifico + ".");
        return p;
    }*/
}
