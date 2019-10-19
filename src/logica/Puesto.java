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
public class Puesto {
    private String codigo;
    private String nombre_puesto;

    public Puesto(String codigo, String nombre_puesto) {
        this.codigo = codigo;
        this.nombre_puesto = nombre_puesto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre_puesto() {
        return nombre_puesto;
    }

    public void setNombre_puesto(String nombre_puesto) {
        this.nombre_puesto = nombre_puesto;
    }
    
    @Override
    public String toString(){
        return this.nombre_puesto;
    }
}
