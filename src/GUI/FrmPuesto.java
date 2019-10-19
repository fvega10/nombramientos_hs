/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Base_Datos.ConexionBaseDatos;
import Datos.PuestoD;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import logica.PermisoUsuario;
import logica.Puesto;

/**
 *
 * @author Fabricio
 */
public class FrmPuesto extends javax.swing.JDialog {
    ConexionBaseDatos conexion;
    ArrayList oPuesto;
    private boolean isModificar;
    private boolean isAceptar;
    private String codigoModificar;
    private int datosModificados;
    private AudioClip sonido;        // Para agregar sonido a un mensaje
    private AudioClip sonidoError;   // Para agregar sonido a un mensaje
    private PermisoUsuario oPermiso;
    
    private DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private String[] cabecera = {"Código", "Nombre de puesto"};
    
    private String[][] datos = new String[0][2];
    
    private void redimensionarMatriz() {
        String[][] respaldo = this.datos;   // Respalda los datos de la matriz principal en una auxiliar

        this.datos = new String[respaldo.length + 1][2]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) 
        {
            for (int j = 0; j < respaldo[0].length; j++) 
            {
                this.datos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    /**
     * Creates new form FrmPuesto
     */
    public FrmPuesto(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion, PermisoUsuario pPermiso) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        this.oPuesto = new ArrayList();
        this.isModificar = false;
        this.isAceptar = false;
        this.codigoModificar = "";
        this.datosModificados = 0;
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = pPermiso;
        this.cargarDatos();
        this.setTitle("Hospital San Carlos | Mantenimiento de personal");
        this.setLocationRelativeTo(null);
    }
    
    public FrmPuesto(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        this.oPuesto = new ArrayList();
        this.isModificar = false;
        this.isAceptar = false;
        this.codigoModificar = "";
        this.datosModificados = 0;
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = new PermisoUsuario(null, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false);
        this.cargarDatos();
        this.setTitle("Hospital San Carlos | Mantenimiento de personal");
        this.setLocationRelativeTo(null);
    }
    
    public boolean getAceptar(){
        return this.isAceptar;
    }
    
    public void limpiarDatos(){
        this.cpCodigo.setText("");
        this.cpNombrePuesto.setText("");
        this.isModificar = false;
        this.codigoModificar = "";
    }
    
    public void setPuesto(Puesto aux){
        this.cpCodigo.setText(aux.getCodigo());
        this.cpNombrePuesto.setText(aux.getNombre_puesto());
    }
    
    public Puesto getPuesto(){
        Puesto aux = new Puesto(this.cpCodigo.getText(), this.cpNombrePuesto.getText());
        return aux;
    }
    
    public void cargarDatos(){
        JTableHeader th;
        TableColumn columna;
        this.datos = new String[0][2];
        PuestoD oP = new PuestoD(conexion);
        oPuesto = oP.obtenerPuesto();
        
        if (!oP.isError())
        {
            if (this.oPuesto.size() > 0)
            {
                for (int i = 0; i<this.oPuesto.size(); i++)
                {
                    Puesto aux = (Puesto)this.oPuesto.get(i);
                    this.redimensionarMatriz();
                    this.datos[this.datos.length - 1][0] = aux.getCodigo();
                    this.datos[this.datos.length - 1][1] = aux.getNombre_puesto();
                }
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }
            else
            {
                this.modelo.setDataVector(null, cabecera);
                this.panel.setModel(modelo);
            }
            th = panel.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }
        else
        {
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oP.getErrorMsg());
        }
    }
    
    public void guardarDatos(){
        PuestoD oP = new PuestoD(conexion);
        this.conexion.IniciarTransacciones();
        
        if (isModificar)
        {
            if(this.oPermiso.isModificar_puesto()){
                oP.actualizarPersonal(this.getPuesto(), codigoModificar);
            }else{
                JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
                return;
            }
        }
        else
        {
            if(this.oPermiso.isNuevo_puesto()){
                oP.agregarPersonal(this.getPuesto());
            this.datosModificados++;
            }else{
                JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
                return;
            }
        }
        if(!oP.isError())
        {
            this.conexion.setCommit();
            this.sonido.play();
            JOptionPane.showMessageDialog(rootPane, "Registro agregado satisfactoriamente");
            this.isAceptar = true;
            this.cargarDatos();
            this.limpiarDatos();
            this.isModificar = false;
            this.codigoModificar = "";
        }
        else
        {
            this.sonidoError.play();
            this.conexion.setRollBack();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oP.getErrorMsg());
        }
    }
    
    public void modificarDatos(){
        if(this.oPermiso.isModificar_puesto()){
            Puesto aux = (Puesto)this.oPuesto.get(this.panel.getSelectedRow());
            this.isModificar = true;
            this.codigoModificar = aux.getCodigo();
            this.setPuesto(aux);
        }else{
            JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
            return;
        }
    }
    
    public void eliminarDatos(){
        if(this.oPermiso.isEliminar_puesto()){
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de borrar el registro?", "Borrar", JOptionPane.YES_NO_OPTION); // Muestra un mensaje de alerta
            if ( respuesta == JOptionPane.YES_OPTION ) {
                PuestoD oP = new PuestoD(conexion);
                this.conexion.IniciarTransacciones();
                Puesto aux = (Puesto)this.oPuesto.get(this.panel.getSelectedRow());
                oP.borrarDatos(aux.getCodigo());
                if(!oP.isError())
                {
                    this.sonido.play();
                    this.conexion.setCommit();
                    JOptionPane.showMessageDialog(rootPane, "Registro eliminado satisfactoriamente");
                    this.cargarDatos();
                }
                else
                {
                    this.sonidoError.play();
                    this.conexion.setRollBack();
                    JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oP.getErrorMsg());
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
        }
    }
    
    public void agregarPuesto(){
        
        if (this.cpCodigo.getText().equals("") ||
            this.cpNombrePuesto.getText().equals(""))
        {
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Alguno campos se encuentran vacíos");
        }
        else
        {
            this.guardarDatos();
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cpNombrePuesto = new javax.swing.JTextField();
        cpCodigo = new javax.swing.JTextField();
        btnAceptar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JTable();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nuevo puesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(0, 102, 153))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jLabel1.setText("Código:");

        jLabel2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jLabel2.setText("Nombre del puesto:");

        btnAceptar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-aceptar2.png"))); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });
        btnAceptar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAceptarKeyPressed(evt);
            }
        });

        btnLimpiar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-cancelar2.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cpNombrePuesto)
                            .addComponent(cpCodigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cpCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cpNombrePuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(0, 102, 153))); // NOI18N

        panel.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(panel);

        btnModificar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-modificar.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-eliminar.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 40, Short.MAX_VALUE))
        );

        jMenuBar2.setPreferredSize(new java.awt.Dimension(56, 35));

        jMenu3.setText("Archivo");
        jMenu3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu3.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/menu.png"))); // NOI18N
        jMenuItem1.setText("Ir al panel principal");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem1.setPreferredSize(new java.awt.Dimension(300, 30));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar2.add(jMenu3);

        jMenu4.setText("Ayuda");
        jMenu4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu4.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jMenuItem2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/logo-softclean.png"))); // NOI18N
        jMenuItem2.setText("Acerca del desarrollador");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        jMenuBar2.add(jMenu4);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        // TODO add your handling code here:
        this.agregarPuesto();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        this.limpiarDatos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (this.oPuesto.size() > 0)
        {
            if (this.panel.getSelectedRow() > -1)
            {
                this.modificarDatos();
            }
            else
            {
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Debe seleccionar el registro que desea modificar");
            }
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (this.oPuesto.size() > 0)
        {
            if (this.panel.getSelectedRow() > -1)
            {
                this.eliminarDatos();
            }
            else
            {
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Debe seleccionar el registro que desea modificar");
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        FrmContacto fC = new FrmContacto(null, true);
        fC.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void btnAceptarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAceptarKeyPressed
        // TODO add your handling code here:
        if(evt.getExtendedKeyCode() == evt.VK_ENTER)
        {
            this.agregarPuesto();
        }
    }//GEN-LAST:event_btnAceptarKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JTextField cpCodigo;
    private javax.swing.JTextField cpNombrePuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable panel;
    // End of variables declaration//GEN-END:variables
}
