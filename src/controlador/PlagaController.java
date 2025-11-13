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

    /* ============================================================
     * MÉTODOS PRIVADOS DE APOYO (VALIDACIONES / EXISTENCIAS)
     * ============================================================ */

    /**
     * Verifica si existe una plaga con el ID dado.
     */
    private boolean existePlagaPorId(int idPlaga) {
        return plagaDAO.obtenerPlagaPorId(idPlaga) != null;
    }

    /**
     * Verifica si ya existe una plaga con el mismo nombre científico.
     * Busca sobre la lista completa de plagas (para no tocar tu DAO).
     */
    public boolean existeNombreCientifico(String nombreCientifico) {
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            return false;
        }
        String buscado = nombreCientifico.trim().toLowerCase();

        List<Plaga> plagas = plagaDAO.listarPlagas();
        for (Plaga p : plagas) {
            if (p.getNombre_cientifico() != null &&
                p.getNombre_cientifico().trim().toLowerCase().equals(buscado)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si ya existe OTRA plaga con el mismo nombre científico
     * (se usa al actualizar, para no chocar contra sí misma).
     */
    public boolean existeNombreCientificoEnOtraPlaga(int idPlaga, String nombreCientifico) {
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            return false;
        }
        String buscado = nombreCientifico.trim().toLowerCase();

        List<Plaga> plagas = plagaDAO.listarPlagas();
        for (Plaga p : plagas) {
            if (p.getId_plaga() != idPlaga &&
                p.getNombre_cientifico() != null &&
                p.getNombre_cientifico().trim().toLowerCase().equals(buscado)) {
                return true;
            }
        }
        return false;
    }

    /* ============================================================
     * CRUD: CREAR
     * ============================================================ */

    /**
     * Crear/insertar una plaga.
     * El id_plaga lo genera la secuencia seq_plaga en PlagaDAO.
     */
    public boolean agregarPlaga(String nombreCientifico, String nombreComun, String descripcion) {
        // Validaciones mínimas
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            System.out.println("[ERROR] El nombre científico es obligatorio.");
            return false;
        }
        if (nombreComun == null || nombreComun.trim().isEmpty()) {
            System.out.println("[ERROR] El nombre común es obligatorio.");
            return false;
        }

        // Evitar duplicados por nombre científico
        if (existeNombreCientifico(nombreCientifico)) {
            System.out.println("[ERROR] Ya existe una plaga con nombre científico: " + nombreCientifico + ".");
            return false;
        }

        Plaga p = new Plaga();
        p.setNombre_cientifico(nombreCientifico.trim());
        p.setNombre_comun(nombreComun.trim());
        p.setDescripcion(
            (descripcion == null || descripcion.trim().isEmpty())
                ? null
                : descripcion.trim()
        );

        boolean ok = plagaDAO.insertarPlaga(p);
        if (ok) {
            System.out.println("[OK] Plaga agregada con éxito. ID generado: " + p.getId_plaga());
        } else {
            System.out.println("[ERROR] Error al agregar la plaga.");
        }

        return ok;
    }

    /* ============================================================
     * CRUD: LEER / LISTAR
     * ============================================================ */

    /**
     * Listar todas las plagas.
     */
    public List<Plaga> listarPlagas() {
        List<Plaga> plagas = plagaDAO.listarPlagas();

        if (plagas.isEmpty()) {
            System.out.println("[INFO] No hay plagas registradas.");
        } else {
            System.out.println("[INFO] Se encontraron " + plagas.size() + " plagas.");
        }

        return plagas;
    }

    /**
     * Buscar una plaga por ID (útil para la UI).
     */
    public Plaga buscarPlagaPorId(int idPlaga) {
        Plaga p = plagaDAO.obtenerPlagaPorId(idPlaga);
        if (p == null) {
            System.out.println("[ADVERTENCIA] No se encontró plaga con ID: " + idPlaga + ".");
        }
        return p;
    }

    /* ============================================================
     * CRUD: ACTUALIZAR
     * ============================================================ */

    /**
     * Actualizar una plaga existente.
     */
    public boolean actualizarPlaga(int idPlaga, String nombreCientifico, String nombreComun, String descripcion) {
        // Verificar existencia
        if (!existePlagaPorId(idPlaga)) {
            System.out.println("[ERROR] No existe la plaga con ID " + idPlaga + ". No se puede actualizar.");
            return false;
        }

        // Validaciones de campos
        if (nombreCientifico == null || nombreCientifico.trim().isEmpty()) {
            System.out.println("[ERROR] El nombre científico es obligatorio.");
            return false;
        }
        if (nombreComun == null || nombreComun.trim().isEmpty()) {
            System.out.println("[ERROR] El nombre común es obligatorio.");
            return false;
        }

        // Validar que no exista otra plaga con el mismo nombre científico
        if (existeNombreCientificoEnOtraPlaga(idPlaga, nombreCientifico)) {
            System.out.println("[ERROR] Ya existe otra plaga con el mismo nombre científico: " + nombreCientifico + ".");
            return false;
        }

        Plaga p = new Plaga();
        p.setId_plaga(idPlaga);
        p.setNombre_cientifico(nombreCientifico.trim());
        p.setNombre_comun(nombreComun.trim());
        p.setDescripcion(
            (descripcion == null || descripcion.trim().isEmpty())
                ? null
                : descripcion.trim()
        );

        boolean ok = plagaDAO.actualizarPlaga(p);

        if (ok) {
            System.out.println("[OK] Plaga actualizada correctamente (ID: " + idPlaga + ").");
        } else {
            System.out.println("[ERROR] Error al actualizar la plaga (ID: " + idPlaga + ").");
        }

        return ok;
    }

    /* ============================================================
     * CRUD: ELIMINAR
     * ============================================================ */

    /**
     * Eliminar una plaga por ID.
     * Primero valida que exista y que no esté referenciada en la tabla afectado.
     */
    public boolean eliminarPlaga(int idPlaga) {
        // Verificar existencia
        if (!existePlagaPorId(idPlaga)) {
            System.out.println("[ERROR] No existe la plaga con ID " + idPlaga + ". No se puede eliminar.");
            return false;
        }

        // Regla de negocio: no se puede eliminar si está referenciada en afectado
        if (plagaDAO.tieneReferenciasEnAfectado(idPlaga)) {
            System.out.println("[ERROR] No se puede eliminar la plaga (ID: " + idPlaga
                    + ") porque está asociada a uno o más registros de 'afectado'.");
            return false;
        }

        boolean ok = plagaDAO.eliminarPlaga(idPlaga);

        if (ok) {
            System.out.println("[OK] Plaga eliminada correctamente (ID: " + idPlaga + ").");
        } else {
            System.out.println("[ERROR] Error al eliminar la plaga con ID: " + idPlaga + ".");
        }

        return ok;
    }
}
