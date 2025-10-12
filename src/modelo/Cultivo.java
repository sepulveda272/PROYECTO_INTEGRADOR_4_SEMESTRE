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
    private double Area_siembra;
    private String Descripcion;         // opcional
    private String Estado_fenologico;

    public Cultivo() {
    }

    // Constructor completo (incluye Descripcion)
    public Cultivo(int Id_cultivo, String Nombre_especie, String Variedad,
                   double Area_siembra, String Descripcion, String Estado_fenologico) {
        this.Id_cultivo = Id_cultivo;
        this.Nombre_especie = Nombre_especie;
        this.Variedad = Variedad;
        this.Area_siembra = Area_siembra;
        this.Descripcion = Descripcion; // puede ser null
        this.Estado_fenologico = Estado_fenologico;
    }

    // Constructor SIN Descripcion (por ser opcional)
    public Cultivo(int Id_cultivo, String Nombre_especie, String Variedad,
                   double Area_siembra, String Estado_fenologico) {
        this(Id_cultivo, Nombre_especie, Variedad, Area_siembra, null, Estado_fenologico);
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

    public double getArea_siembra() {
        return Area_siembra;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public String getEstado_fenologico() {
        return Estado_fenologico;
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

    public void setArea_siembra(double Area_siembra) {
        this.Area_siembra = Area_siembra;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public void setEstado_fenologico(String Estado_fenologico) {
        this.Estado_fenologico = Estado_fenologico;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
