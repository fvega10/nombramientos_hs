/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Datos;

import Base_Datos.ConexionBaseDatos;
import Base_Datos.Parametro;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import logica.Nombramiento;
import logica.Puesto;
import logica.Personal;
import sun.security.krb5.JavaxSecurityAuthKerberosAccess;

/**
 *
 * @author Fabricio
 */
public class NombramientoD {
    private ConexionBaseDatos conexion;
    private boolean error;
    private String errorMsg;

    public boolean isError() {
        return error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    private void limpiarError() {
        this.error = false;
        this.errorMsg = "";
    }

    public NombramientoD(ConexionBaseDatos pConexion) {
        this.conexion = pConexion;
    }
    
    public ArrayList obtenerNombramientos() {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();
       
        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT a.codigo, a.cedula_candidato, a.puesto_nombrado, a,fecha_inicio, a.fecha_finalizacion, a.ubicacion, a.suple_a, a.plaza, "
                    + "e.nombre, e.apellido1, e.apellido2, e.enpropiedad, e.codigo_puesto as codigo_uno, e.veces_nombrado, "
                    + "o.nombre as nombre_uno, o.apellido1 as apellido1__uno, o.apellido2 as apellido2__uno, o.enpropiedad as enpropiedad_uno, o.codigo_puesto, o.veces_nombrado as veces_uno, "
                    + "j.descripcion as descripcion_puesto1, "
                    + "p.descripcion as descripcion_dos, "
                    + "h.descripcion as descripcion_tres "
                    + "FROM appointment a, employee e, employee o, job j, job p, job h "
                    + "WHERE a.cedula_candidato = e.cedula AND a.puesto_nombrado = j.codigo "
                    + "AND e.codigo_puesto = p.codigo AND a.suple_a = o.cedula AND o.codigo_puesto = h.codigo");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Puesto oP = new Puesto(rs.getString("puesto_nombrado"), rs.getString("descripcion_puesto1"));
                    Puesto j1 = new Puesto(rs.getString("codigo_uno"), rs.getString("descripcion_dos"));
                    Puesto j2 = new Puesto(rs.getString("codigo_puesto"), rs.getString("descripcion_tres"));
                    
                    Personal oCandidato = new Personal(rs.getString("cedula_candidato"), 
                            rs.getString("nombre"), 
                            rs.getString("apellido1"), 
                            rs.getString("apellido2"), 
                            null, 
                            null, 
                            "", 
                            rs.getBoolean("enpropiedad"),
                            j1,
                            rs.getInt("veces_nombrado"));
                    
                    Personal oSuple = new Personal(rs.getString("suple_a"), 
                            rs.getString("nombre_uno"), 
                            rs.getString("apellido1__uno"), 
                            rs.getString("apellido2__uno"), 
                            null, 
                            null, 
                            "", 
                            rs.getBoolean("enpropiedad_uno"),
                            j2,
                            rs.getInt("veces_nombrado"));
                    
                    Nombramiento oD = new Nombramiento(rs.getString("codigo"),
                            oCandidato, 
                            oP, 
                            rs.getDate("fecha_inicio"), 
                            rs.getDate("fecha_finalizacion"), 
                            rs.getString("ubicacion"), 
                            oSuple, 
                            rs.getString("plaza"));

                    articulos.add(oD);
                }
                
                rs.close();
                
            } else {
                this.error = true;
                this.errorMsg = this.conexion.getErrorMsg();
            }
        } catch (Exception e) {
            this.error = true;
            this.errorMsg = e.getMessage();
        }

        return articulos;
    }
    
    public boolean exitEmployeeAppointmentYet(String cedulaCandidato){
        ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT fecha_inicio, fecha_finalizacion FROM appointment WHERE cedula_candidato = '" + cedulaCandidato + "'");
        try {
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Date a = new Date();
                    Date b = rs.getDate("fecha_finalizacion");
                    
                    if(a.before(b) || a.compareTo(b) == 0){
                        return true;
                    }else{
                        return false;
                    }
                }
            }else{
                this.error = true;
                this.errorMsg = this.conexion.getErrorMsg();
                return false;
            }
        } catch (Exception e) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
            return false;
        }
        return false;
    }
    
    public void agregarNombramiento(Nombramiento oNombramiento){
        this.limpiarError();
        if(!this.exitEmployeeAppointmentYet(oNombramiento.getCandidato().getCedula())){
            String sql = "INSERT INTO appointment( " +
            "codigo, cedula_candidato, puesto_nombrado, fecha_inicio, fecha_finalizacion, ubicacion, suple_a, plaza) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

            Parametro[] oP = new Parametro[8];
            oP[0] = new Parametro(Parametro.STRING, oNombramiento.getBoleta_nombramiento());
            oP[1] = new Parametro(Parametro.STRING, oNombramiento.getCandidato().getCedula());
            oP[2] = new Parametro(Parametro.STRING, oNombramiento.getPuesto().getCodigo());
            oP[3] = new Parametro(Parametro.DATETIME, new java.sql.Date(oNombramiento.getFecha_nombramiento().getTime()));
            oP[4] = new Parametro(Parametro.DATETIME, new java.sql.Date(oNombramiento.getFecha_finalizacion_nombramiento().getTime()));
            oP[5] = new Parametro(Parametro.STRING, oNombramiento.getUbicacion());
            oP[6] = new Parametro(Parametro.STRING, oNombramiento.getCandidato_suplido().getCedula());
            oP[7] = new Parametro(Parametro.STRING, oNombramiento.getNumero_plaza());
            this.conexion.ejecutarSQL(sql, oP);

            if (this.conexion.isError()) {
                this.error = true;
                this.errorMsg = this.conexion.getErrorMsg();
            }
        }else{
            this.error = true;
                this.errorMsg = "El usuario con cédula " + oNombramiento.getCandidato() + " se encuentra en un nombramiento actualmente.";
        }
        
    }
    
    public void actualizarNombramiento(Nombramiento oNombramiento, String[] pNombramientoEditar){
        this.limpiarError();
        String sql = "UPDATE appointment " +
        "SET codigo=?, cedula_candidato=?, puesto_nombrado=?, fecha_inicio=?, fecha_finalizacion=?, ubicacion=?, suple_a=?, plaza=? " +
        "WHERE codigo=? AND cedula_candidato=?;";

        Parametro[] oP = new Parametro[10];
        oP[0] = new Parametro(Parametro.STRING, oNombramiento.getBoleta_nombramiento());
        oP[1] = new Parametro(Parametro.STRING, oNombramiento.getCandidato().getCedula());
        oP[2] = new Parametro(Parametro.STRING, oNombramiento.getPuesto().getCodigo());
        oP[3] = new Parametro(Parametro.DATETIME, new java.sql.Date(oNombramiento.getFecha_nombramiento().getTime()));
        oP[4] = new Parametro(Parametro.DATETIME, new java.sql.Date(oNombramiento.getFecha_finalizacion_nombramiento().getTime()));
        oP[5] = new Parametro(Parametro.STRING, oNombramiento.getUbicacion());
        oP[6] = new Parametro(Parametro.STRING, oNombramiento.getCandidato_suplido().getCedula());
        oP[7] = new Parametro(Parametro.STRING, oNombramiento.getNumero_plaza());
        oP[8] = new Parametro(Parametro.STRING, pNombramientoEditar[0]);
        oP[9] = new Parametro(Parametro.STRING, pNombramientoEditar[1]);
        
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void borrarNombramiento(String[] pNombramientoEditar){
        this.limpiarError();
        String sql = "DELETE FROM appointment\n" +
" WHERE codigo=? AND cedula_candidato=?;";
        Parametro[] oP = new Parametro[2];
        oP[0] = new Parametro(Parametro.STRING, pNombramientoEditar[0]);
        oP[1] = new Parametro(Parametro.STRING, pNombramientoEditar[1]);
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
}
