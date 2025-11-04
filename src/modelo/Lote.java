/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Lote {
    private int Numero_lote;
    private double Area_total;
    private String Fecha_siembra;
    // Opcional: si el lote sigue activo puede ir null o vacío
    private String Fecha_eliminacion;
    private double Area_siembra;
    private String Estado_fenologico;
    private int Id_cultivo;
    private int Id_lugar;

    public Lote() {
    }

    // Constructor completo (incluye fecha_eliminacion)
    public Lote(int Numero_lote, double Area_total, String Fecha_siembra,
                String Fecha_eliminacion, double Area_siembra, String Estado_fenologico, int Id_cultivo, int Id_lugar) {
        this.Numero_lote = Numero_lote;
        this.Area_total = Area_total;
        this.Fecha_siembra = Fecha_siembra;
        this.Fecha_eliminacion = Fecha_eliminacion; // puede ser null
        this.Area_siembra = Area_siembra;
        this.Estado_fenologico = Estado_fenologico;
        this.Id_cultivo = Id_cultivo;
        this.Id_lugar = Id_lugar;
    }

    // Constructor alterno SIN fecha_eliminacion (por ser opcional)
    public Lote(int Numero_lote, double Area_total, String Fecha_siembra, double Area_siembra, String Estado_fenologico,
                int Id_cultivo, int Id_lugar) {
        this(Numero_lote, Area_total, Fecha_siembra, null, Area_siembra, Estado_fenologico, Id_cultivo, Id_lugar);
    }

    public int getNumero_lote() {
        return Numero_lote;
    }

    public double getArea_total() {
        return Area_total;
    }

    public String getFecha_siembra() {
        return Fecha_siembra;
    }

    public String getFecha_eliminacion() {
        return Fecha_eliminacion;
    }
    
    public double getArea_siembra() {
        return Area_siembra;
    }
    
    public String getEstado_fenologico() {
        return Estado_fenologico;
    }

    public int getId_cultivo() {
        return Id_cultivo;
    }

    public int getId_lugar() {
        return Id_lugar;
    }

    public void setNumero_lote(int Numero_lote) {
        this.Numero_lote = Numero_lote;
    }

    public void setArea_total(double Area_total) {
        this.Area_total = Area_total;
    }

    public void setFecha_siembra(String Fecha_siembra) {
        this.Fecha_siembra = Fecha_siembra;
    }

    public void setFecha_eliminacion(String Fecha_eliminacion) {
        this.Fecha_eliminacion = Fecha_eliminacion;
    }
    
    public void setArea_siembra(double Area_siembra) {
        this.Area_siembra = Area_siembra;
    }
    
    public void setEstado_fenologico(String Estado_fenologico) {
        this.Estado_fenologico = Estado_fenologico;
    }

    public void setId_cultivo(int Id_cultivo) {
        this.Id_cultivo = Id_cultivo;
    }

    public void setId_lugar(int Id_lugar) {
        this.Id_lugar = Id_lugar;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
