/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.InspeccionFitosanitaria;
import modelo.InspeccionFitosanitariaDAO;
import modelo.ReporteDAO;
import modelo.ReporteInspeccionDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

/**
 *
 * @author ADMIN
 */
public class InspeccionFitosanitariaController {
    private final InspeccionFitosanitariaDAO inspeccionDAO;
    private final ReporteDAO reporteDAO;

    public InspeccionFitosanitariaController() {
        this.inspeccionDAO = new InspeccionFitosanitariaDAO();
        this.reporteDAO = new ReporteDAO();
    }

    private String validar(Integer revisadas, Integer afectadas,
                           String fecha, Integer numeroLote, Integer idTecnico) {
        if (revisadas == null || revisadas < 0) return "plantas_revisadas debe ser >= 0.";
        if (afectadas == null || afectadas < 0) return "plantas_afectadas debe ser >= 0.";
        if (afectadas > revisadas) return "plantas_afectadas no puede ser mayor que plantas_revisadas.";
        if (fecha == null || fecha.trim().isEmpty()) return "fecha_inspeccion es obligatoria (yyyy-MM-dd).";
        if (numeroLote == null || numeroLote <= 0) return "numero_lote inválido.";
        if (idTecnico == null || idTecnico <= 0) return "id_tecnico inválido.";
        return null;
    }

    /* CREATE con secuencia */
    public int crearAuto(int revisadas, int afectadas, String fecha, int numeroLote, int idTecnico) {
        String err = validar(revisadas, afectadas, fecha, numeroLote, idTecnico);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        InspeccionFitosanitaria i = new InspeccionFitosanitaria();
        i.setPlantas_revisadas(revisadas);
        i.setPlantas_afectadas(afectadas);
        i.setFecha_inspeccion(fecha);
        i.setNumero_lote(numeroLote);
        i.setId_tecnico(idTecnico);

        try {
            int id = inspeccionDAO.insertarInspeccionAuto(i);
            System.out.println(id > 0 ? "✅ Inspección creada ID " + id : "❌ Error al crear inspección");
            return id;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return -1;
        }
    }

    /* UPDATE: sin nivel en la firma */
    public boolean actualizar(int idInspeccion, int revisadas, int afectadas, String fecha,
                              int numeroLote, int idTecnico) {
        String err = validar(revisadas, afectadas, fecha, numeroLote, idTecnico);
        if (err != null) { System.out.println("❌ " + err); return false; }

        InspeccionFitosanitaria i = new InspeccionFitosanitaria();
        i.setId_inspeccion(idInspeccion);
        i.setPlantas_revisadas(revisadas);
        i.setPlantas_afectadas(afectadas);
        i.setFecha_inspeccion(fecha);
        i.setNumero_lote(numeroLote);
        i.setId_tecnico(idTecnico);

        try {
            boolean ok = inspeccionDAO.actualizarInspeccion(i);
            System.out.println(ok ? "✅ Inspección actualizada" : "❌ Error al actualizar inspección");
            return ok;
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            return false;
        }
    }
    
    public List<InspeccionFitosanitaria> listarConTecnicoEIncidencia() {
        return inspeccionDAO.listarInspeccionesConTecnicoEIncidencia();
    }

    public String eliminar(int idInspeccion) {
        String error = inspeccionDAO.eliminarInspeccion(idInspeccion);

        if (error == null) {
            System.out.println("✅ Inspección eliminada");
        } else {
            System.out.println("❌ No se pudo eliminar: " + error);
        }

        return error;
    }
    public List<ReporteInspeccionDTO> listarHistorialReporte(
            Integer idProductor,
            Integer idPredio,
            Integer idCultivo,
            Integer idPlaga,
            Integer idTecnico,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {
        try {
            return reporteDAO.listarHistorial(
                    idProductor,
                    idPredio,
                    idCultivo,
                    idPlaga,
                    idTecnico,
                    fechaDesde,
                    fechaHasta
            );
        } catch (SQLException e) {
            e.printStackTrace();
            // En caso de error devolvemos lista vacía para que la vista lo maneje
            return new ArrayList<>();
        }
    }

}
