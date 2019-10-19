/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Base_Datos.ConexionBaseDatos;
import Datos.NombramientoD;
import Datos.PersonaD;
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
import logica.Personal;

/**
 *
 * @author Fabricio
 */
public class FrmPersonal extends javax.swing.JDialog {
    ConexionBaseDatos conexion;
    ArrayList oPersonal;
    ArrayList pListaPuestos;
    public int personasNuevas;
    private boolean isModificar;
    private boolean isAceptar;
    private String cedulaModificar;
    private AudioClip sonido;        // Para agregar sonido a un mensaje
    private AudioClip sonidoError;   // Para agregar sonido a un mensaje
    private PermisoUsuario oPermiso;
    
    private DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private String[] cabecera = {"Colaborador", "Fecha Nacimiento", "Fecha Ingreso", "Código presupuesto", "Estado", "Puesto actual"};
    
    private String[][] datos = new String[0][6];
    
    private void redimensionarMatriz() {
        String[][] respaldo = this.datos;   // Respalda los datos de la matriz principal en una auxiliar

        this.datos = new String[respaldo.length + 1][6]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.datos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    /**
     * Creates new form FrmPersonal
     */
    public FrmPersonal(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion, PermisoUsuario pPermiso) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        oPersonal = new ArrayList();
        pListaPuestos = new ArrayList();
        isModificar = false;
        this.isAceptar = false;
        cedulaModificar = "";
        personasNuevas = 0;
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = pPermiso;
        this.cargarDatos();
        this.cargarComboBox();
        this.setTitle("Hospital San Carlos | Mantenimiento de puestos");
        this.setLocationRelativeTo(null);
    }
    
    public FrmPersonal(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        oPersonal = new ArrayList();
        pListaPuestos = new ArrayList();
        isModificar = false;
        this.isAceptar = false;
        cedulaModificar = "";
        personasNuevas = 0;
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = new PermisoUsuario(null, false, false, false, false, false, false, false, false, true, true, true, false, false, false, false);
        this.cargarDatos();
        this.cargarComboBox();
        this.setTitle("Hospital San Carlos | Mantenimiento de puestos");
        this.setLocationRelativeTo(null);
    }
    
    public boolean GetAceptar(){
        return this.isAceptar;
    }

    public void cargarDatos(){
        String estado = "";
        JTableHeader th;
        TableColumn columna;
        PersonaD pD = new PersonaD(conexion);
        this.datos = new String[0][6];
        oPersonal = pD.obtenerPersonal();
        if(!pD.isError()){
            if(oPersonal.size() > 0){
                for(int i = 0; i<oPersonal.size(); i++){
                    Personal aux = (Personal)oPersonal.get(i);
                    this.redimensionarMatriz();
                    this.datos[this.datos.length - 1][0] = aux.toString();
                    this.datos[this.datos.length - 1][1] = String.valueOf(aux.getFecha_nacimiento());
                    this.datos[this.datos.length - 1][2] = String.valueOf(aux.getFecha_ingreso_institucion());
                    this.datos[this.datos.length - 1][3] = aux.getCodigo_presupuesto();
                    if(aux.isConPropiedad()){
                        estado = "En propiedad";
                    }else{
                        estado = "Interino";
                    }
                    this.datos[this.datos.length - 1][4] = estado;
                    this.datos[this.datos.length - 1][5] = aux.getCod_puesto().getNombre_puesto();
                }
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }else{
                this.modelo.setDataVector(null, cabecera);
                this.panel.setModel(modelo);
            }
            columna=panel.getColumnModel().getColumn(0);
            columna.setMinWidth(155);
            columna=panel.getColumnModel().getColumn(0);
            columna.setMinWidth(100);
            th = panel.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + pD.getErrorMsg());
        }
    }
    
    public void limpiarDatos(){
        this.cedula.setText("");
        this.nombre.setText("");
        this.primer_apellido.setText("");
        this.segundo_apellido.setText("");
        this.fecha_nacimiento.setDate(null);
        this.fecha_ingreso.setDate(null);
        this.codigo_presupuesto.setText("");
        this.cbxEstado.setSelectedIndex(0);
        this.cbxEstado.setSelectedIndex(-1);
        this.cbxPuestoActual.setSelectedIndex(-1);
        this.isModificar = false;
        this.cedulaModificar = "";
    }
    
    public void setPersonal(Personal dato){
        this.cedula.setText(dato.getCedula());
        this.nombre.setText(dato.getNombre());
        this.primer_apellido.setText(dato.getApellido1());
        this.segundo_apellido.setText(dato.getApellido2());
        this.fecha_nacimiento.setDate(dato.getFecha_nacimiento());
        this.fecha_ingreso.setDate(dato.getFecha_ingreso_institucion());
        this.codigo_presupuesto.setText(dato.getCodigo_presupuesto());
        if(dato.isConPropiedad()){
            this.cbxEstado.setSelectedItem("En propiedad");
        }else{
            this.cbxEstado.setSelectedItem("Interino");
        }
        for(int i = 0; i<this.pListaPuestos.size(); i++){
            Puesto aux = (Puesto)this.pListaPuestos.get(i);
            if(dato.getCod_puesto().getNombre_puesto().equals(aux.getNombre_puesto())){
                this.cbxPuestoActual.setSelectedItem(aux.getNombre_puesto());
                break;
            }
        }
    }
    
    public Personal getPersonal(){
        boolean enPropiedad = false;
        if(this.cbxEstado.getSelectedItem().toString().equals("En propiedad")){
            enPropiedad = true;
        }
        Puesto oP = (Puesto)this.pListaPuestos.get(this.cbxPuestoActual.getSelectedIndex());
        Personal p = new Personal(
                this.cedula.getText().trim(), 
                this.nombre.getText().trim(), 
                this.primer_apellido.getText().trim(), 
                this.segundo_apellido.getText().trim(), 
                this.fecha_nacimiento.getDate(), 
                this.fecha_ingreso.getDate(), 
                this.codigo_presupuesto.getText().trim(), 
                enPropiedad,
                oP,
                0);
        return p;
    }
    
    public void guardarPersonaBD(){
        String mensaje = "";
        PersonaD oD = new PersonaD(conexion);
        NombramientoD oND = new NombramientoD(conexion);
        this.conexion.IniciarTransacciones(); // Inicia las transacciones con la base de datos
        if(isModificar){
            if(this.oPermiso.isModificar_colaborador()){
                oD.actualizarPersona(this.getPersonal(), cedulaModificar);
            }else{
                JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
                return;
            }
        }else{
            if(this.oPermiso.isNuevo_colaborador()){
                oD.agregarPersona(this.getPersonal());
            }else{
                JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
                return;
            }
            
        }
        if(!oD.isError()){
            if(isModificar){
                mensaje = "modificado";
                this.isModificar = false;
                this.cedulaModificar = "";
            }else{
                mensaje = "agregado";
                this.isAceptar = true;
                this.personasNuevas++;
            }
            this.sonido.play();
            this.conexion.setCommit();
            JOptionPane.showMessageDialog(rootPane, "Registro modificado satisfactoriamente");
            this.cargarDatos();
            this.limpiarDatos();
        }else{
            this.sonidoError.play();
            this.conexion.setRollBack();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    public void modificarDatos(){
        if(this.oPermiso.isModificar_colaborador()){
            Personal aux = (Personal)this.oPersonal.get(this.panel.getSelectedRow());
            this.setPersonal(aux);
            isModificar = true;
            cedulaModificar = aux.getCedula();
        }else{
            JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
        }
    }
    
    public void eliminarPersonal(){
        if(this.oPermiso.isEliminar_colaborador()){
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de borrar el registro?", "Borrar", JOptionPane.YES_NO_OPTION); // Muestra un mensaje de alerta
            if ( respuesta == JOptionPane.YES_OPTION ) {
                PersonaD oD = new PersonaD(conexion);
                this.conexion.IniciarTransacciones();
                Personal aux = (Personal)this.oPersonal.get(this.panel.getSelectedRow());
                oD.borrarDatos(aux.getCedula());
                if(!oD.isError()){
                    this.sonido.play();
                    this.conexion.setCommit();
                    JOptionPane.showMessageDialog(rootPane, "Registro eliminado satisfactoriamente");
                    this.cargarDatos();
                    this.limpiarDatos();
                }else{
                    this.sonidoError.play();
                    this.conexion.setRollBack();
                    JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane, "No tiene los permisos para realizar esta acción");
        }
        
    }
    
    /**
     * Carga el combo de objeto tipo Puesto
     */
    public void cargarComboBox(){
        pListaPuestos = new ArrayList();
        PuestoD oD = new PuestoD(conexion);
        pListaPuestos = oD.obtenerPuesto();
        if(!oD.isError()){
            if(pListaPuestos.size() > 0){
                for(int i = 0; i<this.pListaPuestos.size(); i++){
                    Puesto aux = (Puesto)this.pListaPuestos.get(i);
                    this.cbxPuestoActual.addItem(aux.getNombre_puesto());
                }
                this.cbxPuestoActual.setSelectedIndex(-1);
            }else{
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Para crear un usuario primero deben de existir al menos un puesto de trabajo");
                FrmPuesto oFP = new FrmPuesto(null, true, conexion, this.oPermiso);
                oFP.setVisible(true);
                if(oFP.getAceptar()){
                    this.cargarComboBox();
                }else{
                    this.btnAceptar.setEnabled(false);
                }      
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrio un error: " + oD.getErrorMsg());
        }
    }
    
    public void botonAgregar(){
        String fecha_naci = String.valueOf(this.fecha_nacimiento.getDate());
        String fecha_ingreso = String.valueOf(this.fecha_ingreso.getDate());
        if(this.cedula.getText().equals("") ||
            this.nombre.getText().equals("") ||
            this.primer_apellido.getText().equals("") ||
            this.segundo_apellido.getText().equals("") ||
            fecha_naci.equals("") ||
            fecha_ingreso.equals("") ||
            this.cbxPuestoActual.getSelectedIndex() < 0){
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Algunos campos se encuentra vacíos");
        }else{
            if(this.cbxEstado.getSelectedIndex() > -1){
                this.guardarPersonaBD();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        nombre = new javax.swing.JTextField();
        primer_apellido = new javax.swing.JTextField();
        segundo_apellido = new javax.swing.JTextField();
        cbxEstado = new javax.swing.JComboBox();
        fecha_nacimiento = new com.toedter.calendar.JDateChooser();
        fecha_ingreso = new com.toedter.calendar.JDateChooser();
        codigo_presupuesto = new javax.swing.JTextField();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        cedula = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cbxPuestoActual = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JTable();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nuevo registro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(0, 102, 153))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel1.setText("Cédula:");

        jLabel2.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel2.setText("Nombre:");

        jLabel3.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel3.setText("Fecha nacimiento:");

        jLabel4.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel4.setText("Primer apellido:");

        jLabel5.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel5.setText("Segundo apellido:");

        jLabel6.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel6.setText("Fecha ingreso:");

        jLabel7.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel7.setText("Código presupuesto:");

        jLabel8.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel8.setText("Estado:");

        nombre.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        primer_apellido.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        segundo_apellido.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        cbxEstado.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cbxEstado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "En propiedad", "Interino" }));
        cbxEstado.setSelectedIndex(-1);

        fecha_nacimiento.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        fecha_ingreso.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        codigo_presupuesto.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

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

        btnCancelar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-cancelar2.png"))); // NOI18N
        btnCancelar.setText("Limpiar");
        btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        cedula.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel9.setText("Puesto actual:");

        cbxPuestoActual.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(52, 52, 52)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nombre, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(primer_apellido, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(segundo_apellido)
                                    .addComponent(cedula)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbxEstado, 0, 284, Short.MAX_VALUE)
                                    .addComponent(fecha_ingreso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(fecha_nacimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(codigo_presupuesto)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(cbxPuestoActual, 0, 284, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cedula, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(primer_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(segundo_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(fecha_nacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fecha_ingreso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(codigo_presupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbxPuestoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(237, 237, 237)
                        .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 782, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenuBar1.setPreferredSize(new java.awt.Dimension(92, 35));

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
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        // TODO add your handling code here:
        this.botonAgregar();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        this.limpiarDatos();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if(this.oPersonal.size() > 0){
            if(this.panel.getSelectedRow() > -1){
                this.modificarDatos();
            }else{
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Debe seleccionar el registro que desea modificar");
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Aún no existen registros");
        }
        
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        this.eliminarPersonal();
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
            this.botonAgregar();
        }
    }//GEN-LAST:event_btnAceptarKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JComboBox cbxEstado;
    private javax.swing.JComboBox cbxPuestoActual;
    private javax.swing.JTextField cedula;
    private javax.swing.JTextField codigo_presupuesto;
    private com.toedter.calendar.JDateChooser fecha_ingreso;
    private com.toedter.calendar.JDateChooser fecha_nacimiento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nombre;
    private javax.swing.JTable panel;
    private javax.swing.JTextField primer_apellido;
    private javax.swing.JTextField segundo_apellido;
    // End of variables declaration//GEN-END:variables
}
