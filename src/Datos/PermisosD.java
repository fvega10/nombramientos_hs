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
import logica.PermisoUsuario;
import logica.Puesto;
import logica.Usuario;
import logica.Personal;

/**
 *
 * @author Fabricio
 */
public class PermisosD {
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

    public PermisosD(ConexionBaseDatos pConexion) {
        this.conexion = pConexion;
    }
    
    public ArrayList obtenerPermisos() {
        
        this.limpiarError();
        ArrayList articulos = new ArrayList();

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT u.*, "
                                                            + "p.nombramiento_en_curso, p.asignar_nuevo_nombramiento, p.lista_general, p.lista_activos, p.lista_pasivos, p.nuevo_puesto, p.modificar_puesto, p.eliminar_puesto, p.nuevo_colaborador, p.modificar_colaborador, p.eliminar_colaborador, p.nuevo_usuario, p.asignar_permisos, p.modificar_usuarios, p.eliminar_usuarios, "
                                                            + "m.nombre, m.apellido1, m.apellido2 "
                                                            + "FROM sistem_user u, user_permission p, employee m "
                                                            + "WHERE u.cedula = p.cedula AND u.cedula = m.cedula");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Personal oP = new Personal(rs.getString("cedula"), 
                                                rs.getString("nombre"), 
                                                rs.getString("apellido1"), 
                                                rs.getString("apellido2"), 
                                                null, 
                                                null, 
                                                "", 
                                                false, 
                                                null, 0);
                    Usuario oU = new Usuario(oP, rs.getString("usuario"));
                    PermisoUsuario oPU = new PermisoUsuario(oU, 
                            rs.getBoolean("nombramiento_en_curso"), 
                            rs.getBoolean("asignar_nuevo_nombramiento"), 
                            rs.getBoolean("lista_general"), 
                            rs.getBoolean("lista_activos"), 
                            rs.getBoolean("lista_pasivos"), 
                            rs.getBoolean("nuevo_puesto"), 
                            rs.getBoolean("modificar_puesto"), 
                            rs.getBoolean("eliminar_puesto"), 
                            rs.getBoolean("nuevo_colaborador"), 
                            rs.getBoolean("modificar_colaborador"), 
                            rs.getBoolean("eliminar_colaborador"), 
                            rs.getBoolean("nuevo_usuario"), 
                            rs.getBoolean("asignar_permisos"), 
                            rs.getBoolean("modificar_usuarios"), 
                            rs.getBoolean("eliminar_usuarios"));
                    articulos.add(oPU);
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
    
    public PermisoUsuario obtenerPermisosPorCedula(String cedula) {
        
        this.limpiarError();
        PermisoUsuario articulos = null;

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT u.*, "
                                                            + "p.nombramiento_en_curso, p.asignar_nuevo_nombramiento, p.lista_general, p.lista_activos, p.lista_pasivos, p.nuevo_puesto, p.modificar_puesto, p.eliminar_puesto, p.nuevo_colaborador, p.modificar_colaborador, p.eliminar_colaborador, p.nuevo_usuario, p.asignar_permisos, p.modificar_usuarios, p.eliminar_usuarios, "
                                                            + "m.nombre, m.apellido1, m.apellido2 "
                                                            + "FROM sistem_user u, user_permission p, employee m "
                                                            + "WHERE u.cedula = p.cedula AND u.cedula = m.cedula AND u.cedula = '" + cedula + "'");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Personal oP = new Personal(rs.getString("cedula"), 
                                                rs.getString("nombre"), 
                                                rs.getString("apellido1"), 
                                                rs.getString("apellido2"), 
                                                null, 
                                                null, 
                                                "", 
                                                false, 
                                                null, 0);
                    Usuario oU = new Usuario(oP, rs.getString("usuario"));
                    PermisoUsuario oPU = new PermisoUsuario(oU, 
                            rs.getBoolean("nombramiento_en_curso"), 
                            rs.getBoolean("asignar_nuevo_nombramiento"), 
                            rs.getBoolean("lista_general"), 
                            rs.getBoolean("lista_activos"), 
                            rs.getBoolean("lista_pasivos"), 
                            rs.getBoolean("nuevo_puesto"), 
                            rs.getBoolean("modificar_puesto"), 
                            rs.getBoolean("eliminar_puesto"), 
                            rs.getBoolean("nuevo_colaborador"), 
                            rs.getBoolean("modificar_colaborador"), 
                            rs.getBoolean("eliminar_colaborador"), 
                            rs.getBoolean("nuevo_usuario"), 
                            rs.getBoolean("asignar_permisos"), 
                            rs.getBoolean("modificar_usuarios"), 
                            rs.getBoolean("eliminar_usuarios"));
                    
                    articulos = oPU;
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
    
    public ArrayList obtenerUsuarios(){
        this.limpiarError();
        ArrayList articulos = new ArrayList();

        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT u.cedula, u.usuario, u.pass, "
                                                            + "p.nombre, p.apellido1, p.apellido2 "
                                                            + "FROM sistem_user u, employee p "
                                                            + "WHERE u.cedula = p.cedula");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    Personal oP = new Personal(rs.getString("cedula"), rs.getString("nombre"), rs.getString("apellido1"), rs.getString("apellido2"), null, null, "", false, null, 0);
                    Usuario oU = new Usuario(oP, rs.getString("usuario"), rs.getString("pass"));
                    articulos.add(oU);
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
    
    public boolean existeRegistro(String cedula){
        this.limpiarError();
        boolean existe = false;
        try {
            ResultSet rs = this.conexion.ejecutarConsultaSQL("SELECT cedula FROM sistem_user WHERE cedula = '" + cedula +"'");
            if (!this.conexion.isError()) {
                while (rs.next()) {
                    existe = true;
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
        return existe;
    }
    
    public void agregarUsuario(Usuario oUsuario){
        this.limpiarError();
        String sql = "INSERT INTO sistem_user(\n" +
"            cedula, usuario, pass)\n" +
"    VALUES (?, ?, md5(?));";
        
        Parametro[] oP = new Parametro[3];
        oP[0] = new Parametro(Parametro.STRING, oUsuario.getoPersona().getCedula());
        oP[1] = new Parametro(Parametro.STRING, oUsuario.getNombre_usuario());
        oP[2] = new Parametro(Parametro.STRING, oUsuario.getConstrasenna());
        
        if(existeRegistro(oUsuario.getoPersona().getCedula())){
            this.error = true;
            this.errorMsg = "Este usuario ya se encuentra registrado";
        }else{
            this.conexion.ejecutarSQL(sql, oP);
        }
        

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void actualizarUsuario(Usuario pUsuario, String pCedulaActualizar){
        this.limpiarError();
        String sql = "UPDATE sistem_user " +
"SET pass=md5(?), usuario=? " +
"WHERE cedula=?;";
        
        Parametro[] oP = new Parametro[3];
        oP[0] = new Parametro(Parametro.STRING, pUsuario.getConstrasenna());
        oP[1] = new Parametro(Parametro.STRING, pUsuario.getNombre_usuario());
        oP[2] = new Parametro(Parametro.STRING, pCedulaActualizar);
        
        this.conexion.ejecutarSQL(sql, oP);

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void borrarUsuario(String pCedulaEliminar){
        this.limpiarError();
        String sql = "DELETE FROM sistem_user\n" +
" WHERE cedula=?;";
        Parametro[] oP = new Parametro[1];
        oP[0] = new Parametro(Parametro.STRING, pCedulaEliminar);
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }else{
            this.borrarPermiso(pCedulaEliminar);
        }
    }
    
    public void agregarPermisosUsuarioNuevo(String pCedula){
        this.limpiarError();
        String sql = "INSERT INTO user_permission(\n" +
"            cedula)\n" +
"    VALUES (?);";
        Parametro[] oP = new Parametro[1];
        oP[0] = new Parametro(Parametro.STRING, pCedula);
        this.conexion.ejecutarSQL(sql, oP);

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void agregarPermisos(PermisoUsuario oPermiso){
        this.limpiarError();
        String sql = "INSERT INTO user_permission(\n" +
"            cedula, nombramiento_en_curso, asignar_nuevo_nombramiento, lista_general, lista_activos, lista_pasivos, nuevo_puesto, modificar_puesto, eliminar_puesto, nuevo_colaborador, modificar_colaborador, eliminar_colaborador, nuevo_usuario, asignar_permisos, modificar_usuarios, eliminar_usuarios)\n" +
"    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        
        Parametro[] oP = new Parametro[16];
        oP[0] = new Parametro(Parametro.STRING, oPermiso.getoUsuario().getoPersona().getCedula());
        oP[1] = new Parametro(Parametro.BOOLEAN, oPermiso.isNombramiento_en_curso());
        oP[2] = new Parametro(Parametro.BOOLEAN, oPermiso.isAsignar_nuevo_nombramiento());
        oP[3] = new Parametro(Parametro.BOOLEAN, oPermiso.isLista_general());
        oP[4] = new Parametro(Parametro.BOOLEAN, oPermiso.isLista_activos());
        oP[5] = new Parametro(Parametro.BOOLEAN, oPermiso.isLista_pasivos());
        oP[6] = new Parametro(Parametro.BOOLEAN, oPermiso.isNuevo_puesto());
        oP[7] = new Parametro(Parametro.BOOLEAN, oPermiso.isModificar_puesto());
        oP[8] = new Parametro(Parametro.BOOLEAN, oPermiso.isEliminar_puesto());
        oP[9] = new Parametro(Parametro.BOOLEAN, oPermiso.isNuevo_colaborador());
        oP[10] = new Parametro(Parametro.BOOLEAN, oPermiso.isModificar_colaborador());
        oP[11] = new Parametro(Parametro.BOOLEAN, oPermiso.isEliminar_colaborador());
        oP[12] = new Parametro(Parametro.BOOLEAN, oPermiso.isNuevo_usuario());
        oP[13] = new Parametro(Parametro.BOOLEAN, oPermiso.isAsignar_permisos());
        oP[14] = new Parametro(Parametro.BOOLEAN, oPermiso.isModificar_usuarios());
        oP[15] = new Parametro(Parametro.BOOLEAN, oPermiso.isEliminar_usuarios());
        this.conexion.ejecutarSQL(sql, oP);

        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void actualizarPermisos(PermisoUsuario oPermiso, String pPCedulaEditar){
        this.limpiarError();
        String sql = "UPDATE user_permission " +
"SET nombramiento_en_curso=?, asignar_nuevo_nombramiento=?, lista_general=?, lista_activos=?, lista_pasivos=?, nuevo_puesto=?, modificar_puesto=?, eliminar_puesto=?, nuevo_colaborador=?, modificar_colaborador=?, eliminar_colaborador=?, nuevo_usuario=?, asignar_permisos=?, modificar_usuarios=?, eliminar_usuarios=? " +
"WHERE cedula=?;";
        
        Parametro[] oP = new Parametro[16];
        oP[0] = new Parametro(Parametro.BOOLEAN, oPermiso.isNombramiento_en_curso());
        oP[1] = new Parametro(Parametro.BOOLEAN, oPermiso.isAsignar_nuevo_nombramiento());
        oP[2] = new Parametro(Parametro.BOOLEAN, oPermiso.isLista_general());
        oP[3] = new Parametro(Parametro.BOOLEAN, oPermiso.isLista_activos());
        oP[4] = new Parametro(Parametro.BOOLEAN, oPermiso.isLista_pasivos());
        oP[5] = new Parametro(Parametro.BOOLEAN, oPermiso.isNuevo_puesto());
        oP[6] = new Parametro(Parametro.BOOLEAN, oPermiso.isModificar_puesto());
        oP[7] = new Parametro(Parametro.BOOLEAN, oPermiso.isEliminar_puesto());
        oP[8] = new Parametro(Parametro.BOOLEAN, oPermiso.isNuevo_colaborador());
        oP[9] = new Parametro(Parametro.BOOLEAN, oPermiso.isModificar_colaborador());
        oP[10] = new Parametro(Parametro.BOOLEAN, oPermiso.isEliminar_colaborador());
        oP[11] = new Parametro(Parametro.BOOLEAN, oPermiso.isNuevo_usuario());
        oP[12] = new Parametro(Parametro.BOOLEAN, oPermiso.isAsignar_permisos());
        oP[13] = new Parametro(Parametro.BOOLEAN, oPermiso.isModificar_usuarios());
        oP[14] = new Parametro(Parametro.BOOLEAN, oPermiso.isEliminar_usuarios());
        oP[15] = new Parametro(Parametro.STRING, pPCedulaEditar);
        
        this.conexion.ejecutarSQL(sql, oP);
        if (this.conexion.isError()) {
            this.error = true;
            this.errorMsg = this.conexion.getErrorMsg();
        }
    }
    
    public void borrarPermiso(String pPersonalBorrar){
        this.limpiarError();
        String sql = "DELETE FROM user_permission\n" +
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
