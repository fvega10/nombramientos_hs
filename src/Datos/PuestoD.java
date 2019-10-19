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

/**
 *
 * @author Fabricio
 */
public class PuestoD {
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

    public PuestoD(ConexionBaseDatos pConexion) {
        this.conexion = pConexion;
    }
    
    public ArrayList obtenerPuesto() {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT * FROM job");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Puesto oD = new Puesto(rs.getString("codigo"), rs.getString("descripcion"));

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
    
    public ArrayList obtenerPuestoPorNombre(String nombre) {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT * FROM job WHERE descripcion like '" + nombre + "%'");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Puesto oD = new Puesto(rs.getString("codigo"), rs.getString("descripcion"));

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
    
    public void agregarPersonal(Puesto oPuesto){
        this.limpiarError();
        String sql = "INSERT INTO job(\n" +
"            codigo, descripcion)\n" +
"    VALUES (?, ?);";
        
        Parametro[] oP = new Parametro[2];
        oP[0] = new Parametro(Parametro.STRING, oPuesto.getCodigo());
        oP[1] = new Parametro(Parametro.STRING, oPuesto.getNombre_puesto());
        this.conexion.ejecutarSQL(sql, oP);

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void actualizarPersonal(Puesto oPuesto, String pPuestoEditar){
        this.limpiarError();
        String sql = "UPDATE job\n" +
"   SET codigo=?, descripcion=?\n" +
" WHERE codigo=?;";
        
        Parametro[] oP = new Parametro[3];
        oP[0] = new Parametro(Parametro.STRING, oPuesto.getCodigo());
        oP[1] = new Parametro(Parametro.STRING, oPuesto.getNombre_puesto());
        oP[2] = new Parametro(Parametro.STRING, pPuestoEditar);
        
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void borrarDatos(String pPuestoBorrar){
        this.limpiarError();
        String sql = "DELETE FROM job\n" +
" WHERE codigo=?;";
        Parametro[] oP = new Parametro[1];
        oP[0] = new Parametro(Parametro.STRING, pPuestoBorrar);
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
}
