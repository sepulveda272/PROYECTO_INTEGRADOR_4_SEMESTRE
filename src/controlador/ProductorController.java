/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Productor;
import modelo.ProductorDAO;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author ADMIN
 */
public class ProductorController {
    private final ProductorDAO productorDAO;

    public ProductorController() {
        this.productorDAO = new ProductorDAO();
    }

    /* ======================= Validaciones mínimas ======================= */
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

    /* ============================ CREATE ============================ */

    /** Alta automática (ID desde secuencia seq_producto). Retorna ID generado (>0) o -1. */
    public int agregarProductorAuto(long numeroIdentificacion, String tipoIdentificacion,
                                    String primerNombre, String segundoNombre, String primerApellido,
                                    String segundoApellido, String direccion, long celular,
                                    String correo, String password) {

        String err = validarAlta(numeroIdentificacion, tipoIdentificacion, primerNombre, primerApellido, direccion, celular, correo, password);
        if (err != null) { System.out.println("❌ " + err); return -1; }

        Productor p = new Productor();
        p.setNumero_identificacion(numeroIdentificacion);
        p.setTipo_identificacion(tipoIdentificacion);
        p.setPrimer_nombre(primerNombre);
        p.setSegundo_nombre(segundoNombre);
        p.setPrimer_apellido(primerApellido);
        p.setSegundo_apellido(segundoApellido);
        p.setDireccion(direccion);
        p.setCelular(celular);
        p.setCorreo(correo);
        p.setPassword(password); // el DAO lo hashea
        p.setEstado("ACTIVO");

        int nuevoId = productorDAO.insertarProductorAuto(p);
        if (nuevoId > 0) System.out.println("✅ Productor creado con ID " + nuevoId + " (secuencia).");
        else             System.out.println("❌ Error al crear el productor (ID automático).");
        return nuevoId;
    }

    /* ============================= READ ============================= */

    /** Lista solo activos. */
    public List<Productor> listarProductoresActivos() {
        List<Productor> productores = productorDAO.listarProductoresActivos();
        if (productores.isEmpty()) System.out.println("⚠️ No hay productores activos.");
        else                       System.out.println("✅ Productores activos: " + productores.size());
        return productores;
    }

    /** Lista todos (activos + inactivos). */
    public List<Productor> listarProductores() {
        List<Productor> productores = productorDAO.listarProductores();
        if (productores.isEmpty()) System.out.println("ℹ️ No hay productores registrados.");
        else                       System.out.println("✅ Total productores: " + productores.size());
        return productores;
    }

    /* ============================ UPDATE ============================ */

    public boolean actualizarProductor(int idProductor, long numeroIdentificacion, String tipoIdentificacion,
                                       String primerNombre, String segundoNombre, String primerApellido,
                                       String segundoApellido, String direccion, long celular,
                                       String correo, String estado) {

        if (!productorDAO.existeProductor(idProductor)) {
            System.out.println("⚠️ No existe productor con ID " + idProductor + ". Nada que actualizar.");
            return false;
        }

        Productor p = new Productor();
        p.setId_productor(idProductor);
        p.setNumero_identificacion(numeroIdentificacion);
        p.setTipo_identificacion(tipoIdentificacion);
        p.setPrimer_nombre(primerNombre);
        p.setSegundo_nombre(segundoNombre);
        p.setPrimer_apellido(primerApellido);
        p.setSegundo_apellido(segundoApellido);
        p.setDireccion(direccion);
        p.setCelular(celular);
        p.setCorreo(correo);
        p.setEstado(estado);

        boolean ok = productorDAO.actualizarProductor(p);
        if (ok) System.out.println("✅ Productor actualizado (ID: " + idProductor + ").");
        else    System.out.println("❌ Error al actualizar el productor (ID: " + idProductor + ").");
        return ok;
    }

    /** Cambio de contraseña (el DAO aplica BCrypt). */
    public boolean actualizarPassword(int idProductor, String nuevoPassword) {
        if (!productorDAO.existeProductor(idProductor)) {
            System.out.println("⚠️ No existe productor con ID " + idProductor + ".");
            return false;
        }
        if (nuevoPassword == null || nuevoPassword.isEmpty()) {
            System.out.println("❌ La contraseña no puede ser vacía.");
            return false;
        }
        boolean ok = productorDAO.actualizarPassword(idProductor, nuevoPassword);
        if (ok) System.out.println("✅ Contraseña actualizada (ID: " + idProductor + ").");
        else    System.out.println("❌ Error al actualizar la contraseña (ID: " + idProductor + ").");
        return ok;
    }

    /* ====================== DELETE (soft delete) ===================== */

    public boolean eliminarProductor(int idProductor) {
        if (!productorDAO.existeProductor(idProductor)) {
            System.out.println("⚠️ No existe productor con ID " + idProductor + ". Nada que eliminar.");
            return false;
        }
        boolean ok = productorDAO.eliminarProductor(idProductor);
        if (ok) System.out.println("✅ Productor inactivado (ID: " + idProductor + ").");
        else    System.out.println("❌ Error al inactivar el productor (ID: " + idProductor + ").");
        return ok;
    }

    /* ===================== EXISTS / LOGIN helpers ===================== */

    public boolean existeIdProductor(int idProductor) {
        return productorDAO.existeProductor(idProductor);
    }

    public boolean existeProductorActivo(int idProductor) {
        return productorDAO.existeProductorActivo(idProductor);
    }

    /** Login: retorna ID si OK + ACTIVO; si no, null. */
    public Integer iniciarSesion(String correo, String passwordPlano) {
        Integer id = productorDAO.validarProductor(correo, passwordPlano);
        if (id != null) System.out.println("✅ Inicio de sesión (Productor ID: " + id + ").");
        else            System.out.println("❌ Credenciales inválidas o productor inactivo.");
        return id;
    }
}
