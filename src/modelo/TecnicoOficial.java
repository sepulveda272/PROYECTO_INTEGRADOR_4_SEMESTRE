/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class TecnicoOficial {
    private int Numero_registro;          // PK
    private long Numero_identificacion;
    private String Tipo_identificacion;
    private String Primer_nombre;
    private String Segundo_nombre;        // opcional
    private String Primer_apellido;
    private String Segundo_apellido;      // opcional
    private String Direccion;
    private long Celular;
    private String Correo;
    private String Estado;
    private String Password;

    public TecnicoOficial() {
    }

    // Constructor completo (incluye campos opcionales)
    public TecnicoOficial(int Numero_registro, long Numero_identificacion, String Tipo_identificacion,
                          String Primer_nombre, String Segundo_nombre, String Primer_apellido,
                          String Segundo_apellido, String Direccion, long Celular,
                          String Correo, String Estado, String Password) {
        this.Numero_registro = Numero_registro;
        this.Numero_identificacion = Numero_identificacion;
        this.Tipo_identificacion = Tipo_identificacion;
        this.Primer_nombre = Primer_nombre;
        this.Segundo_nombre = Segundo_nombre;     // puede ser null
        this.Primer_apellido = Primer_apellido;
        this.Segundo_apellido = Segundo_apellido; // puede ser null
        this.Direccion = Direccion;
        this.Celular = Celular;
        this.Correo = Correo;
        this.Estado = Estado;
        this.Password = Password;
    }

    // Constructor solo con obligatorios (sin los opcionales)
    public TecnicoOficial(int Numero_registro, long Numero_identificacion, String Tipo_identificacion,
                          String Primer_nombre, String Primer_apellido,
                          String Direccion, long Celular, String Correo,
                          String Estado, String Password) {
        this(Numero_registro, Numero_identificacion, Tipo_identificacion,
             Primer_nombre, null, Primer_apellido, null, Direccion,
             Celular, Correo, Estado, Password);
    }

    public int getNumero_registro() {
        return Numero_registro;
    }
    
    public long getNumero_identificacion() {
        return Numero_identificacion; 
    }
    
    public String getTipo_identificacion() {
        return Tipo_identificacion; 
    }
    
    public String getPrimer_nombre() {
        return Primer_nombre; 
    }
    
    public String getSegundo_nombre() {
        return Segundo_nombre; 
    }
    
    public String getPrimer_apellido() { 
        return Primer_apellido; 
    }
    
    public String getSegundo_apellido() { 
        return Segundo_apellido; 
    }
    
    public String getDireccion() {
        return Direccion; 
    }
    
    public long getCelular() {
        return Celular; 
    }
    
    public String getCorreo() { 
        return Correo; 
    }
    
    public String getEstado() {
        return Estado;
    }
    
    public String getPassword() {
        return Password; 
    }
    

    public void setNumero_registro(int Numero_registro) {
        this.Numero_registro = Numero_registro; 
    }
    
    public void setNumero_identificacion(long Numero_identificacion) { 
        this.Numero_identificacion = Numero_identificacion;
    }
    
    public void setTipo_identificacion(String Tipo_identificacion) {
        this.Tipo_identificacion = Tipo_identificacion;
    }
    
    public void setPrimer_nombre(String Primer_nombre) { 
        this.Primer_nombre = Primer_nombre;
    }
    
    public void setSegundo_nombre(String Segundo_nombre) {
        this.Segundo_nombre = Segundo_nombre; 
    }
    
    public void setPrimer_apellido(String Primer_apellido) {
        this.Primer_apellido = Primer_apellido;
    }
    
    public void setSegundo_apellido(String Segundo_apellido) { this.Segundo_apellido = Segundo_apellido; }
    public void setDireccion(String Direccion) { this.Direccion = Direccion; }
    public void setCelular(long Celular) { this.Celular = Celular; }
    public void setCorreo(String Correo) { this.Correo = Correo; }
    public void setEstado(String Estado) { this.Estado = Estado; }
    public void setPassword(String Password) { this.Password = Password; }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
