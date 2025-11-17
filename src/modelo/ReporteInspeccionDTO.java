/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author ADMIN
 */
public class ReporteInspeccionDTO {

    private String productor;        // nombre completo productor
    private String predio;           // nombre_predio
    private String cultivo;          // especie + variedad
    private String plaga;            // nombre común (y/o científica)
    private String tecnico;          // nombre completo técnico
    private java.time.LocalDate fechaInspeccion;
    private int plantasRevisadas;
    private int plantasAfectadas;
    private String nivelAlerta;

    // opcionales:
    private String municipio;
    private String vereda;

    public ReporteInspeccionDTO() {
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public String getCultivo() {
        return cultivo;
    }

    public void setCultivo(String cultivo) {
        this.cultivo = cultivo;
    }

    public String getPlaga() {
        return plaga;
    }

    public void setPlaga(String plaga) {
        this.plaga = plaga;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public java.time.LocalDate getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(java.time.LocalDate fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    public int getPlantasRevisadas() {
        return plantasRevisadas;
    }

    public void setPlantasRevisadas(int plantasRevisadas) {
        this.plantasRevisadas = plantasRevisadas;
    }

    public int getPlantasAfectadas() {
        return plantasAfectadas;
    }

    public void setPlantasAfectadas(int plantasAfectadas) {
        this.plantasAfectadas = plantasAfectadas;
    }

    public String getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(String nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getVereda() {
        return vereda;
    }

    public void setVereda(String vereda) {
        this.vereda = vereda;
    }
}

