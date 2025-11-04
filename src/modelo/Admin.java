/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author angel
 */
public class Admin {
    private int Id_admin;
    private String Correo;
    private String Password;

    public Admin() {
    }

    public Admin(int Id_admin, String Correo, String Password) {
        this.Id_admin = Id_admin;
        this.Correo = Correo;
        this.Password = Password;
    }

    public int getId_admin() {
        return Id_admin;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getPassword() {
        return Password;
    }

    public void setId_admin(int Id_admin) {
        this.Id_admin = Id_admin;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
    
    public String[] split(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
