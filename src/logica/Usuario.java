/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

/**
 *
 * @author Fabricio
 */
public class Usuario {
    private Personal oPersona;
    private String nombre_usuario;
    private String constrasenna;

    public Usuario(Personal oPersona, String nombre_usuario, String constrasenna) {
        this.oPersona = oPersona;
        this.nombre_usuario = nombre_usuario;
        this.constrasenna = constrasenna;
    }

    public Usuario(Personal oPersona, String nombre_usuario) {
        this.oPersona = oPersona;
        this.nombre_usuario = nombre_usuario;
    }

    public Usuario() {
    }
    
    public Personal getoPersona() {
        return oPersona;
    }

    public void setoPersona(Personal oPersona) {
        this.oPersona = oPersona;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getConstrasenna() {
        return constrasenna;
    }

    public void setConstrasenna(String constrasenna) {
        this.constrasenna = constrasenna;
    }
}
