/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author ADMIN
 */
public class Productor {
    private int Id_productor;             
    private long Numero_identificacion;
    private String Tipo_identificacion;
    private String Primer_nombre;
    private String Segundo_nombre;         
    private String Primer_apellido;
    private String Segundo_apellido;      
    private String Direccion;
    private long Celular;
    private String Correo;
    private String Password;
    private String Estado;

    public Productor() {
    }

    // Constructor completo (incluye campos opcionales)
    public Productor(int Id_productor, long Numero_identificacion, String Tipo_identificacion,
                     String Primer_nombre, String Segundo_nombre, String Primer_apellido,
                     String Segundo_apellido, String Direccion, long Celular,
                     String Correo, String Password, String Estado) {
        this.Id_productor = Id_productor;
        this.Numero_identificacion = Numero_identificacion;
        this.Tipo_identificacion = Tipo_identificacion;
        this.Primer_nombre = Primer_nombre;
        this.Segundo_nombre = Segundo_nombre;       
        this.Primer_apellido = Primer_apellido;
        this.Segundo_apellido = Segundo_apellido;  
        this.Direccion = Direccion;
        this.Celular = Celular;
        this.Correo = Correo;
        this.Password = Password;
        this.Estado = Estado;
    }

    // Constructor solo con obligatorios (sin opcionales)
    public Productor(int Id_productor, long Numero_identificacion, String Tipo_identificacion,
                     String Primer_nombre, String Primer_apellido, String Direccion,
                     long Celular, String Correo, String Password, String Estado) {
        this(Id_productor, Numero_identificacion, Tipo_identificacion,
             Primer_nombre, null, Primer_apellido, null, Direccion,
             Celular, Correo, Password, Estado);
    }

    // Getters
    public int getId_productor() {
        return Id_productor;
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
    
    public String getPassword() {
        return Password; 
    }
    
    public String getEstado() {
        return Estado; 
    }
    

    // Setters
    public void setId_productor(int Id_productor) {
        this.Id_productor = Id_productor; 
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
    
    public void setSegundo_apellido(String Segundo_apellido) {
        this.Segundo_apellido = Segundo_apellido;
    }
    
    public void setDireccion(String Direccion) {
        this.Direccion = Direccion; 
    }
    
    public void setCelular(long Celular) {
        this.Celular = Celular; 
    }
    
    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }
    
    public void setPassword(String Password) {
        this.Password = Password;
    }
    
    public void setEstado(String Estado) {
        this.Estado = Estado;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

