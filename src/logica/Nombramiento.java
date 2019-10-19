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
public class Nombramiento { 
    private String boleta_nombramiento; // primary key
    private Personal candidato; // primary key and foreign key
    private Puesto puesto; // primary key and foreign key
    private Date fecha_nombramiento;
    private Date fecha_finalizacion_nombramiento;
    private String ubicacion;
    private Personal candidato_suplido; // foreign key
    private String numero_plaza;

    public Nombramiento(String boleta_nombramiento, Personal candidato, Puesto puesto, Date fecha_nombramiento, Date fecha_finalizacion_nombramiento, String ubicacion, Personal candidato_suplido, String numero_plaza) {
        this.boleta_nombramiento = boleta_nombramiento;
        this.candidato = candidato;
        this.puesto = puesto;
        this.fecha_nombramiento = fecha_nombramiento;
        this.fecha_finalizacion_nombramiento = fecha_finalizacion_nombramiento;
        this.ubicacion = ubicacion;
        this.candidato_suplido = candidato_suplido;
        this.numero_plaza = numero_plaza;
    }

    public String getNumero_plaza() {
        return numero_plaza;
    }

    public void setNumero_plaza(String numero_plaza) {
        this.numero_plaza = numero_plaza;
    }

    public Personal getCandidato() {
        return candidato;
    }

    public void setCandidato(Personal candidato) {
        this.candidato = candidato;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Date getFecha_nombramiento() {
        return fecha_nombramiento;
    }

    public void setFecha_nombramiento(Date fecha_nombramiento) {
        this.fecha_nombramiento = fecha_nombramiento;
    }

    public Date getFecha_finalizacion_nombramiento() {
        return fecha_finalizacion_nombramiento;
    }

    public void setFecha_finalizacion_nombramiento(Date fecha_finalizacion_nombramiento) {
        this.fecha_finalizacion_nombramiento = fecha_finalizacion_nombramiento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Personal getCandidato_suplido() {
        return candidato_suplido;
    }

    public void setCandidato_suplido(Personal candidato_suplido) {
        this.candidato_suplido = candidato_suplido;
    }

    public String getBoleta_nombramiento() {
        return boleta_nombramiento;
    }

    public void setBoleta_nombramiento(String boleta_nombramiento) {
        this.boleta_nombramiento = boleta_nombramiento;
    }
}
