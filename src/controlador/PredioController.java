/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Predio;
import modelo.PredioDAO;
import modelo.LugarProduccionDAO;
import java.util.List;
/**
 *
 * @author ADMIN
 */
public class PredioController {
    private PredioDAO predioDAO;
    private LugarProduccionDAO lugarProduccionDAO;

    public PredioController() {
        this.predioDAO = new PredioDAO();
        this.lugarProduccionDAO = new LugarProduccionDAO();
    }
    
    public boolean agregarPredio(int idPredio, String nombrePredio, double areaTotal,
                                 String nombrePropietario, String direccion,
                                 double coordenadasLat, double coordenadasLon, int idLugar, String estado) {

        // ✅ Verificar si el lugar de producción existe y está activo
        if (!lugarProduccionDAO.existeLugarPro(idLugar)) {
            System.out.println("❌ El lugar de producción con ID " + idLugar + " no existe o no está activo.");
            return false;
        }

        // Crear el objeto Predio
        Predio predio = new Predio(
                idPredio,
                nombrePredio,
                areaTotal,
                nombrePropietario,
                direccion,
                coordenadasLat,
                coordenadasLon,
                idLugar,
                estado
        );

        // Insertar usando el DAO
        boolean resultado = predioDAO.insertarPredio(predio);

        // Validar resultado
        if (resultado) {
            System.out.println("✅ Predio agregado correctamente (ID: " + idPredio + ")");
        } else {
            System.out.println("❌ Error al agregar el predio.");
        }

        return resultado;
    }

    /**
     * Listar todos los predios
     */
    public List<Predio> listarPredios() {
        List<Predio> predios = predioDAO.listarPredios();

        if (predios.isEmpty()) {
            System.out.println("⚠️ No hay predios registrados.");
        } else {
            System.out.println("✅ Se encontraron " + predios.size() + " predios.");
        }

        return predios;
    }

    /**
     * Actualizar un predio existente
     */
    public boolean actualizarPredio(int idPredio, String nombrePredio, double areaTotal,
                                    String nombrePropietario, String direccion,
                                    double coordenadasLat, double coordenadasLon,int idLugar, String estado) {

        // ✅ Verificar si el lugar existe y está activo
        if (!lugarProduccionDAO.existeLugarPro(idLugar)) {
            System.out.println("❌ El lugar de producción con ID " + idLugar + " no existe o no está activo. No se puede actualizar.");
            return false;
        }

        // Crear el objeto actualizado
        Predio predio = new Predio();
        predio.setId_predio(idPredio);
        predio.setNombre_predio(nombrePredio);
        predio.setArea_total(areaTotal);
        predio.setNombre_propietario(nombrePropietario);
        predio.setDireccion(direccion);
        predio.setCoordenadas_lat(coordenadasLat);
        predio.setCoordenadas_lon(coordenadasLon);
        predio.setId_lugar(idLugar);
        predio.setEstado(estado);

        // Actualizar usando el DAO
        boolean resultado = predioDAO.actualizarPredio(predio);

        if (resultado) {
            System.out.println("✅ Predio actualizado correctamente (ID: " + idPredio + ")");
        } else {
            System.out.println("❌ Error al actualizar el predio (ID: " + idPredio + ")");
        }

        return resultado;
    }

    /**
     * Eliminar (inactivar) un predio
     */
    public boolean eliminarPredio(int idPredio) {
        boolean resultado = predioDAO.eliminarPredio(idPredio);

        if (resultado) {
            System.out.println("🗑️ Predio inactivado correctamente (ID: " + idPredio + ")");
        } else {
            System.out.println("❌ Error al inactivar el predio (ID: " + idPredio + ")");
        }

        return resultado;
    }
}
