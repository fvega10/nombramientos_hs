/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Base_Datos.ConexionBaseDatos;
import Datos.PersonaD;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import logica.Personal;

/**
 *
 * @author Fabricio
 */
public class FrmBuscarPersonal extends javax.swing.JDialog {
    ConexionBaseDatos conexion;
    ArrayList lista; // objetos tipo Personal
    int posicion; // Posición del panel que sea seleccionada
    private boolean aceptar; // Si se seleccionó un dato correcto del panel
    
    
    private DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private String[] cabecera = {"Colaborador", "Fecha nacimiento", "Fecha inicio", "Estado", "Puesto actual"};
    
    private String[][] datos = new String[0][5];
    
    private void redimensionarMatriz() {
        String[][] respaldo = this.datos;   // Respalda los datos de la matriz principal en una auxiliar

        this.datos = new String[respaldo.length + 1][5]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.datos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    /**
     * Creates new form FrmBuscarPersonal
     */
    public FrmBuscarPersonal(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        this.posicion = 0;
        lista = new ArrayList();
        this.cargarDatos();
        this.setTitle("Hospital San Carlos | Buscar persona");
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Si se selecciono un dato correcto
     * @return True si es correcto
     */
    public boolean isAceptar(){
        return aceptar;
    }
    
    /**
     * Carga el panel con objetos de tipo persona
     */
    public void cargarDatos(){
        this.datos = new String[0][4]; // datos que se mostrarán en el panel
        String estado = ""; // Si True = En Propiedad / Si false = Interino
        PersonaD oD = new PersonaD(conexion);
        lista = oD.obtenerPersonal(); // Se recuperan los datos de la base de datos
        if (!oD.isError()) 
        { 
            if (lista.size() > 0) 
            {
                for(int i = 0; i<lista.size(); i++)
                {
                    Personal aux = (Personal)this.lista.get(i); // Se inicializa el objeto de la posición i según la lista de objetos persona
                    this.redimensionarMatriz();
                    this.datos[this.datos.length - 1][0] = aux.toString();
                    this.datos[this.datos.length - 1][1] = String.valueOf(aux.getFecha_nacimiento());
                    this.datos[this.datos.length - 1][2] = String.valueOf(aux.getFecha_ingreso_institucion());
                    if(aux.isConPropiedad()){
                        estado = "En propiedad";
                    }else{
                        estado = "Interino";
                    }
                    this.datos[this.datos.length - 1][3] = estado;
                    this.datos[this.datos.length - 1][4] = aux.getCod_puesto().getNombre_puesto();
                }
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }else
            {
                this.modelo.setDataVector(null, cabecera);
                this.panel.setModel(modelo);
            }
        }else
        {
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    /**
     * Retornará el filtro de datos según los escrito en el campo correspondiente a la búsqueda
     * @param nombre Servirá para buscar en la base de datos
     */
    public void filtrarDatos(String nombre){
        this.datos = new String[0][4];
        String estado = "";
        PersonaD oD = new PersonaD(conexion);
        lista = oD.obtenerPersonalPorNombre(nombre);
        if(!oD.isError()){
            if(lista.size() > 0){
                for(int i = 0; i<lista.size(); i++){
                    Personal aux = (Personal)this.lista.get(i);
                    this.redimensionarMatriz();
                    this.datos[this.datos.length - 1][0] = aux.toString();
                    this.datos[this.datos.length - 1][1] = String.valueOf(aux.getFecha_nacimiento());
                    this.datos[this.datos.length - 1][2] = String.valueOf(aux.getFecha_ingreso_institucion());
                    if(aux.isConPropiedad()){
                        estado = "En propiedad";
                    }else{
                        estado = "Interino";
                    }
                    this.datos[this.datos.length - 1][3] = estado;
                    this.datos[this.datos.length - 1][4] = aux.getCod_puesto().getNombre_puesto();
                }
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }else{
                this.modelo.setDataVector(null, cabecera);
                this.panel.setModel(modelo);
            }
        }else{
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    /**
     * Recupera el objeto seleccionado en el panel
     * @return Objeto tipo persona
     */
    public Personal getPersonal(){
        Personal oP = (Personal)this.lista.get(this.posicion);
        return oP;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JTable();
        btnSeleccionar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cpFiltro = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(0, 102, 153))); // NOI18N

        panel.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        panel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(panel);

        btnSeleccionar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnSeleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-aceptar2.png"))); // NOI18N
        btnSeleccionar.setText("Seleccionar");
        btnSeleccionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarActionPerformed(evt);
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
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(273, 273, 273)
                .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtro información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(0, 102, 153))); // NOI18N

        cpFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cpFiltroKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Filtro por nombre de colaborador");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cpFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cpFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenuBar1.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N
        jMenuBar1.setPreferredSize(new java.awt.Dimension(56, 30));

        jMenu1.setText("Archivo");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

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
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ayuda");
        jMenu2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jMenuItem2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/logo-softclean.png"))); // NOI18N
        jMenuItem2.setText("Acerca del desarrollador");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cpFiltroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpFiltroKeyReleased
        // TODO add your handling code here:
        if(this.cpFiltro.getText().equals("")){
            this.cargarDatos();
        }else{
            this.filtrarDatos(this.cpFiltro.getText());
        }
        
    }//GEN-LAST:event_cpFiltroKeyReleased

    private void panelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() >= 2){
            this.posicion = this.panel.getSelectedRow();
            this.aceptar = true;
            this.setVisible(false);
        }
    }//GEN-LAST:event_panelMouseClicked

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
        // TODO add your handling code here:
        if(this.panel.getSelectedRow() > -1){
            this.posicion = this.panel.getSelectedRow();
            this.aceptar = true;
            this.setVisible(false);
        }
    }//GEN-LAST:event_btnSeleccionarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        
        this.cpFiltro.setText("");
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JTextField cpFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable panel;
    // End of variables declaration//GEN-END:variables
}
