/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.LugarProduccion;
import modelo.LugarProduccionDAO;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ADMIN
 */
public class LugarProduccionController {
    private LugarProduccionDAO lugarProduccionDAO;
    public LugarProduccionController() {
        this.lugarProduccionDAO = new LugarProduccionDAO();
    }

    /* ===================== VALIDACIONES (mínimas) ===================== */

    private String validarCampos(String departamento, String municipio, String vereda,
                                 Integer cantidadMaxima, Integer idProductor) {
        if (departamento == null || departamento.trim().isEmpty()) return "El departamento es obligatorio.";
        if (municipio == null || municipio.trim().isEmpty())       return "El municipio es obligatorio.";
        if (vereda == null || vereda.trim().isEmpty())             return "La vereda es obligatoria.";
        if (cantidadMaxima == null || cantidadMaxima <= 0)         return "La cantidad máxima debe ser > 0.";
        if (idProductor == null || idProductor <= 0)               return "id_productor debe ser > 0.";
        return null;
    }

    /* ===================== CREATE ===================== */

    /** Alta con ID automático (usa seq_lugar). Retorna ID (>0) o -1 si falla. */
    public int agregarLugarAuto(String departamento, String municipio, String vereda,
                                int cantidadMaxima, int idProductor) {

        String err = validarCampos(departamento, municipio, vereda, cantidadMaxima, idProductor);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        LugarProduccion lp = new LugarProduccion();
        lp.setDepartamento(departamento);
        lp.setMunicipio(municipio);
        lp.setVereda(vereda);
        lp.setCantidad_maxima(cantidadMaxima);
        lp.setId_productor(idProductor);

        try {
            int nuevoId = lugarProduccionDAO.insertarLugarAuto(lp);
            if (nuevoId > 0) System.out.println("✅ Lugar creado con ID " + nuevoId + ".");
            else             System.out.println("❌ Error al crear el lugar.");
            return nuevoId;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return -1;
        }
    }

    /** Alta con ID provisto. Retorna true si OK. */
    public boolean agregarLugarConId(int idLugar, String departamento, String municipio, String vereda,
                                     int cantidadMaxima, int idProductor) {
        String err = validarCampos(departamento, municipio, vereda, cantidadMaxima, idProductor);
        if (err != null) { System.out.println("❌ " + err); return false; }

        LugarProduccion lp = new LugarProduccion();
        lp.setId_lugar(idLugar);
        lp.setDepartamento(departamento);
        lp.setMunicipio(municipio);
        lp.setVereda(vereda);
        lp.setCantidad_maxima(cantidadMaxima);
        lp.setId_productor(idProductor);

        try {
            boolean ok = lugarProduccionDAO.insertarLugar(lp);
            System.out.println(ok ? "✅ Lugar creado." : "❌ Error al crear el lugar.");
            return ok;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return false;
        }
    }

    /* ===================== READ ===================== */

    public List<LugarProduccion> listarLugares() {
        List<LugarProduccion> lista = lugarProduccionDAO.listarLugaresProduccion();
        if (lista.isEmpty()) System.out.println("ℹ️ No hay lugares registrados.");
        else                 System.out.println("✅ Lugares: " + lista.size());
        return lista;
    }

    /** Para tablas/vistas que necesitan mostrar el nombre del productor. */
    public List<LugarProduccion> listarLugaresConProductor() {
        List<LugarProduccion> lista = lugarProduccionDAO.listarLugaresConProductor();
        if (lista.isEmpty()) System.out.println("ℹ️ No hay lugares registrados.");
        else                 System.out.println("✅ Lugares (con productor): " + lista.size());
        return lista;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarLugar(int idLugar, String departamento, String municipio, String vereda,
                                   int cantidadMaxima, int idProductor) {
        String err = validarCampos(departamento, municipio, vereda, cantidadMaxima, idProductor);
        if (err != null) { System.out.println("❌ " + err); return false; }

        if (!lugarProduccionDAO.existeLugar(idLugar)) {
            System.out.println("⚠️ No existe el lugar con ID " + idLugar + ".");
            return false;
        }

        LugarProduccion lp = new LugarProduccion();
        lp.setId_lugar(idLugar);
        lp.setDepartamento(departamento);
        lp.setMunicipio(municipio);
        lp.setVereda(vereda);
        lp.setCantidad_maxima(cantidadMaxima);
        lp.setId_productor(idProductor);

        try {
            boolean ok = lugarProduccionDAO.actualizarLugar(lp);
            System.out.println(ok ? "✅ Lugar actualizado." : "❌ Error al actualizar el lugar.");
            return ok;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    /**
     * Elimina físicamente (DELETE). Si existe referencia por FK (p.ej., LOTE.ID_LUGAR),
     * el DAO devolverá false y ya imprime un mensaje claro. Aquí solo propagamos resultado.
     */
    public boolean eliminarLugar(int idLugar) {
        if (!lugarProduccionDAO.existeLugar(idLugar)) {
            System.out.println("⚠️ No existe el lugar con ID " + idLugar + ". Nada que eliminar.");
            return false;
        }
        boolean ok = lugarProduccionDAO.eliminarLugar(idLugar);
        if (ok) System.out.println("✅ Lugar eliminado (ID " + idLugar + ").");
        else    System.out.println("❌ No se pudo eliminar: verifique dependencias (PREDIO O LOTE).");
        return ok;
    }
}
