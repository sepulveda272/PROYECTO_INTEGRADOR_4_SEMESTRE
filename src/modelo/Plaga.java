/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Plaga {
    private int Id_plaga;
    private String Nombre_cientifico;
    private String Nombre_comun;
    private String Descripcion; // opcional

    public Plaga() {
    }

    // Constructor completo (incluye campo opcional)
    public Plaga(int Id_plaga, String Nombre_cientifico, String Nombre_comun, String Descripcion) {
        this.Id_plaga = Id_plaga;
        this.Nombre_cientifico = Nombre_cientifico;
        this.Nombre_comun = Nombre_comun;
        this.Descripcion = Descripcion; // puede ser null
    }

    // Constructor SIN el campo opcional
    public Plaga(int Id_plaga, String Nombre_cientifico, String Nombre_comun) {
        this(Id_plaga, Nombre_cientifico, Nombre_comun, null);
    }

    public int getId_plaga() {
        return Id_plaga;
    }

    public String getNombre_cientifico() {
        return Nombre_cientifico;
    }

    public String getNombre_comun() {
        return Nombre_comun;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setId_plaga(int Id_plaga) {
        this.Id_plaga = Id_plaga;
    }

    public void setNombre_cientifico(String Nombre_cientifico) {
        this.Nombre_cientifico = Nombre_cientifico;
    }

    public void setNombre_comun(String Nombre_comun) {
        this.Nombre_comun = Nombre_comun;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
