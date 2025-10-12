/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Productor;
import modelo.ProductorDAO;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ProductorController {
    private ProductorDAO productorDAO;
    
    public ProductorController(){
        this.productorDAO = new ProductorDAO();
    }
    
    public void agregarProductor(int idProductor, long numeroIdentificacion, String tipoIdentificacion,String primerNombre, String segundoNombre, String primerApellido,String segundoApellido, String direccion, long celular,String correo, String password) {
    
        // Crear objeto Productor con los datos recibidos
        Productor productor = new Productor(
            idProductor,
            numeroIdentificacion,
            tipoIdentificacion,
            primerNombre,
            segundoNombre,
            primerApellido,
            segundoApellido,
            direccion,
            celular,
            correo,
            password,
            "ACTIVO" // Estado por defecto
        );

        // Insertar usando el DAO
        boolean resultado = productorDAO.insertarProductor(productor);

        // Validar resultado
        if (resultado) {
            System.out.println("✅ Productor agregado con éxito.");
        } else {
            System.out.println("❌ Error al agregar el productor.");
        }
    }
    
    public List<Productor> listarProductores() {
        List<Productor> productores = productorDAO.listarProductoresActivos();

        if (productores.isEmpty()) {
            System.out.println("⚠️ No hay productores activos registrados.");
        } else {
            System.out.println("✅ Se encontraron " + productores.size() + " productores activos.");
        }

        return productores;
    }

    public boolean actualizarProductor(int idProductor, long numeroIdentificacion, String tipoIdentificacion,String primerNombre, String segundoNombre, String primerApellido,String segundoApellido, String direccion, long celular,String correo, String estado) {
         // Crear objeto con los datos actualizados
        Productor productor = new Productor();
        productor.setId_productor(idProductor);
        productor.setNumero_identificacion(numeroIdentificacion);
        productor.setTipo_identificacion(tipoIdentificacion);
        productor.setPrimer_nombre(primerNombre);
        productor.setSegundo_nombre(segundoNombre);
        productor.setPrimer_apellido(primerApellido);
        productor.setSegundo_apellido(segundoApellido);
        productor.setDireccion(direccion);
        productor.setCelular(celular);
        productor.setCorreo(correo);
        productor.setEstado(estado);

        // Llamar al DAO para actualizar
        boolean resultado = productorDAO.actualizarProductor(productor);

        // Mensaje de control
        if (resultado) {
            System.out.println("✅ Productor actualizado correctamente (ID: " + idProductor + ")");
        } else {
            System.out.println("❌ Error al actualizar el productor (ID: " + idProductor + ")");
        }

        return resultado;
    }
    
    public void eliminarProductor(int idProductor) {
        boolean resultado = productorDAO.eliminarProductor(idProductor);

        if (resultado) {
            System.out.println("✅ Productor eliminado (estado cambiado a INACTIVO).");
        } else {
            System.out.println("❌ Error al eliminar el productor con ID: " + idProductor);
        }
    }


}
