/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Base_Datos.ConexionBaseDatos;
import Datos.PermisosD;
import Datos.PersonaD;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import logica.PermisoUsuario;
import logica.Usuario;

/**
 *
 * @author Fabricio
 */
public class FrmInicioSesion extends javax.swing.JFrame {
    ConexionBaseDatos conexion;
    Image ico;
    ArrayList usuarioList;
    private boolean isError;
    Usuario oUsuario;
    private AudioClip sonido;        // Para agregar sonido a un mensaje
    private AudioClip sonidoError;   // Para agregar sonido a un mensaje
    
    /**
     * Creates new form FrmInicioSesion
     */
    public FrmInicioSesion(ConexionBaseDatos pConexion) {
        initComponents();
        this.conexion = pConexion;
        this.oUsuario = null;
        this.isError = false;
        usuarioList = new ArrayList();
        this.cpUsuario.requestFocus();
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/inicio-sesion.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        ico = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Media/icono.png"));
        this.setSize(new Dimension(470, 324));
        this.setIconImage(ico);
        this.setTitle("Hospital San Carlos - Inicio de sesión");
        this.verificarExistenciasUsuarios();
        this.setLocationRelativeTo(null);
    }
    
    public void limpiarDatos(){
        this.cpPassword.setText("");
        this.cpUsuario.setText("");
    }
    
    public void verificarExistenciasUsuarios(){
        ArrayList oPersona = new ArrayList();
        PermisosD oD = new PermisosD(conexion);
        PersonaD pD = new PersonaD(conexion);
        this.usuarioList = oD.obtenerUsuarios();
        if(!oD.isError()){
            if(this.usuarioList.size() == 0){
                oPersona = pD.obtenerPersonal();
                if(oPersona.size() > 0){
                    FrmPermisos oFP = new FrmPermisos(this, true, conexion, this.generarPermiso(this.oUsuario));
                    oFP.setVisible(true);
                    if(oFP.GetAceptar()){
                        this.limpiarDatos();
                        this.cpUsuario.requestFocus();
                    }else{
                        sonidoError.play();
                        JOptionPane.showMessageDialog(rootPane, "No registraste ningún usuario.");
                        System.exit(0);
                    }
                }else{
                    sonidoError.play();
                    JOptionPane.showMessageDialog(rootPane, "Para iniciar sesión primero se debe de crear un super usuario");
                    FrmPersonal oP = new FrmPersonal(this, true, conexion);
                    oP.setVisible(true);
                    if(oP.GetAceptar()){
                        FrmPermisos oFP = new FrmPermisos(this, true, conexion);
                        oFP.setVisible(true);
                        if(oFP.GetAceptar()){
                            this.limpiarDatos();
                            this.cpUsuario.requestFocus();
                        }else{
                            sonidoError.play();
                            JOptionPane.showMessageDialog(rootPane, "No registraste ningún usuario.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }
    
    public String generarContrasennaCifradaMD5(String pass){
        String resultado = "";
        String md5 = null;
        String source = this.cpPassword.getText();
        try {
            MessageDigest mdEnc = MessageDigest.getInstance("MD5"); // Encryption algorithm
            mdEnc.update(source.getBytes(), 0, source.length());
            md5 = new BigInteger(1, mdEnc.digest()).toString(16); // Encrypted string
            resultado = md5; 
        
        }catch(Exception e){
        
        }
        return resultado;
    }
    
    public boolean compareDates(String user, String password){
        boolean isCorrect = false;
        PermisosD oD = new PermisosD(conexion);
        this.usuarioList = oD.obtenerUsuarios();
        if(!oD.isError()){
            if(this.usuarioList.size() > 0){
                for (int i = 0; i < this.usuarioList.size(); i++) {
                    Usuario aux = (Usuario)this.usuarioList.get(i);
                    if ( (aux.getNombre_usuario().equals(user)) &&
                        (aux.getConstrasenna().equals(password)) ){
                        this.oUsuario = aux;
                        isCorrect = true;
                    }
                }
            }
        }else{
            sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
        return isCorrect;
    }
    
    public PermisoUsuario generarPermiso(Usuario oUser){
        PermisoUsuario oP = null;
        PermisosD oPU = new PermisosD(conexion);
        oP = oPU.obtenerPermisosPorCedula(oUser.getoPersona().getCedula());
        if(!oPU.isError()){
            this.isError = true;
        }else{
            JOptionPane.showMessageDialog(rootPane, "Este usuario no cuanta con permisos");
        }
        return oP;
    }
    
    public void okButton(){
        if(this.cpUsuario.getText().equals("") ||
            this.cpPassword.getText().equals("")){
            sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Todos los campos son requeridos");
            this.cpUsuario.requestFocus();
        }else{
            String pass = this.generarContrasennaCifradaMD5(this.cpPassword.getText());
            if(compareDates(this.cpUsuario.getText(), pass)){
                sonido.play();
                PermisoUsuario oP = this.generarPermiso(this.oUsuario);
                if(this.isError){
                    FrmPanelPrincipal fp = new FrmPanelPrincipal(conexion, ico, oP);
                    this.setVisible(false);
                    fp.setVisible(true);
                }
                
            }else{
                sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "El usuario y/o contraseña son incorrectos");
                this.cpPassword.setText("");
                this.cpUsuario.requestFocus();
            }
            
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

        btnInicio = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cpUsuario = new javax.swing.JTextField();
        cpPassword = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        getContentPane().setLayout(null);

        btnInicio.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        btnInicio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-ingresar.png"))); // NOI18N
        btnInicio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicioActionPerformed(evt);
            }
        });
        btnInicio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnInicioKeyPressed(evt);
            }
        });
        getContentPane().add(btnInicio);
        btnInicio.setBounds(90, 206, 290, 50);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/user-2.png"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(80, 80, 60, 50);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn_pass.png"))); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(80, 140, 60, 50);

        cpUsuario.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        cpUsuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(cpUsuario);
        cpUsuario.setBounds(150, 80, 230, 40);

        cpPassword.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cpPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cpPasswordKeyPressed(evt);
            }
        });
        getContentPane().add(cpPassword);
        cpPassword.setBounds(150, 140, 230, 40);

        jLabel2.setFont(new java.awt.Font("Myriad Pro Light", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Inicio de sesión");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(60, 44, 350, 20);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/inicio-sesion.jpg"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 460, 314);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicioActionPerformed
        // TODO add your handling code here:
        this.okButton();
    }//GEN-LAST:event_btnInicioActionPerformed

    private void btnInicioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnInicioKeyPressed
        // TODO add your handling code here:
        if(evt.getExtendedKeyCode() == evt.VK_ENTER){
            this.okButton();
        }
    }//GEN-LAST:event_btnInicioKeyPressed

    private void cpPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpPasswordKeyPressed
        // TODO add your handling code here:
        if(evt.getExtendedKeyCode() == evt.VK_ENTER){
            this.okButton();
        }
    }//GEN-LAST:event_cpPasswordKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInicio;
    private javax.swing.JPasswordField cpPassword;
    private javax.swing.JTextField cpUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
