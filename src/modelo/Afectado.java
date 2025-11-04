/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Afectado {
    private int Id_afectado;
    private int Id_lote;
    private int Id_plaga;

    public Afectado() {
    }

    public Afectado(int Id_afectado, int Id_lote, int Id_plaga) {
        this.Id_afectado = Id_afectado;
        this.Id_lote = Id_lote;
        this.Id_plaga = Id_plaga;
    }

    public int getId_afectado() {
        return Id_afectado;
    }

    public int getId_lote() {
        return Id_lote;
    }

    public int getId_plaga() {
        return Id_plaga;
    }

    public void setId_afectado(int Id_afectado) {
        this.Id_afectado = Id_afectado;
    }

    public void setId_lote(int Id_lote) {
        this.Id_lote = Id_lote;
    }

    public void setId_plaga(int Id_plaga) {
        this.Id_plaga = Id_plaga;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
