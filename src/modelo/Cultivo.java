/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Cultivo {
    private int Id_cultivo;
    private String Nombre_especie;
    private String Variedad;
    private String Descripcion;         // opcional

    public Cultivo() {
    }

    // Constructor completo (incluye Descripcion)
    public Cultivo(int Id_cultivo, String Nombre_especie, String Variedad, String Descripcion) {
        this.Id_cultivo = Id_cultivo;
        this.Nombre_especie = Nombre_especie;
        this.Variedad = Variedad;
        this.Descripcion = Descripcion; // puede ser null
    }

    // Constructor SIN Descripcion (por ser opcional)
    public Cultivo(int Id_cultivo, String Nombre_especie, String Variedad) {
        this(Id_cultivo, Nombre_especie, Variedad, null);
    }

    public int getId_cultivo() {
        return Id_cultivo;
    }

    public String getNombre_especie() {
        return Nombre_especie;
    }

    public String getVariedad() {
        return Variedad;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setId_cultivo(int Id_cultivo) {
        this.Id_cultivo = Id_cultivo;
    }

    public void setNombre_especie(String Nombre_especie) {
        this.Nombre_especie = Nombre_especie;
    }

    public void setVariedad(String Variedad) {
        this.Variedad = Variedad;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
