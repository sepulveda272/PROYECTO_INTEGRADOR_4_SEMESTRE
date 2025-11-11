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
    
     /* ===================== AUTH / LOGIN ===================== */

    /**
     * Intenta iniciar sesión con correo y password.
     * @return id_funcionario si es correcto, null si falla.
     */
    public Integer iniciarSesion(String correo, String passwordPlano) {
        if (correo == null || correo.trim().isEmpty()
                || passwordPlano == null || passwordPlano.isEmpty()) {
            return null;
        }
        return funcionarioICADAO.validarFuncionario(correo.trim(), passwordPlano);
    }

    /* ===================== CREATE ===================== */

    /**
     * Registra un funcionario nuevo usando la secuencia (ID automático).
     * Devuelve mensaje para mostrar en la vista.
     */
    public String registrarFuncionario(
            String numeroIdentStr,
            String tipoIdentificacion,
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            String celularStr,
            String correo,
            String passwordPlano,
            String estado // puedes enviar null para que quede ACTIVO por defecto
    ) {
        // Validaciones básicas
        if (esBlank(numeroIdentStr) || esBlank(tipoIdentificacion) ||
            esBlank(primerNombre)   || esBlank(primerApellido)   ||
            esBlank(celularStr)     || esBlank(correo)           ||
            esBlank(passwordPlano)) {

            return "Debes diligenciar todos los campos obligatorios.";
        }

        long numeroIdent;
        long celular;
        try {
            numeroIdent = Long.parseLong(numeroIdentStr.trim());
            celular     = Long.parseLong(celularStr.trim());
        } catch (NumberFormatException e) {
            return "Número de identificación y celular deben ser numéricos.";
        }

        FuncionarioICA f = new FuncionarioICA();
        f.setNumero_identificacion(numeroIdent);
        f.setTipo_identificacion(tipoIdentificacion.trim());
        f.setPrimer_nombre(primerNombre.trim());
        f.setSegundo_nombre(esBlank(segundoNombre) ? null : segundoNombre.trim());
        f.setPrimer_apellido(primerApellido.trim());
        f.setSegundo_apellido(esBlank(segundoApellido) ? null : segundoApellido.trim());
        f.setCelular(celular);
        f.setCorreo(correo.trim());
        f.setPassword(passwordPlano);           // el DAO se encarga de hashearla
        f.setEstado(estado);                    // el DAO pone ACTIVO si viene null

        int nuevoId = funcionarioICADAO.insertarFuncionarioAuto(f);
        if (nuevoId > 0) {
            return "Funcionario registrado correctamente. ID: " + nuevoId;
        } else {
            return "No se pudo registrar el funcionario.";
        }
    }

    /* ===================== READ ===================== */

    public List<FuncionarioICA> obtenerFuncionarios() {
        return funcionarioICADAO.listarFuncionarios();
    }

    public List<FuncionarioICA> obtenerFuncionariosActivos() {
        return funcionarioICADAO.listarFuncionariosActivos();
    }

    /* ===================== UPDATE ===================== */

    /**
     * Actualiza un funcionario existente.
     */
    public String actualizarFuncionario(
            int idFuncionario,
            String numeroIdentStr,
            String tipoIdentificacion,
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            String celularStr,
            String correo,
            String estado
    ) {
        if (!funcionarioICADAO.existeFuncionario(idFuncionario)) {
            return "No existe el funcionario con ID " + idFuncionario;
        }

        if (esBlank(numeroIdentStr) || esBlank(tipoIdentificacion) ||
            esBlank(primerNombre)   || esBlank(primerApellido)   ||
            esBlank(celularStr)     || esBlank(correo)           ||
            esBlank(estado)) {

            return "Debes diligenciar todos los campos obligatorios.";
        }

        long numeroIdent;
        long celular;
        try {
            numeroIdent = Long.parseLong(numeroIdentStr.trim());
            celular     = Long.parseLong(celularStr.trim());
        } catch (NumberFormatException e) {
            return "Número de identificación y celular deben ser numéricos.";
        }

        FuncionarioICA f = new FuncionarioICA();
        f.setId_funcionario(idFuncionario);
        f.setNumero_identificacion(numeroIdent);
        f.setTipo_identificacion(tipoIdentificacion.trim());
        f.setPrimer_nombre(primerNombre.trim());
        f.setSegundo_nombre(esBlank(segundoNombre) ? null : segundoNombre.trim());
        f.setPrimer_apellido(primerApellido.trim());
        f.setSegundo_apellido(esBlank(segundoApellido) ? null : segundoApellido.trim());
        f.setCelular(celular);
        f.setCorreo(correo.trim());
        f.setEstado(estado.trim());

        boolean ok = funcionarioICADAO.actualizarFuncionario(f);
        if (ok) {
            return "Funcionario actualizado correctamente.";
        } else {
            return "No se pudo actualizar el funcionario.";
        }
    }

    /**
     * Cambia el password de un funcionario.
     */
    public String cambiarPassword(int idFuncionario, String nuevoPasswordPlano) {
        if (!funcionarioICADAO.existeFuncionario(idFuncionario)) {
            return "No existe el funcionario con ID " + idFuncionario;
        }
        if (esBlank(nuevoPasswordPlano)) {
            return "El password no puede estar vacío.";
        }
        boolean ok = funcionarioICADAO.actualizarPassword(idFuncionario, nuevoPasswordPlano);
        return ok ? "Contraseña actualizada correctamente."
                  : "No se pudo actualizar la contraseña.";
    }

    /* ===================== DELETE LÓGICO ===================== */

    /**
     * Intenta desactivar el funcionario validando que no tenga inspecciones.
     */
    public String desactivarFuncionario(int idFuncionario) {
        boolean ok = funcionarioICADAO.desactivarFuncionarioSiNoReferenciado(idFuncionario);
        if (ok) {
            return "Funcionario desactivado correctamente.";
        } else {
            return "No se pudo desactivar el funcionario (revise mensajes en la consola).";
        }
    }

    /* ===================== Helpers ===================== */

    private boolean esBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
