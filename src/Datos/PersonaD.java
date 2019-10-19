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
import logica.Puesto;
import logica.Personal;

/**
 *
 * @author Fabricio
 */
public class PersonaD {
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

    public PersonaD(ConexionBaseDatos pConexion) {
        this.conexion = pConexion;
    }
    
    public ArrayList obtenerPersonal() {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT e.*, "
                                                            + "j.descripcion "
                                                            + "FROM employee e, job j "
                                                            + "WHERE e.codigo_puesto = j.codigo");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Puesto oP = new Puesto(rs.getString("codigo_puesto"), rs.getString("descripcion"));
                    Personal oD = new Personal(rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getDate("fecha_ingreso_institucion"),
                        rs.getString("cod_presupuesto"),
                        rs.getBoolean("enPropiedad"),
                        oP,
                        rs.getInt("veces_nombrado"));
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
    
    public ArrayList obtenerPersonalSinNombramiento() {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT e.*, "
                                                            + "j.descripcion "
                                                            + "FROM employee e, job j "
                                                            + "WHERE e.codigo_puesto = j.codigo AND veces_nombrado = 0");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Puesto oP = new Puesto(rs.getString("codigo_puesto"), rs.getString("descripcion"));
                    Personal oD = new Personal(rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getDate("fecha_ingreso_institucion"),
                        rs.getString("cod_presupuesto"),
                        rs.getBoolean("enPropiedad"),
                        oP,
                        rs.getInt("veces_nombrado"));
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
    
    public ArrayList obtenerPersonalPorNombre(String nombre) {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();
        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT e.*, "
                                                            + "j.descripcion "
                                                            + "FROM employee e, job j "
                                                            + "WHERE e.codigo_puesto = j.codigo AND nombre = '" + nombre + "'");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Puesto oP = new Puesto(rs.getString("codigo_puesto"), rs.getString("descripcion"));
                    Personal oD = new Personal(rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getDate("fecha_ingreso_institucion"),
                        rs.getString("cod_presupuesto"),
                        rs.getBoolean("enPropiedad"),
                        oP,
                        rs.getInt("veces_nombrado"));

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
    
    public int obtenerTotalPersona(String cedula) {
        
        this.limpiarError();
        int articulos = 0;
        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT veces_nombrado FROM employee WHERE cedula = '" + cedula + "'");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    articulos = rs.getInt("veces_nombrado");
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
    
    public void agregarPersona(Personal oPersona){
        this.limpiarError();
        String sql = "INSERT INTO employee( " +
        "cedula, nombre, apellido1, apellido2, fecha_nacimiento, fecha_ingreso_institucion, cod_presupuesto, enpropiedad, codigo_puesto, veces_nombrado) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Parametro[] oP = new Parametro[10];
        oP[0] = new Parametro(Parametro.STRING, oPersona.getCedula());
        oP[1] = new Parametro(Parametro.STRING, oPersona.getNombre());
        oP[2] = new Parametro(Parametro.STRING, oPersona.getApellido1());
        oP[3] = new Parametro(Parametro.STRING, oPersona.getApellido2());
        oP[4] = new Parametro(Parametro.DATETIME, new java.sql.Date(oPersona.getFecha_nacimiento().getTime()));
        oP[5] = new Parametro(Parametro.DATETIME, new java.sql.Date(oPersona.getFecha_ingreso_institucion().getTime()));
        oP[6] = new Parametro(Parametro.STRING, oPersona.getCodigo_presupuesto());
        oP[7] = new Parametro(Parametro.BOOLEAN, oPersona.isConPropiedad());
        oP[8] = new Parametro(Parametro.STRING, oPersona.getCod_puesto().getCodigo());
        oP[9] = new Parametro(Parametro.INT, oPersona.getVeces_nombrado());
        this.conexion.ejecutarSQL(sql, oP);

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void agregarDíasNombramiento(String cedula, int dias){
        this.limpiarError();
        String sql = "UPDATE employee " +
        "SET veces_nombrado=? " +
        "WHERE cedula=?;";

        Parametro[] oP = new Parametro[2];
        oP[0] = new Parametro(Parametro.INT, (this.obtenerTotalPersona(cedula) + dias));
        oP[1] = new Parametro(Parametro.STRING, cedula);
        this.conexion.ejecutarSQL(sql, oP);

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void actualizarPersona(Personal oPersona, String cedulaModificar){
        this.limpiarError();
        String sql = "UPDATE employee " +
"SET cedula=?, nombre=?, apellido1=?, apellido2=?, fecha_nacimiento=?, fecha_ingreso_institucion=?, cod_presupuesto=?, enpropiedad=?, codigo_puesto=? " +
"WHERE cedula=?;";

        Parametro[] oP = new Parametro[10];
        oP[0] = new Parametro(Parametro.STRING, oPersona.getCedula());
        oP[1] = new Parametro(Parametro.STRING, oPersona.getNombre());
        oP[2] = new Parametro(Parametro.STRING, oPersona.getApellido1());
        oP[3] = new Parametro(Parametro.STRING, oPersona.getApellido2());
        oP[4] = new Parametro(Parametro.DATETIME, new java.sql.Date(oPersona.getFecha_nacimiento().getTime()));
        oP[5] = new Parametro(Parametro.DATETIME, new java.sql.Date(oPersona.getFecha_ingreso_institucion().getTime()));
        oP[6] = new Parametro(Parametro.STRING, oPersona.getCodigo_presupuesto());
        oP[7] = new Parametro(Parametro.BOOLEAN, oPersona.isConPropiedad());
        oP[8] = new Parametro(Parametro.STRING, oPersona.getCod_puesto().getCodigo());
        oP[9] = new Parametro(Parametro.STRING, cedulaModificar);
        
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    
    public void actualizarPersonaEnNombramiento(Personal oPersona, String cedulaModificar, int díasEliminar){
        this.limpiarError();
        int días = this.obtenerTotalPersona(cedulaModificar);
        String sql = "UPDATE employee " +
        "SET cedula=?, veces_nombrado=?" +
        "WHERE cedula=?;";
        
        Parametro[] oP = new Parametro[3];
        oP[0] = new Parametro(Parametro.STRING, oPersona.getCedula());
        oP[1] = new Parametro(Parametro.INT, (días + díasEliminar));
        oP[2] = new Parametro(Parametro.STRING, cedulaModificar);
        
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void borrarDatos(String pPersonalBorrar){
        this.limpiarError();
        String sql = "DELETE FROM employee\n" +
" WHERE cedula=?;";
        Parametro[] oP = new Parametro[1];
        oP[0] = new Parametro(Parametro.STRING, pPersonalBorrar);
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
}
