/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.FuncionarioICA;
import modelo.FuncionarioICADAO;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class FuncionarioICAController {
    private FuncionarioICADAO funcionarioICADAO;
    
    public FuncionarioICAController(){
        this.funcionarioICADAO = new FuncionarioICADAO();
    }
    
    public boolean agregarFuncionarioICA(int idFuncionario, long numeroIdentificacion, String tipoIdentificacion,String primerNombre, String segundoNombre, String primerApellido,String segundoApellido, long celular,String correo, String password) {
        
        // Evitar duplicados por ID
        if (funcionarioICADAO.existeFuncionario(idFuncionario)) {
            System.out.println("❌ Ya existe un funcionario con ID " + idFuncionario + ".");
            return false;
        }
        
        // Crear objeto Productor con los datos recibidos
        FuncionarioICA funcionario = new FuncionarioICA(
            idFuncionario,
            numeroIdentificacion,
            tipoIdentificacion,
            primerNombre,
            segundoNombre,
            primerApellido,
            segundoApellido,
            celular,
            correo,
            password,
            "ACTIVO" // Estado por defecto
        );

        // Insertar usando el DAO
        boolean resultado = funcionarioICADAO.insertarFuncionario(funcionario);

        // Validar resultado
        if (resultado) {
            System.out.println("✅ Funcionario agregado con éxito.");
        } else {
            System.out.println("❌ Error al agregar el Tecnico.");
        }
        return resultado;
    }
    
    // En PlagaController
    public boolean existeIdFuncionario(int idFuncionario) {
        return funcionarioICADAO.existeFuncionario(idFuncionario);
    }
    
    public List<FuncionarioICA> listarFuncionario() {
        List<FuncionarioICA> funcionarios = funcionarioICADAO.listarFuncionarios();

        if (funcionarios.isEmpty()) {
            System.out.println("⚠️ No hay Funcionario activos registrados.");
        } else {
            System.out.println("✅ Se encontraron " + funcionarios.size() + " funcionarios activos.");
        }

        return funcionarios;
    }

    public boolean actualizarFuncionario(int idFuncionario, long numeroIdentificacion, String tipoIdentificacion,String primerNombre, String segundoNombre, String primerApellido,String segundoApellido, long celular,String correo, String estado) {
         // Crear objeto con los datos actualizados
        FuncionarioICA funcionarioICA = new FuncionarioICA();
        funcionarioICA.setId_funcionario(idFuncionario);
        funcionarioICA.setNumero_identificacion(numeroIdentificacion);
        funcionarioICA.setTipo_identificacion(tipoIdentificacion);
        funcionarioICA.setPrimer_nombre(primerNombre);
        funcionarioICA.setSegundo_nombre(segundoNombre);
        funcionarioICA.setPrimer_apellido(primerApellido);
        funcionarioICA.setSegundo_apellido(segundoApellido);
        funcionarioICA.setCelular(celular);
        funcionarioICA.setCorreo(correo);
        funcionarioICA.setEstado(estado);

        // Llamar al DAO para actualizar
        boolean resultado = funcionarioICADAO.actualizarFuncionario(funcionarioICA);

        // Mensaje de control
        if (resultado) {
            System.out.println("✅ Funcionario actualizado correctamente (ID: " + idFuncionario + ")");
        } else {
            System.out.println("❌ Error al actualizar el funcionario (ID: " + idFuncionario + ")");
        }

        return resultado;
    }
    
    public void eliminarFuncionario(int idFuncionario) {
        boolean resultado = funcionarioICADAO.eliminarFuncionario(idFuncionario);

        if (resultado) {
            System.out.println("✅ Tecnico eliminado (estado cambiado a INACTIVO).");
        } else {
            System.out.println("❌ Error al eliminar el tecnico con ID: " + idFuncionario);
        }
    }
}
