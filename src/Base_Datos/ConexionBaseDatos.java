/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Base_Datos;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Fabricio
 */
public class ConexionBaseDatos {
    private boolean error;
    private String errorMsg;
    private Connection conexion;
    private String usuario;
    private String password;
    private String baseDatos;
    private String servidor;
    private String puerto;
    private String driver;
    private String esquema;
    private String cnxStr;
    
    public ConexionBaseDatos(String pUsuario, String pPassword,
            String pBaseDatos, String pServidor,
            String pPuerto, String pDriver,
            String pEsquema) {
        this.usuario   = pUsuario;
        this.password  = pPassword;
        this.baseDatos = pBaseDatos;
        this.servidor  = pServidor;
        this.puerto    = pPuerto;
        this.driver    = pDriver;
        this.esquema   = pEsquema;
        this.cnxStr    = "";
    }
   
    private void limpiarError() {
        this.error = false;
        this.errorMsg = "";
    }

    public void conectar() {
        this.limpiarError();

        cnxStr = "jdbc:postgresql://"
                + this.servidor + ":"
                + this.puerto + "/"
                + this.baseDatos;
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException cnfe) {
            this.error = true;
            this.errorMsg = cnfe.getMessage();
            return;
        }

        try {
            this.conexion = DriverManager.getConnection(cnxStr, this.usuario, this.password);
        } catch (SQLException sqle) {
            this.error = true;
            this.errorMsg = sqle.getMessage();
            return;
        }
    }
    
    public void IniciarTransacciones(){
        try{
            this.conexion.setAutoCommit(false);
        }catch(SQLException e){
            this.error    = true;
            this.errorMsg = e.getMessage();
        }
    }
    
    public void setCommit(){
        try{
            this.conexion.commit();
        }catch(SQLException e){
            this.error = true;
            this.errorMsg = e.getMessage();
        }
    }
    
    public void setRollBack(){
        try{
            this.conexion.rollback();
        }catch(SQLException e){
            this.error = true;
            this.errorMsg = e.getMessage();
        }
    }
    
    public void ejecutarSQL(String pSql) {
        this.limpiarError();
        Statement stmt;
        try {
            stmt = this.conexion.createStatement();
            stmt.executeUpdate(pSql);
        } catch (SQLException e) {
            this.error = true;
            this.errorMsg = e.getMessage();
        }
    }

    public void ejecutarSQL(String pSql, Parametro[] pParametros) {
        this.limpiarError();
        PreparedStatement stmt;
        try {
            stmt = this.conexion.prepareStatement(pSql);
            for (int i = 0; i < pParametros.length; i++) {
                switch (pParametros[i].getTipo()) {
                    case Parametro.INT:
                        stmt.setInt((i + 1), pParametros[i].getValorInt());
                        break;
                    case Parametro.DOUBLE:
                        stmt.setDouble((i + 1), pParametros[i].getValorDouble());
                        break;
                    case Parametro.STRING:
                        stmt.setString((i + 1), pParametros[i].getValorString());
                        break;
                    case Parametro.DATETIME:
                        stmt.setDate((i + 1), pParametros[i].getValorDate());
                        break;
                    case Parametro.BOOLEAN:
                        stmt.setBoolean((i + 1), pParametros[i].getValorBoolean());
                        break;
                }
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            this.error = true;
            this.errorMsg = e.getMessage();
            return;
        }
    }

    public ResultSet ejecutarConsultaSQL(String pSql, Parametro[] pParametros) {
        this.limpiarError();
        ResultSet rs = null;
        PreparedStatement stmt;
        try {
            stmt = this.conexion.prepareStatement(pSql);
            for (int i = 0; i < pParametros.length; i++) {
                switch (pParametros[i].getTipo()) {
                    case Parametro.INT:
                        stmt.setInt((i + 1), pParametros[i].getValorInt());
                        break;
                    case Parametro.DOUBLE:
                        stmt.setDouble((i + 1), pParametros[i].getValorDouble());
                        break;
                    case Parametro.STRING:
                        stmt.setString((i + 1), pParametros[i].getValorString());
                        break;
                    case Parametro.DATETIME:
                        stmt.setDate((i + 1), pParametros[i].getValorDate());
                        break;
                    case Parametro.BOOLEAN:
                        stmt.setBoolean((i + 1), pParametros[i].getValorBoolean());
                        break;
                }
            }
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            this.error = true;
            this.errorMsg = e.getMessage();
        }
        return rs;
    }

    public ResultSet ejecutarConsultaSQL(String pSql) {
        this.limpiarError();
        ResultSet rs = null;
        Statement stmt;
        try {
            stmt = this.conexion.createStatement();
            rs = stmt.executeQuery(pSql);
        } catch (SQLException e) {
            this.error = true;
            this.errorMsg = e.getMessage();
        }
        return rs;
    }

    public void desconectar() {
        this.limpiarError();
        try {
            this.conexion.close();
        } catch (SQLException sqle) {
            this.error = true;
            this.errorMsg = sqle.getMessage();
        }
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @return the baseDatos
     */
    public String getBaseDatos() {
        return baseDatos;
    }

    /**
     * @return the servidor
     */
    public String getServidor() {
        return servidor;
    }

    /**
     * @return the puerto
     */
    public String getPuerto() {
        return puerto;
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @return the esquema
     */
    public String getEsquema() {
        return esquema;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCnxStr() {
        return cnxStr;
    }

    public void setCnxStr(String cnxStr) {
        this.cnxStr = cnxStr;
    }
}
