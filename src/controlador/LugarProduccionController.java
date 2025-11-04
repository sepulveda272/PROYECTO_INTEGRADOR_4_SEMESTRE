/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.LugarProduccion;
import modelo.LugarProduccionDAO;
import modelo.ProductorDAO;
import java.util.List;
/**
 *
 * @author ADMIN
 */
public class LugarProduccionController {
    private LugarProduccionDAO lugarProduccionDAO;
    private ProductorDAO productorDAO;

    public LugarProduccionController() {
        this.lugarProduccionDAO = new LugarProduccionDAO();
        this.productorDAO = new ProductorDAO();
    }
    
    public boolean agregarLugarProduccion(int idLugar, String departamento, String municipio, 
                                       String vereda, int cantidadMaxima, int idProductor) {

        // Evitar duplicados por ID
        if (lugarProduccionDAO.existeLugarPro(idLugar)) {
            System.out.println("❌ Ya existe un lugar de produccion con ID " + idLugar + ".");
            return false;
        }
        
        if (!productorDAO.existeProductorActivo(idProductor)) {
            System.out.println("❌ El productor con ID " + idProductor + " no existe o no está activo.");
            return false;
        }
        
        // Crear objeto con los datos
        LugarProduccion lugar = new LugarProduccion(
            idLugar,
            departamento,
            municipio,
            vereda,
            cantidadMaxima,
            idProductor
        );

        // Insertar usando el DAO
        boolean resultado = lugarProduccionDAO.insertarLugarProduccion(lugar);

        // Validar resultado
        if (resultado) {
            System.out.println("✅ Lugar de producción agregado con éxito.");
        } else {
            System.out.println("❌ Error al agregar el lugar de producción.");
        }
        
        return resultado;
    }
    
    // En PlagaController
    public boolean existeIdLugarPro(int idLugar) {
        return lugarProduccionDAO.existeLugarPro(idLugar);
    }

    /**
     * Listar todos los lugares de producción
     */
    public List<LugarProduccion> listarLugaresProduccion() {
        List<LugarProduccion> lugares = lugarProduccionDAO.listarLugaresProduccion();

        if (lugares.isEmpty()) {
            System.out.println("⚠️ No hay lugares de producción registrados.");
        } else {
            System.out.println("✅ Se encontraron " + lugares.size() + " lugares de producción.");
        }

        return lugares;
    }

    /**
     * Actualizar un lugar de producción existente
     */
    public boolean actualizarLugarProduccion(int idLugar, String departamento, String municipio, 
                                             String vereda, int cantidadMaxima, int idProductor) {
        
        if (!productorDAO.existeProductorActivo(idProductor)) {
            System.out.println("❌ El productor con ID " + idProductor + " no existe o no está activo. No se puede actualizar.");
            return false;
        }

        // Crear objeto con los datos actualizados
        LugarProduccion lugar = new LugarProduccion();
        lugar.setId_lugar(idLugar);
        lugar.setDepartamento(departamento);
        lugar.setMunicipio(municipio);
        lugar.setVereda(vereda);
        lugar.setCantidad_maxima(cantidadMaxima);
        lugar.setId_productor(idProductor);

        // Llamar al DAO para actualizar
        boolean resultado = lugarProduccionDAO.actualizarLugarProduccion(lugar);

        // Mensaje de control
        if (resultado) {
            System.out.println("✅ Lugar de producción actualizado correctamente (ID: " + idLugar + ")");
        } else {
            System.out.println("❌ Error al actualizar el lugar de producción (ID: " + idLugar + ")");
        }

        return resultado;
    }

    /**
     * Eliminar un lugar de producción por ID
     */
    public void eliminarLugarProduccion(int idLugar) {
        boolean resultado = lugarProduccionDAO.eliminarLugarProduccion(idLugar);

        if (resultado) {
            System.out.println("✅ Lugar de producción eliminado correctamente (ID: " + idLugar + ")");
        } else {
            System.out.println("❌ Error al eliminar el lugar de producción con ID: " + idLugar);
        }
    }
}
