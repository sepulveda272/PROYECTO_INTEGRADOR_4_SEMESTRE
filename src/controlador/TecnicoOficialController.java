/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.TecnicoOficial;
import modelo.TecnicoOficialDAO;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author ADMIN
 */
public class TecnicoOficialController {
    private final TecnicoOficialDAO tecnicoOficialDAO;

    public TecnicoOficialController() {
        this.tecnicoOficialDAO = new TecnicoOficialDAO();
    }

    /* ====== Validaciones mínimas (mismas que ya usabas) ====== */
    private static final Pattern EMAIL_RX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private String validarAlta(long numeroIdentificacion, String tipoIdentificacion,
                               String primerNombre, String primerApellido, String direccion,
                               long celular, String correo, String password) {
        if (numeroIdentificacion <= 0) return "numero_identificacion debe ser > 0.";
        if (tipoIdentificacion == null || tipoIdentificacion.trim().isEmpty()) return "tipo_identificacion es obligatorio.";
        if (primerNombre == null || primerNombre.trim().isEmpty()) return "primer_nombre es obligatorio.";
        if (primerApellido == null || primerApellido.trim().isEmpty()) return "primer_apellido es obligatorio.";
        if (direccion == null || direccion.trim().isEmpty()) return "direccion es obligatoria.";
        if (celular <= 0) return "celular debe ser > 0.";
        if (correo == null || correo.trim().isEmpty()) return "correo es obligatorio.";
        if (!EMAIL_RX.matcher(correo.trim()).matches()) return "correo con formato inválido.";
        if (password == null || password.isEmpty()) return "password es obligatorio.";
        return null;
    }

    /* ===================== CREATE ===================== */

    /** Alta con ID automático (usa seq_tecnico). Retorna el ID generado (>0) o -1 si falla. */
    public int agregarTecnicoAuto(long numeroIdentificacion, String tipoIdentificacion,
                                  String primerNombre, String segundoNombre, String primerApellido,
                                  String segundoApellido, String direccion, long celular,
                                  String correo, String password) {

        String err = validarAlta(numeroIdentificacion, tipoIdentificacion, primerNombre, primerApellido, direccion, celular, correo, password);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        TecnicoOficial t = new TecnicoOficial();
        t.setNumero_identificacion(numeroIdentificacion);
        t.setTipo_identificacion(tipoIdentificacion);
        t.setPrimer_nombre(primerNombre);
        t.setSegundo_nombre(segundoNombre);
        t.setPrimer_apellido(primerApellido);
        t.setSegundo_apellido(segundoApellido);
        t.setDireccion(direccion);
        t.setCelular(celular);
        t.setCorreo(correo);
        t.setPassword(password);   // el DAO lo hashea
        t.setEstado("ACTIVO");     // default; la tabla también lo pone por defecto

        int nuevoId = tecnicoOficialDAO.insertarTecnicoAuto(t);
        if (nuevoId > 0) System.out.println("✅ Técnico agregado con ID " + nuevoId + " (secuencia).");
        else              System.out.println("❌ Error al agregar el técnico (ID automático).");
        return nuevoId;
    }

    /* ===================== READ ===================== */

    public List<TecnicoOficial> listarTecnicosActivos() {
        List<TecnicoOficial> tecnicos = tecnicoOficialDAO.listarTecnicosActivos();
        if (tecnicos.isEmpty()) System.out.println("⚠️ No hay técnicos activos registrados.");
        else                    System.out.println("✅ Se encontraron " + tecnicos.size() + " técnicos activos.");
        return tecnicos;
    }

    public List<TecnicoOficial> listarTecnicos() {
        List<TecnicoOficial> tecnicos = tecnicoOficialDAO.listarTecnicos();
        if (tecnicos.isEmpty()) System.out.println("ℹ️ No hay técnicos registrados.");
        else                    System.out.println("✅ Total técnicos: " + tecnicos.size() + ".");
        return tecnicos;
    }

    /* ===================== UPDATE ===================== */

    public boolean actualizarTecnico(int numeroRegistro, long numeroIdentificacion, String tipoIdentificacion,
                                     String primerNombre, String segundoNombre, String primerApellido,
                                     String segundoApellido, String direccion, long celular,
                                     String correo, String estado) {

        if (!tecnicoOficialDAO.existeTecnico(numeroRegistro)) {
            System.out.println("⚠️ No existe técnico con ID " + numeroRegistro + ". Nada que actualizar.");
            return false;
        }

        TecnicoOficial t = new TecnicoOficial();
        t.setNumero_registro(numeroRegistro);
        t.setNumero_identificacion(numeroIdentificacion);
        t.setTipo_identificacion(tipoIdentificacion);
        t.setPrimer_nombre(primerNombre);
        t.setSegundo_nombre(segundoNombre);
        t.setPrimer_apellido(primerApellido);
        t.setSegundo_apellido(segundoApellido);
        t.setDireccion(direccion);
        t.setCelular(celular);
        t.setCorreo(correo);
        t.setEstado(estado);

        boolean ok = tecnicoOficialDAO.actualizarTecnico(t);
        if (ok) System.out.println("✅ Técnico actualizado (ID: " + numeroRegistro + ").");
        else    System.out.println("❌ Error al actualizar el técnico (ID: " + numeroRegistro + ").");
        return ok;
    }

    /** Cambio de contraseña: el DAO aplica BCrypt automáticamente. */
    public boolean actualizarPassword(int numeroRegistro, String nuevoPassword) {
        if (!tecnicoOficialDAO.existeTecnico(numeroRegistro)) {
            System.out.println("⚠️ No existe técnico con ID " + numeroRegistro + ".");
            return false;
        }
        if (nuevoPassword == null || nuevoPassword.isEmpty()) {
            System.out.println("❌ La contraseña no puede ser vacía.");
            return false;
        }
        boolean ok = tecnicoOficialDAO.actualizarPassword(numeroRegistro, nuevoPassword);
        if (ok) System.out.println("✅ Contraseña actualizada (ID: " + numeroRegistro + ").");
        else    System.out.println("❌ Error al actualizar la contraseña (ID: " + numeroRegistro + ").");
        return ok;
    }

    /* ===================== DELETE / REACTIVAR ===================== */

    public boolean desactivarTecnicoSeguro(int numeroRegistro) {
        if (!tecnicoOficialDAO.existeTecnico(numeroRegistro)) {
            System.out.println("⚠️ No existe técnico con ID " + numeroRegistro + ". Nada que inactivar.");
            return false;
        }
        boolean ok = tecnicoOficialDAO.desactivarTecnicoSiNoReferenciado(numeroRegistro);
        if (ok) {
            System.out.println("✅ Técnico inactivado (ID: " + numeroRegistro + ").");
        } else {
            System.out.println("❌ No se pudo inactivar: el técnico está referenciado por inspecciones o hubo un error.");
        }
        return ok;
    }

    /* ===================== EXISTS / LOGIN ===================== */

    public boolean existeIdTecnico(int numeroRegistro) {
        return tecnicoOficialDAO.existeTecnico(numeroRegistro);
    }

    public boolean existeTecnicoActivo(int numeroRegistro) {
        return tecnicoOficialDAO.existeTecnicoActivo(numeroRegistro);
    }

    /** Retorna el número de registro si login OK + ACTIVO; si no, null. */
    public Integer iniciarSesion(String correo, String password) {
        Integer id = tecnicoOficialDAO.validarTecnico(correo, password);
        if (id != null) System.out.println("✅ Inicio de sesión exitoso (ID: " + id + ").");
        else            System.out.println("❌ Credenciales inválidas o técnico inactivo.");
        return id;
    }
}
