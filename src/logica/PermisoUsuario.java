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
public class PermisoUsuario {
    private Usuario oUsuario;
    private boolean nombramiento_en_curso;
    private boolean asignar_nuevo_nombramiento;
    private boolean lista_general;
    private boolean lista_activos;
    private boolean lista_pasivos;
    private boolean nuevo_puesto;
    private boolean modificar_puesto;
    private boolean eliminar_puesto;
    private boolean nuevo_colaborador;
    private boolean modificar_colaborador;
    private boolean eliminar_colaborador;
    private boolean nuevo_usuario;
    private boolean asignar_permisos;
    private boolean modificar_usuarios;
    private boolean eliminar_usuarios;

    public PermisoUsuario(Usuario oUsuario, 
            boolean nombramiento_en_curso, 
            boolean asignar_nuevo_nombramiento, 
            boolean lista_general, 
            boolean lista_activos, 
            boolean lista_pasivos, 
            boolean nuevo_puesto, 
            boolean modificar_puesto, 
            boolean eliminar_puesto, 
            boolean nuevo_colaborador, 
            boolean modificar_colaborador, 
            boolean eliminar_colaborador, 
            boolean nuevo_usuario, 
            boolean asignar_permisos, 
            boolean modificar_usuarios, 
            boolean eliminar_usuarios) {
        
        this.oUsuario = oUsuario;
        this.nombramiento_en_curso = nombramiento_en_curso;
        this.asignar_nuevo_nombramiento = asignar_nuevo_nombramiento;
        this.lista_general = lista_general;
        this.lista_activos = lista_activos;
        this.lista_pasivos = lista_pasivos;
        this.nuevo_puesto = nuevo_puesto;
        this.modificar_puesto = modificar_puesto;
        this.eliminar_puesto = eliminar_puesto;
        this.nuevo_colaborador = nuevo_colaborador;
        this.modificar_colaborador = modificar_colaborador;
        this.eliminar_colaborador = eliminar_colaborador;
        this.nuevo_usuario = nuevo_usuario;
        this.asignar_permisos = asignar_permisos;
        this.modificar_usuarios = modificar_usuarios;
        this.eliminar_usuarios = eliminar_usuarios;
    }

    public Usuario getoUsuario() {
        return oUsuario;
    }

    public void setoUsuario(Usuario oUsuario) {
        this.oUsuario = oUsuario;
    }

    public boolean isNombramiento_en_curso() {
        return nombramiento_en_curso;
    }

    public void setNombramiento_en_curso(boolean nombramiento_en_curso) {
        this.nombramiento_en_curso = nombramiento_en_curso;
    }

    public boolean isAsignar_nuevo_nombramiento() {
        return asignar_nuevo_nombramiento;
    }

    public void setAsignar_nuevo_nombramiento(boolean asignar_nuevo_nombramiento) {
        this.asignar_nuevo_nombramiento = asignar_nuevo_nombramiento;
    }

    public boolean isLista_general() {
        return lista_general;
    }

    public void setLista_general(boolean lista_general) {
        this.lista_general = lista_general;
    }

    public boolean isLista_activos() {
        return lista_activos;
    }

    public void setLista_activos(boolean lista_activos) {
        this.lista_activos = lista_activos;
    }

    public boolean isLista_pasivos() {
        return lista_pasivos;
    }

    public void setLista_pasivos(boolean lista_pasivos) {
        this.lista_pasivos = lista_pasivos;
    }

    public boolean isNuevo_puesto() {
        return nuevo_puesto;
    }

    public void setNuevo_puesto(boolean nuevo_puesto) {
        this.nuevo_puesto = nuevo_puesto;
    }

    public boolean isModificar_puesto() {
        return modificar_puesto;
    }

    public void setModificar_puesto(boolean modificar_puesto) {
        this.modificar_puesto = modificar_puesto;
    }

    public boolean isEliminar_puesto() {
        return eliminar_puesto;
    }

    public void setEliminar_puesto(boolean eliminar_puesto) {
        this.eliminar_puesto = eliminar_puesto;
    }

    public boolean isNuevo_colaborador() {
        return nuevo_colaborador;
    }

    public void setNuevo_colaborador(boolean nuevo_colaborador) {
        this.nuevo_colaborador = nuevo_colaborador;
    }

    public boolean isModificar_colaborador() {
        return modificar_colaborador;
    }

    public void setModificar_colaborador(boolean modificar_colaborador) {
        this.modificar_colaborador = modificar_colaborador;
    }

    public boolean isEliminar_colaborador() {
        return eliminar_colaborador;
    }

    public void setEliminar_colaborador(boolean eliminar_colaborador) {
        this.eliminar_colaborador = eliminar_colaborador;
    }

    public boolean isNuevo_usuario() {
        return nuevo_usuario;
    }

    public void setNuevo_usuario(boolean nuevo_usuario) {
        this.nuevo_usuario = nuevo_usuario;
    }

    public boolean isAsignar_permisos() {
        return asignar_permisos;
    }

    public void setAsignar_permisos(boolean asignar_permisos) {
        this.asignar_permisos = asignar_permisos;
    }

    public boolean isModificar_usuarios() {
        return modificar_usuarios;
    }

    public void setModificar_usuarios(boolean modificar_usuarios) {
        this.modificar_usuarios = modificar_usuarios;
    }

    public boolean isEliminar_usuarios() {
        return eliminar_usuarios;
    }

    public void setEliminar_usuarios(boolean eliminar_usuarios) {
        this.eliminar_usuarios = eliminar_usuarios;
    }
    
    
    
}
