/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.TecnicoOficial;
import modelo.TecnicoOficialDAO;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class TecnicoOficialController {
    private TecnicoOficialDAO tecnicoOficialDAO;
    
    public TecnicoOficialController(){
        this.tecnicoOficialDAO = new TecnicoOficialDAO();
    }
    
    public void agregarTecnico(int numeroRegistro, long numeroIdentificacion, String tipoIdentificacion,String primerNombre, String segundoNombre, String primerApellido,String segundoApellido, String direccion, long celular,String correo, String password) {
    
        // Crear objeto Productor con los datos recibidos
        TecnicoOficial tecnico = new TecnicoOficial(
            numeroRegistro,
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
        boolean resultado = tecnicoOficialDAO.insertarTecnico(tecnico);

        // Validar resultado
        if (resultado) {
            System.out.println("✅ Tecnico agregado con éxito.");
        } else {
            System.out.println("❌ Error al agregar el Tecnico.");
        }
    }
    
    public List<TecnicoOficial> listarTecnico() {
        List<TecnicoOficial> tecnicos = tecnicoOficialDAO.listarTecnicosActivos();

        if (tecnicos.isEmpty()) {
            System.out.println("⚠️ No hay tecnicos activos registrados.");
        } else {
            System.out.println("✅ Se encontraron " + tecnicos.size() + " tecnicos activos.");
        }

        return tecnicos;
    }

    public boolean actualizarTecnico(int numeroRegistro, long numeroIdentificacion, String tipoIdentificacion,String primerNombre, String segundoNombre, String primerApellido,String segundoApellido, String direccion, long celular,String correo, String estado) {
         // Crear objeto con los datos actualizados
        TecnicoOficial tecnicoOficial = new TecnicoOficial();
        tecnicoOficial.setNumero_registro(numeroRegistro);
        tecnicoOficial.setNumero_identificacion(numeroIdentificacion);
        tecnicoOficial.setTipo_identificacion(tipoIdentificacion);
        tecnicoOficial.setPrimer_nombre(primerNombre);
        tecnicoOficial.setSegundo_nombre(segundoNombre);
        tecnicoOficial.setPrimer_apellido(primerApellido);
        tecnicoOficial.setSegundo_apellido(segundoApellido);
        tecnicoOficial.setDireccion(direccion);
        tecnicoOficial.setCelular(celular);
        tecnicoOficial.setCorreo(correo);
        tecnicoOficial.setEstado(estado);

        // Llamar al DAO para actualizar
        boolean resultado = tecnicoOficialDAO.actualizarTecnico(tecnicoOficial);

        // Mensaje de control
        if (resultado) {
            System.out.println("✅ Tecnico actualizado correctamente (ID: " + numeroRegistro + ")");
        } else {
            System.out.println("❌ Error al actualizar el tecnico (ID: " + numeroRegistro + ")");
        }

        return resultado;
    }
    
    public void eliminarTecnico(int numeroRegistro) {
        boolean resultado = tecnicoOficialDAO.eliminarTecnico(numeroRegistro);

        if (resultado) {
            System.out.println("✅ Tecnico eliminado (estado cambiado a INACTIVO).");
        } else {
            System.out.println("❌ Error al eliminar el tecnico con ID: " + numeroRegistro);
        }
    }
}
