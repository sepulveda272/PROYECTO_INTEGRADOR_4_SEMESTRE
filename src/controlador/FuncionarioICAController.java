/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.FuncionarioICA;
import modelo.FuncionarioICADAO;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author ADMIN
 */
public class FuncionarioICAController {
    private final FuncionarioICADAO funcionarioICADAO;

    public FuncionarioICAController() {
        this.funcionarioICADAO = new FuncionarioICADAO();
    }

    /* ======================= Validaciones mínimas ======================= */
    private static final Pattern EMAIL_RX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /** Devuelve null si todo OK; en otro caso, el mensaje de error. */
    private String validarAltaFuncionario(long numeroIdentificacion, String tipoIdentificacion,
                                          String primerNombre, String primerApellido,
                                          long celular, String correo, String passwordPlano) {

        if (numeroIdentificacion <= 0) return "numero_identificacion debe ser > 0.";
        if (tipoIdentificacion == null || tipoIdentificacion.trim().isEmpty()) return "tipo_identificacion es obligatorio.";
        if (primerNombre == null || primerNombre.trim().isEmpty()) return "primer_nombre es obligatorio.";
        if (primerApellido == null || primerApellido.trim().isEmpty()) return "primer_apellido es obligatorio.";
        if (celular <= 0) return "celular debe ser > 0.";
        if (correo == null || correo.trim().isEmpty()) return "correo es obligatorio.";
        if (!EMAIL_RX.matcher(correo.trim()).matches()) return "correo con formato inválido.";
        if (passwordPlano == null || passwordPlano.isEmpty()) return "password es obligatorio.";

        // Longitudes (según tabla)
        if (tipoIdentificacion.trim().length() > 15) return "Tipo de identificación máx. 15 caracteres.";
        if (primerNombre.trim().length() > 50)       return "Primer nombre máx. 50 caracteres.";
        if (primerApellido.trim().length() > 50)     return "Primer apellido máx. 50 caracteres.";
        if (correo.trim().length() > 120)            return "Correo máx. 120 caracteres.";
        if (passwordPlano.length() > 120)            return "Password máx. 120 caracteres.";

        return null;
    }

    /* ===================== AUTH / LOGIN ===================== */

    /** Login: retorna ID si OK + ACTIVO; si no, null. */
    public Integer iniciarSesion(String correo, String passwordPlano) {
        if (correo == null || correo.trim().isEmpty()
                || passwordPlano == null || passwordPlano.isEmpty()) {
            return null;
        }
        return funcionarioICADAO.validarFuncionario(correo.trim(), passwordPlano);
    }

    /* ============================ CREATE ============================ */

    /** Alta automática (ID desde secuencia). Retorna ID generado (>0) o -1. */
    public int registrarFuncionarioAuto(long numeroIdentificacion,
                                        String tipoIdentificacion,
                                        String primerNombre,
                                        String segundoNombre,     // opcional
                                        String primerApellido,
                                        String segundoApellido,   // opcional
                                        long celular,
                                        String correo,
                                        String passwordPlano) {       // null => DEFAULT 'ACTIVO'
        String err = validarAltaFuncionario(numeroIdentificacion, tipoIdentificacion,
                                            primerNombre, primerApellido, celular,
                                            correo, passwordPlano);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        FuncionarioICA f = new FuncionarioICA();
        f.setNumero_identificacion(numeroIdentificacion);
        f.setTipo_identificacion(tipoIdentificacion.trim());
        f.setPrimer_nombre(primerNombre.trim());
        f.setSegundo_nombre((segundoNombre == null || segundoNombre.trim().isEmpty()) ? null : segundoNombre.trim());
        f.setPrimer_apellido(primerApellido.trim());
        f.setSegundo_apellido((segundoApellido == null || segundoApellido.trim().isEmpty()) ? null : segundoApellido.trim());
        f.setCelular(celular);
        f.setCorreo(correo.trim());
        f.setPassword(passwordPlano); // DAO hashea con BCrypt
        f.setEstado("ACTIVO");

        int nuevoId = funcionarioICADAO.insertarFuncionarioAuto(f);
        if (nuevoId > 0) System.out.println("✅ Funcionario creado con ID " + nuevoId + " (secuencia).");
        else             System.out.println("❌ Error al crear el funcionario (ID automático).");
        return nuevoId;
    }

    /* ============================= READ ============================= */

    /** Lista todos (activos + inactivos). */
    public List<FuncionarioICA> obtenerFuncionarios() {
        List<FuncionarioICA> lista = funcionarioICADAO.listarFuncionarios();
        if (lista.isEmpty()) System.out.println("ℹ️ No hay funcionarios registrados.");
        else                 System.out.println("✅ Total funcionarios: " + lista.size());
        return lista;
    }

    /** Lista solo activos. */
    public List<FuncionarioICA> obtenerFuncionariosActivos() {
        List<FuncionarioICA> lista = funcionarioICADAO.listarFuncionariosActivos();
        if (lista.isEmpty()) System.out.println("⚠️ No hay funcionarios activos.");
        else                 System.out.println("✅ Funcionarios activos: " + lista.size());
        return lista;
    }

    /* ============================ UPDATE ============================ */

    /** Actualiza un funcionario existente. Devuelve mensaje para la vista. */
    public boolean actualizarFuncionario(int idFuncionario,
                                        long numeroIdentificacion,
                                        String tipoIdentificacion,
                                        String primerNombre,
                                        String segundoNombre,
                                        String primerApellido,
                                        String segundoApellido,
                                        long celular,
                                        String correo,
                                        String estado) {

        if (!funcionarioICADAO.existeFuncionario(idFuncionario)) {
            System.out.println("⚠️ No existe un funcionario con ID " + idFuncionario + ". Nada que actualizar.");
            return false;
        }

        FuncionarioICA f = new FuncionarioICA();
        f.setId_funcionario(idFuncionario);
        f.setNumero_identificacion(numeroIdentificacion);
        f.setTipo_identificacion(tipoIdentificacion.trim());
        f.setPrimer_nombre(primerNombre.trim());
        f.setSegundo_nombre((segundoNombre == null || segundoNombre.trim().isEmpty()) ? null : segundoNombre.trim());
        f.setPrimer_apellido(primerApellido.trim());
        f.setSegundo_apellido((segundoApellido == null || segundoApellido.trim().isEmpty()) ? null : segundoApellido.trim());
        f.setCelular(celular);
        f.setCorreo(correo.trim());
        f.setEstado(estado.trim().toUpperCase());

        boolean ok = funcionarioICADAO.actualizarFuncionario(f);
        if (ok) System.out.println("✅ Funcionario actualizado (ID: " + idFuncionario + ").");
        else    System.out.println("❌ Error al actualizar el funcionario (ID: " + idFuncionario + ").");
        return ok;
    }

    /** Cambio de contraseña (el DAO aplica BCrypt). */
    public boolean actualizarPassword(int idFuncionario, String nuevoPasswordPlano) {
        if (!funcionarioICADAO.existeFuncionario(idFuncionario)) {
            System.out.println("⚠️ No existe funcionario con ID " + idFuncionario + ".");
            return false;
        }
        if (nuevoPasswordPlano == null || nuevoPasswordPlano.isEmpty()) {
            System.out.println("❌ La contraseña no puede ser vacía.");
            return false;
        }
        boolean ok = funcionarioICADAO.actualizarPassword(idFuncionario, nuevoPasswordPlano);
        if (ok) System.out.println("✅ Contraseña actualizada (ID: " + idFuncionario + ").");
        else    System.out.println("❌ Error al actualizar la contraseña (ID: " + idFuncionario + ").");
        return ok;
    }

    /* ====================== DELETE (soft delete) ===================== */

    /** Desactiva (estado = INACTIVO) si existe y no está referenciado por inspecciones. */
    public boolean desactivarFuncionarioSeguro(int idFuncionario) {
        if (!funcionarioICADAO.existeFuncionario(idFuncionario)) {
            System.out.println("⚠️ No existe funcionario con ID " + idFuncionario + ". Nada que inactivar.");
            return false;
        }
        boolean ok = funcionarioICADAO.desactivarFuncionarioSiNoReferenciado(idFuncionario);
        if (ok) System.out.println("✅ Funcionario inactivado (ID: " + idFuncionario + ").");
        else    System.out.println("❌ Error al inactivar: el funcionario está referenciado por inspecciones o hubo un error.");
        return ok;
    }

    /* ===================== EXISTS / HELPERS ===================== */

    public boolean existeIdFuncionario(int idFuncionario) {
        return funcionarioICADAO.existeFuncionario(idFuncionario);
    }

    public boolean existeFuncionarioActivo(int idFuncionario) {
        return funcionarioICADAO.existeFuncionarioActivo(idFuncionario);
    }

    /* ===================== Utils ===================== */
    private boolean esBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
