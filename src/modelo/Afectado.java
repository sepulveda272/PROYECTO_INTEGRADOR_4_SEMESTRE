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
    private int Id_cultivo;
    private int Id_plaga;

    public Afectado() {
    }

    public Afectado(int Id_afectado, int Id_cultivo, int Id_plaga) {
        this.Id_afectado = Id_afectado;
        this.Id_cultivo = Id_cultivo;
        this.Id_plaga = Id_plaga;
    }

    public int getId_afectado() {
        return Id_afectado;
    }

    public int getId_cultivo() {
        return Id_cultivo;
    }

    public int getId_plaga() {
        return Id_plaga;
    }

    public void setId_afectado(int Id_afectado) {
        this.Id_afectado = Id_afectado;
    }

    public void setId_cultivo(int Id_cultivo) {
        this.Id_cultivo = Id_cultivo;
    }

    public void setId_plaga(int Id_plaga) {
        this.Id_plaga = Id_plaga;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
