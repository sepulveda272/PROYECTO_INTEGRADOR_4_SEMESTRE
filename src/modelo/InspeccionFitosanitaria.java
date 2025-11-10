/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class InspeccionFitosanitaria {
    private int Id_inspeccion;
    private int Plantas_revisadas;
    private int Plantas_afectadas;
    private String Fecha_inspeccion;
    private String Nivel_alerta;  // p.ej.: BAJO, MEDIO, ALTO
    private int Numero_lote;
    private int Id_tecnico;
    private String nombre_tecnico;

    public InspeccionFitosanitaria() {
    }

    public InspeccionFitosanitaria(int Id_inspeccion,
                                   int Plantas_revisadas,
                                   int Plantas_afectadas,
                                   String Fecha_inspeccion,
                                   String Nivel_alerta,
                                   int Numero_lote,
                                   int Id_tecnico) {
        this.Id_inspeccion = Id_inspeccion;
        this.Plantas_revisadas = Plantas_revisadas;
        this.Plantas_afectadas = Plantas_afectadas;
        this.Fecha_inspeccion = Fecha_inspeccion;
        this.Nivel_alerta = Nivel_alerta;
        this.Numero_lote = Numero_lote;
        this.Id_tecnico = Id_tecnico;
    }

    public int getId_inspeccion() {
        return Id_inspeccion;
    }

    public int getPlantas_revisadas() {
        return Plantas_revisadas;
    }

    public int getPlantas_afectadas() {
        return Plantas_afectadas;
    }

    public String getFecha_inspeccion() {
        return Fecha_inspeccion;
    }

    public String getNivel_alerta() {
        return Nivel_alerta;
    }

    public int getNumero_lote() {
        return Numero_lote;
    }

    public int getId_tecnico() {
        return Id_tecnico;
    }
    
    public String getNombre_tecnico() { 
        return nombre_tecnico; 
    }
    
    public void setNombre_tecnico(String nombre_tecnico) {
        this.nombre_tecnico = nombre_tecnico; 
    }

    public void setId_inspeccion(int Id_inspeccion) {
        this.Id_inspeccion = Id_inspeccion;
    }

    public void setPlantas_revisadas(int Plantas_revisadas) {
        this.Plantas_revisadas = Plantas_revisadas;
    }

    public void setPlantas_afectadas(int Plantas_afectadas) {
        this.Plantas_afectadas = Plantas_afectadas;
    }

    public void setFecha_inspeccion(String Fecha_inspeccion) {
        this.Fecha_inspeccion = Fecha_inspeccion;
    }

    public void setNivel_alerta(String Nivel_alerta) {
        this.Nivel_alerta = Nivel_alerta;
    }

    public void setNumero_lote(int Numero_lote) {
        this.Numero_lote = Numero_lote;
    }

    public void setId_tecnico(int Id_tecnico) {
        this.Id_tecnico = Id_tecnico;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
