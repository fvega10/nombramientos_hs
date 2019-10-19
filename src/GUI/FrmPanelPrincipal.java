/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Base_Datos.ConexionBaseDatos;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import logica.PermisoUsuario;

/**
 *
 * @author Fabricio
 */
public class FrmPanelPrincipal extends javax.swing.JFrame {
    ConexionBaseDatos conexion;
    PermisoUsuario pPermiso;
    /**
     * Creates new form FrmPanelPrincipal
     */
    public FrmPanelPrincipal(ConexionBaseDatos pConexion, Image pIcon, PermisoUsuario oPermiso) {
        initComponents();
        this.conexion = pConexion;
        this.pPermiso = oPermiso;
        this.comprobarPermisos();
        this.setVisible(true);
        this.setSize(new Dimension(859, 550));
        this.setIconImage(pIcon);
        this.setTitle("Hospital San Carlos | Panel principal");
        this.setLocationRelativeTo(null);
    }
    
    public void comprobarPermisos(){
        if(
            !(this.pPermiso.isNuevo_puesto()) && 
            !(this.pPermiso.isEliminar_puesto()) && 
            !(this.pPermiso.isModificar_puesto()) 
        )
        {
        
            this.btnPuestos.setEnabled(false);
        
        }
        else if( 
            !(this.pPermiso.isEliminar_colaborador()) && 
            !(this.pPermiso.isModificar_colaborador()) && 
            !(this.pPermiso.isNuevo_colaborador()) 
        )
        {
        
            this.btnPersonal.setEnabled(false);
        
        }
        else if( 
            !(this.pPermiso.isAsignar_nuevo_nombramiento()) && 
            !(this.pPermiso.isLista_activos()) && 
            !(this.pPermiso.isLista_general()) && 
            !(this.pPermiso.isLista_pasivos()) && 
            !(this.pPermiso.isNombramiento_en_curso()) 
        )
        {
        
            this.btnNombramientos.setEnabled(false);
        
        }
        else if( 
                !(this.pPermiso.isAsignar_permisos()) && 
                !(this.pPermiso.isEliminar_usuarios()) && 
                !(this.pPermiso.isModificar_usuarios()) && 
                !(this.pPermiso.isNuevo_usuario())
        )
        {
        
            this.btnPermisos.setEnabled(false);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnNombramientos = new javax.swing.JButton();
        btnPersonal = new javax.swing.JButton();
        btnPuestos = new javax.swing.JButton();
        btnPermisos = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        btnNombramientos.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        btnNombramientos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-nombramientos.png"))); // NOI18N
        btnNombramientos.setMnemonic('N');
        btnNombramientos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNombramientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNombramientosActionPerformed(evt);
            }
        });
        getContentPane().add(btnNombramientos);
        btnNombramientos.setBounds(80, 200, 340, 80);

        btnPersonal.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        btnPersonal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-colaboradores.png"))); // NOI18N
        btnPersonal.setMnemonic('C');
        btnPersonal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPersonal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPersonalActionPerformed(evt);
            }
        });
        getContentPane().add(btnPersonal);
        btnPersonal.setBounds(440, 200, 340, 80);

        btnPuestos.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        btnPuestos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-puestos.png"))); // NOI18N
        btnPuestos.setMnemonic('P');
        btnPuestos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPuestos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPuestos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPuestosActionPerformed(evt);
            }
        });
        getContentPane().add(btnPuestos);
        btnPuestos.setBounds(80, 320, 340, 80);

        btnPermisos.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        btnPermisos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-permisos.png"))); // NOI18N
        btnPermisos.setMnemonic('R');
        btnPermisos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPermisos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPermisosActionPerformed(evt);
            }
        });
        getContentPane().add(btnPermisos);
        btnPermisos.setBounds(440, 320, 340, 80);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/img-panel-principal.png.jpg"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 860, 510);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPersonalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPersonalActionPerformed
        // TODO add your handling code here:
        FrmPersonal oFPE = new FrmPersonal(this, true, conexion, this.pPermiso);
        oFPE.setVisible(true);
    }//GEN-LAST:event_btnPersonalActionPerformed

    private void btnNombramientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNombramientosActionPerformed
        // TODO add your handling code here:
        FrmNombramientos oFN = new FrmNombramientos(this, true, conexion, this.pPermiso);
        oFN.setVisible(true);
    }//GEN-LAST:event_btnNombramientosActionPerformed

    private void btnPuestosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPuestosActionPerformed
        // TODO add your handling code here:
        FrmPuesto oFP = new FrmPuesto(this, true, conexion, this.pPermiso);
        oFP.setVisible(true);
    }//GEN-LAST:event_btnPuestosActionPerformed

    private void btnPermisosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPermisosActionPerformed
        // TODO add your handling code here:
        FrmPermisos oPE = new FrmPermisos(this, true, conexion, this.pPermiso);
        oPE.setVisible(true);
    }//GEN-LAST:event_btnPermisosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNombramientos;
    private javax.swing.JButton btnPermisos;
    private javax.swing.JButton btnPersonal;
    private javax.swing.JButton btnPuestos;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
