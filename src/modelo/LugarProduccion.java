/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class LugarProduccion {
    private int Id_lugar;
    private String Departamento;
    private String Municipio;
    private String Vereda;
    private int Cantidad_maxima;
    private int Id_productor;

    public LugarProduccion() {
    }

    public LugarProduccion(int Id_lugar, String Departamento, String Municipio,
                           String Vereda, int Cantidad_maxima, int Id_productor) {
        this.Id_lugar = Id_lugar;
        this.Departamento = Departamento;
        this.Municipio = Municipio;
        this.Vereda = Vereda;
        this.Cantidad_maxima = Cantidad_maxima;
        this.Id_productor = Id_productor;
    }

    public int getId_lugar() {
        return Id_lugar;
    }

    public String getDepartamento() {
        return Departamento;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public String getVereda() {
        return Vereda;
    }

    public int getCantidad_maxima() {
        return Cantidad_maxima;
    }

    public int getId_productor() {
        return Id_productor;
    }

    public void setId_lugar(int Id_lugar) {
        this.Id_lugar = Id_lugar;
    }

    public void setDepartamento(String Departamento) {
        this.Departamento = Departamento;
    }

    public void setMunicipio(String Municipio) {
        this.Municipio = Municipio;
    }

    public void setVereda(String Vereda) {
        this.Vereda = Vereda;
    }

    public void setCantidad_maxima(int Cantidad_maxima) {
        this.Cantidad_maxima = Cantidad_maxima;
    }

    public void setId_productor(int Id_productor) {
        this.Id_productor = Id_productor;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
