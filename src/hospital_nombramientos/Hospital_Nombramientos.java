/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital_nombramientos;

import Base_Datos.ConexionBaseDatos;
import Base_Datos.Config;
import GUI.FrmInicioSesion;
import javax.swing.JOptionPane;

/**
 *
 * @author Fabricio
 */
public class Hospital_Nombramientos {
    public static ConexionBaseDatos conexion;
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmInicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmInicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmInicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmInicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        try {
            String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "lib/Config.cfg";

            Config params = new Config(path);
            conexion = new ConexionBaseDatos(params.getProperty("User"),
                params.getProperty("Pass"),
                params.getProperty("Base"),
                params.getProperty("Server"),
                params.getProperty("Port"),
                "org.postgresql.Driver",
                params.getProperty("Schema"));

            conexion.conectar();
            if (conexion.isError()) {
                JOptionPane.showMessageDialog(null, "Error de conexión: \n"
                        + conexion.getErrorMsg());
                System.exit(0);
            } else {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new FrmInicioSesion(conexion).setVisible(true);
                    }
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se puedo realizar la conexion");
        }
        

        
    }
    
}
