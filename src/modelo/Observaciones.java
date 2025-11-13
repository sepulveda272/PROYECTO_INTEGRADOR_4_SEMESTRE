/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Observaciones {
    private int Id_observacion;
    private String Fecha_observacion;
    private String Observaciones;
    private int Id_inspeccion;
    private int Id_funcionario;
    private String Nombre_Funcionario;

    public Observaciones() {
    }

    public Observaciones(int Id_observacion, String Fecha_observacion, String Observaciones,
                         int Id_inspeccion, int Id_funcionario, String Nombre_Funcionario) {
        this.Id_observacion = Id_observacion;
        this.Fecha_observacion = Fecha_observacion;
        this.Observaciones = Observaciones;
        this.Id_inspeccion = Id_inspeccion;
        this.Id_funcionario = Id_funcionario;
        this.Nombre_Funcionario = Nombre_Funcionario;
    }

    public int getId_observacion() {
        return Id_observacion;
    }

    public String getFecha_observacion() {
        return Fecha_observacion;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public int getId_inspeccion() {
        return Id_inspeccion;
    }

    public int getId_funcionario() {
        return Id_funcionario;
    }

    public String getNombre_Funcionario() {
        return Nombre_Funcionario;
    }

    public void setId_observacion(int Id_observacion) {
        this.Id_observacion = Id_observacion;
    }

    public void setFecha_observacion(String Fecha_observacion) {
        this.Fecha_observacion = Fecha_observacion;
    }

    public void setObservaciones(String Observaciones) {
        this.Observaciones = Observaciones;
    }

    public void setId_inspeccion(int Id_inspeccion) {
        this.Id_inspeccion = Id_inspeccion;
    }

    public void setId_funcionario(int Id_funcionario) {
        this.Id_funcionario = Id_funcionario;
    }

    public void setNombre_Funcionario(String Nombre_Funcionario) {
        this.Nombre_Funcionario = Nombre_Funcionario;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
