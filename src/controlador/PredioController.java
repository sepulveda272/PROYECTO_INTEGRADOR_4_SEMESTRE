/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Predio;
import modelo.PredioDAO;
import java.util.List;
/**
 *
 * @author ADMIN
 */
public class PredioController {
    private final PredioDAO predioDAO;

    public PredioController() {
        this.predioDAO = new PredioDAO();
    }

    /* ===================== VALIDACIONES (mínimas) ===================== */

    private String validarCampos(String nombrePredio,
                                 Double areaTotal,
                                 String nombrePropietario,
                                 String direccion,
                                 Double lat,
                                 Double lon,
                                 Integer idLugar,
                                 String estadoOpt) {

        if (nombrePredio == null || nombrePredio.trim().isEmpty()) return "El nombre del predio es obligatorio.";
        if (areaTotal == null || areaTotal <= 0)                   return "El área total debe ser > 0.";
        if (nombrePropietario == null || nombrePropietario.trim().isEmpty())
            return "El nombre del propietario es obligatorio.";
        if (direccion == null || direccion.trim().isEmpty())       return "La dirección es obligatoria.";

        if (lat == null || lat < -90 || lat > 90)                  return "Latitud fuera de rango (-90..90).";
        if (lon == null || lon < -180 || lon > 180)                return "Longitud fuera de rango (-180..180).";

        if (estadoOpt != null && !estadoOpt.trim().isEmpty()) {
            String e = estadoOpt.trim().toUpperCase();
            if (!e.equals("ACTIVO") && !e.equals("INACTIVO"))
                return "estado debe ser ACTIVO o INACTIVO.";
        }
        return null;
    }

    /* ===================== CREATE ===================== */

    /** Alta con ID automático (usa seq_predio). Retorna ID (>0) o -1 si falla. */
    public int agregarPredioAuto(String nombrePredio,
                                 double areaTotal,
                                 String nombrePropietario,
                                 String direccion,
                                 double lat,
                                 double lon,
                                 int idLugar,
                                 String estadoOpt /* puede ser null */) {

        String err = validarCampos(nombrePredio, areaTotal, nombrePropietario, direccion, lat, lon, idLugar, estadoOpt);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        Predio p = new Predio();
        p.setNombre_predio(nombrePredio);
        p.setArea_total(areaTotal);
        p.setNombre_propietario(nombrePropietario);
        p.setDireccion(direccion);
        p.setCoordenadas_lat(lat);
        p.setCoordenadas_lon(lon);
        p.setId_lugar(idLugar);
        p.setEstado(estadoOpt); // si viene null/blank, la BD pone ACTIVO

        try {
            int nuevoId = predioDAO.insertarPredioAuto(p);
            System.out.println(nuevoId > 0
                ? "✅ Predio creado con ID " + nuevoId + "."
                : "❌ Error al crear el predio.");
            return nuevoId;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return -1;
        }
    }

    /* ===================== READ ===================== */

    /** Lista para tabla mostrando además la etiqueta del lugar. */
    public List<Predio> listarPrediosConLugar() {
        List<Predio> lista = predioDAO.listarPrediosConLugar();
        if (lista.isEmpty()) System.out.println("ℹ️ No hay predios registrados.");
        else                 System.out.println("✅ Predios: " + lista.size());
        return lista;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarPredio(int idPredio,
                                    String nombrePredio,
                                    double areaTotal,
                                    String nombrePropietario,
                                    String direccion,
                                    double lat,
                                    double lon,
                                    int idLugar,
                                    String estado) {

        String err = validarCampos(nombrePredio, areaTotal, nombrePropietario, direccion, lat, lon, idLugar, estado);
        if (err != null) { System.out.println("❌ " + err); return false; }

        if (!predioDAO.existePredio(idPredio)) {
            System.out.println("⚠️ No existe el predio con ID " + idPredio + ".");
            return false;
        }

        Predio p = new Predio();
        p.setId_predio(idPredio);
        p.setNombre_predio(nombrePredio);
        p.setArea_total(areaTotal);
        p.setNombre_propietario(nombrePropietario);
        p.setDireccion(direccion);
        p.setCoordenadas_lat(lat);
        p.setCoordenadas_lon(lon);
        p.setId_lugar(idLugar);
        p.setEstado(estado);

        try {
            boolean ok = predioDAO.actualizarPredio(p);
            System.out.println(ok ? "✅ Predio actualizado." : "❌ Error al actualizar el predio.");
            return ok;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return false;
        }
    }

    /* ===================== DELETE (soft) ===================== */

    /** Pone ESTADO = 'INACTIVO'. */
    public boolean inactivarPredio(int idPredio) {
        if (!predioDAO.existePredio(idPredio)) {
            System.out.println("⚠️ No existe el predio con ID " + idPredio + ". Nada que inactivar.");
            return false;
        }
        boolean ok = predioDAO.eliminarPredio(idPredio);
        System.out.println(ok ? "✅ Predio inactivado." : "❌ No se pudo inactivar el predio.");
        return ok;
    }

    /* ===================== EXISTS (wrappers) ===================== */

    public boolean existePredio(int idPredio) { return predioDAO.existePredio(idPredio); }
    public boolean existeLugar(int idLugar)   { return predioDAO.existeLugar(idLugar); }
}
