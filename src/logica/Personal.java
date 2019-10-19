/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import java.util.Date;

/**
 *
 * @author Fabricio
 */
public class Personal {
    private String cedula;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private Date fecha_nacimiento;
    private Date fecha_ingreso_institucion;
    private String codigo_presupuesto;
    private boolean conPropiedad;
    private Puesto cod_puesto;
    private int veces_nombrado;
    
    public Personal(){
        this.cedula = "";
        this.nombre = "";
        this.apellido1 = "";
        this.apellido2 = "";
        this.fecha_nacimiento = null;
        this.fecha_ingreso_institucion = null;
        this.codigo_presupuesto = "";
        this.conPropiedad = false;
        this.cod_puesto = null;
        this.veces_nombrado = 0;
    }
    
    public Personal(String cedula, 
            String nombre, 
            String apellido1, 
            String apellido2, 
            Date fecha_nacimiento, 
            Date fecha_ingreso_institucion, 
            String codigo_presupuesto, 
            boolean conPropiedad,
            Puesto puesto,
            int veces_nombrado) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.fecha_nacimiento = fecha_nacimiento;
        this.fecha_ingreso_institucion = fecha_ingreso_institucion;
        this.codigo_presupuesto = codigo_presupuesto;
        this.conPropiedad = conPropiedad;
        this.cod_puesto = puesto;
        this.veces_nombrado = veces_nombrado;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Date getFecha_ingreso_institucion() {
        return fecha_ingreso_institucion;
    }

    public void setFecha_ingreso_institucion(Date fecha_ingreso_institucion) {
        this.fecha_ingreso_institucion = fecha_ingreso_institucion;
    }

    public String getCodigo_presupuesto() {
        return codigo_presupuesto;
    }

    public void setCodigo_presupuesto(String codigo_presupuesto) {
        this.codigo_presupuesto = codigo_presupuesto;
    }

    public boolean isConPropiedad() {
        return conPropiedad;
    }

    public void setConPropiedad(boolean conPropiedad) {
        this.conPropiedad = conPropiedad;
    }
    
    @Override
    public String toString(){
        return this.cedula + " - " + this.nombre + " " + this.apellido1 + " " + this.apellido2;
    }

    public Puesto getCod_puesto() {
        return cod_puesto;
    }

    public void setCod_puesto(Puesto cod_puesto) {
        this.cod_puesto = cod_puesto;
    }

    public int getVeces_nombrado() {
        return veces_nombrado;
    }

    public void setVeces_nombrado(int veces_nombrado) {
        this.veces_nombrado = veces_nombrado;
    }

}
