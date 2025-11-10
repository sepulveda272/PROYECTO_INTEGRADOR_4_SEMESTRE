/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Predio {
    private int Id_predio;
    private String Nombre_predio;
    private double Area_total;
    private String Nombre_propietario;
    private String Direccion;
    private double Coordenadas_lat; 
    private double Coordenadas_lon;
    private int Id_lugar;
    private String Lugar_label;
    private String Estado;

    public Predio() {
    }

    public Predio(int Id_predio, String Nombre_predio, double Area_total,
                  String Nombre_propietario, String Direccion,
                  double Coordenadas_lat, double Coordenadas_lon, int Id_lugar, String Estado) {

        this.Id_predio = Id_predio;
        this.Nombre_predio = Nombre_predio;
        this.Area_total = Area_total;
        this.Nombre_propietario = Nombre_propietario;
        this.Direccion = Direccion;
        this.Coordenadas_lat = Coordenadas_lat;
        this.Coordenadas_lon = Coordenadas_lon;
        this.Id_lugar = Id_lugar;
    }

    public int getId_predio() {
        return Id_predio;
    }

    public String getNombre_predio() {
        return Nombre_predio;
    }

    public double getArea_total() {
        return Area_total;
    }

    public String getNombre_propietario() {
        return Nombre_propietario;
    }

    public String getDireccion() {
        return Direccion;
    }

    public double getCoordenadas_lat() {
        return Coordenadas_lat;
    }

    public double getCoordenadas_lon() {
        return Coordenadas_lon;
    }

    public String getEstado() {
        return Estado;
    }

    public int getId_lugar() {
        return Id_lugar;
    }
    
    public String getLugar_label() {
        return Lugar_label;
    }

    public void setId_predio(int Id_predio) {
        this.Id_predio = Id_predio;
    }

    public void setNombre_predio(String Nombre_predio) {
        this.Nombre_predio = Nombre_predio;
    }

    public void setArea_total(double Area_total) {
        this.Area_total = Area_total;
    }

    public void setNombre_propietario(String Nombre_propietario) {
        this.Nombre_propietario = Nombre_propietario;
    }

    public void setDireccion(String Direccion) {
        this.Direccion = Direccion;
    }

    public void setCoordenadas_lat(double Coordenadas_lat) {
        this.Coordenadas_lat = Coordenadas_lat;
    }

    public void setCoordenadas_lon(double Coordenadas_lon) {
        this.Coordenadas_lon = Coordenadas_lon;
    }

    public void setEstado(String Estado) {
        this.Estado = Estado;
    }

    public void setId_lugar(int Id_lugar) {
        this.Id_lugar = Id_lugar;
    }
    
    public void setLugar_label( String Lugar_label){
        this.Lugar_label = Lugar_label;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
