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
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import logica.PermisoUsuario;
import logica.Usuario;
import logica.Personal;

/**
 *
 * @author Fabricio
 */
public class FrmPermisos extends javax.swing.JDialog {
    ConexionBaseDatos conexion;
    private ArrayList pColaboradores;
    private ArrayList pUsuarios;
    private ArrayList pPermisos;
    private boolean isModificarUsuario;
    private boolean isAceptar;
    private boolean isPermitido;
    private String cedulaEditar;
    private AudioClip sonido;        // Para agregar sonido a un mensaje
    private AudioClip sonidoError;   // Para agregar sonido a un mensaje
    private PermisoUsuario oPermiso;
    
    /*Panel lista de nombramientos*/
    private DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private String[] cabecera = {"Colaborador", "Nombre de Usuario"};
    
    private String[][] datos = new String[0][2];
    
    private void redimensionarMatriz() {
        String[][] respaldo = this.datos;   // Respalda los datos de la matriz principal en una auxiliar

        this.datos = new String[respaldo.length + 1][2]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.datos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    /**
     * Creates new form FrmPermisos
     */
    public FrmPermisos(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion, PermisoUsuario pPermiso) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        this.pColaboradores = new ArrayList();
        this.pPermisos = new ArrayList();
        pUsuarios = new ArrayList();
        isModificarUsuario = false;
        this.isAceptar = false;
        this.isPermitido = false;
        cedulaEditar = "";
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = pPermiso;
        this.cargarComboBoxColaboradores();
        this.cargarPermisosUsuario();
        this.cargarComboxUsuariosRegistrados();
        this.limpiarNuevoUsuario();
        this.limpiarPermisos();
        this.asignarPermisos();
        this.cbxColaborador.setRequestFocusEnabled(true);
        this.setTitle("Hospital San Carlos | Permisos de usuario");
        this.setLocationRelativeTo(null);
    }
    
    public FrmPermisos(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        this.pColaboradores = new ArrayList();
        this.pPermisos = new ArrayList();
        pUsuarios = new ArrayList();
        isModificarUsuario = false;
        this.isAceptar = false;
        this.isPermitido = false;
        cedulaEditar = "";
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = new PermisoUsuario(null, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true);
        this.cargarComboBoxColaboradores();
        this.cargarPermisosUsuario();
        this.cargarComboxUsuariosRegistrados();
        this.limpiarNuevoUsuario();
        this.limpiarPermisos();
        this.asignarPermisos();
        this.cbxColaborador.setRequestFocusEnabled(true);
        this.setTitle("Hospital San Carlos | Permisos de usuario");
        this.setLocationRelativeTo(null);
    }
    
    public void asignarPermisos(){
        if(!this.oPermiso.isAsignar_permisos()){
            this.cbxColaborador2.setEnabled(false);
            this.btnAsignarPermisos.setEnabled(false);
        }else if(!this.oPermiso.isNuevo_usuario()){
            this.cbxColaborador.setEnabled(false);
            this.cpUser.setEnabled(false);
            this.cpContrasenna.setEnabled(false);
            this.cpContrasenna2.setEnabled(false);
            this.btnAgregarNuevoUsuario.setEnabled(false);
        }else if(!this.oPermiso.isModificar_usuarios()){
            this.btnModificarUsuario.setEnabled(false);
        }else if(!this.oPermiso.isEliminar_usuarios()){
            this.btnEliminarUsuario.setEnabled(false);
        }
    }
    
    public boolean GetAceptar(){
        return this.isAceptar;
    }
    
    public void cargarComboBoxColaboradores(){
        PersonaD oP = new PersonaD(conexion);
        if(this.pColaboradores.size() > 0){
            this.cbxColaborador.removeAllItems();
        }
        this.pColaboradores = oP.obtenerPersonal();
        if(!oP.isError()){
            if(this.pColaboradores.size() > 0){
                for(int i = 0; i<this.pColaboradores.size(); i++){
                    Personal aux = (Personal)this.pColaboradores.get(i);
                    this.cbxColaborador.addItem(aux.toString());
                }
                this.cbxColaborador.setSelectedIndex(-1);
                this.isPermitido = true;
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oP.getErrorMsg());
        }
    }
    
    public void cargarComboxUsuariosRegistrados(){
        JTableHeader th;
        TableColumn columna;
        PermisosD oD = new PermisosD(conexion);
        this.datos = new String[0][2];
        if(this.pUsuarios.size() > 0){
            this.cbxColaborador2.removeAllItems();
        }
        this.pUsuarios = oD.obtenerUsuarios();
        if(!oD.isError()){
            if(this.pUsuarios.size() > 0){
                for(int i = 0; i<this.pUsuarios.size(); i++){
                    Usuario oR = (Usuario)this.pUsuarios.get(i);
                    this.redimensionarMatriz();
                    this.cbxColaborador2.addItem(oR.getoPersona().toString());
                    this.datos[this.datos.length - 1][0] = oR.getoPersona().toString();
                    this.datos[this.datos.length - 1][1] = oR.getNombre_usuario();
                }
                this.cbxColaborador2.setSelectedIndex(-1);
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }else{
                this.modelo.setDataVector(null, cabecera);
                this.panel.setModel(modelo);
            }
            th = panel.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    public void cargarPermisosUsuario(){
        PermisosD oD = new PermisosD(conexion);
        this.pPermisos = oD.obtenerPermisos();
        if(oD.isError()){
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    public Usuario getUsuario(){
        Personal oR = (Personal)this.pColaboradores.get(this.cbxColaborador.getSelectedIndex());
        Usuario oU = new Usuario(oR, this.cpUser.getText(), this.cpContrasenna.getText());
        return oU;
    }
    
    public String obtenerNombraUsuario(Personal oR){
        String valor = "";
        String[] nombre;
        valor = oR.getNombre() + "-" + oR.getApellido1();
        nombre = valor.split("-");
        valor = nombre[0].toLowerCase().charAt(0) + nombre[1].toLowerCase();
        
        return valor;
    }
    
    public Usuario getUsuarioPermiso(){
        Personal oR = (Personal)this.pColaboradores.get(this.cbxColaborador2.getSelectedIndex());
        Usuario oU = new Usuario(oR, this.cpUser.getText());
        return oU;
    }
    
    public PermisoUsuario getPermisos(){
        PermisoUsuario oP = new PermisoUsuario(this.getUsuario(), 
                this.chkEnCurso.isSelected(), 
                this.chkAsignarNuevoNombramiento.isSelected(), 
                this.chkListaGeneral.isSelected(), 
                this.chkListaActivos.isSelected(), 
                this.chkListaPasivos.isSelected(), 
                this.chkNuevoPuesto.isSelected(), 
                this.chkModificarPuesto.isSelected(), 
                this.chkEliminarPuesto.isSelected(), 
                this.chkNuevoColaborador.isSelected(), 
                this.chkModificarColaborador.isSelected(), 
                this.chkEliminarColaborador.isSelected(), 
                this.chkNuevoPermiso.isSelected(), 
                this.chkAsignarPermisos.isSelected(), 
                this.chkModificarUsuario.isSelected(), 
                this.chkEliminarUsuario.isSelected());
        return oP;
    }
    
    public PermisoUsuario getPermisos2(){
        PermisoUsuario oP = new PermisoUsuario(this.getUsuarioPermiso(), 
                this.chkEnCurso.isSelected(), 
                this.chkAsignarNuevoNombramiento.isSelected(), 
                this.chkListaGeneral.isSelected(), 
                this.chkListaActivos.isSelected(), 
                this.chkListaPasivos.isSelected(), 
                this.chkNuevoPuesto.isSelected(), 
                this.chkModificarPuesto.isSelected(), 
                this.chkEliminarPuesto.isSelected(), 
                this.chkNuevoColaborador.isSelected(), 
                this.chkModificarColaborador.isSelected(), 
                this.chkEliminarColaborador.isSelected(), 
                this.chkNuevoPermiso.isSelected(), 
                this.chkAsignarPermisos.isSelected(), 
                this.chkModificarUsuario.isSelected(), 
                this.chkEliminarUsuario.isSelected());
        return oP;
    }
    
    public void setPermisos(PermisoUsuario oPermiso){
                this.chkEnCurso.setSelected(oPermiso.isNombramiento_en_curso()); 
                this.chkAsignarNuevoNombramiento.setSelected(oPermiso.isAsignar_nuevo_nombramiento()); 
                this.chkListaGeneral.setSelected(oPermiso.isLista_general()); 
                this.chkListaActivos.setSelected(oPermiso.isLista_activos()); 
                this.chkListaPasivos.setSelected(oPermiso.isLista_pasivos()); 
                this.chkNuevoPuesto.setSelected(oPermiso.isNuevo_puesto()); 
                this.chkModificarPuesto.setSelected(oPermiso.isModificar_puesto()); 
                this.chkEliminarPuesto.setSelected(oPermiso.isEliminar_puesto()); 
                this.chkNuevoColaborador.setSelected(oPermiso.isNuevo_colaborador()); 
                this.chkModificarColaborador.setSelected(oPermiso.isModificar_colaborador()); 
                this.chkEliminarColaborador.setSelected(oPermiso.isEliminar_colaborador()); 
                this.chkNuevoPermiso.setSelected(oPermiso.isNuevo_usuario());
                this.chkAsignarPermisos.setSelected(oPermiso.isAsignar_permisos()); 
                this.chkModificarUsuario.setSelected(oPermiso.isModificar_usuarios()); 
                this.chkEliminarUsuario.setSelected(oPermiso.isEliminar_usuarios());
    }
    
    public void setUsuario(Usuario oUsuario){
        for(int i = 0; i<this.pColaboradores.size(); i++){
            Personal oP = (Personal)this.pColaboradores.get(i);
            if(oP.toString().equals(oUsuario.getoPersona().toString())){
                this.cbxColaborador.setSelectedItem(oP.toString());
                this.cpUser.setText(oUsuario.getNombre_usuario());
                isModificarUsuario = true;
                break;
            }
        }
    }
    
    public void guardarUsuario(){
        PermisosD oD = new PermisosD(conexion);
        this.conexion.IniciarTransacciones();
        if(isModificarUsuario){
            oD.actualizarUsuario(this.getUsuario(), this.cedulaEditar);
        }else{
            oD.agregarUsuario(this.getUsuario());
        }   
        if(!oD.isError()){
            if(isModificarUsuario){
                this.sonido.play();
                this.conexion.setCommit();
                JOptionPane.showMessageDialog(rootPane, "Registro modificado satisfactoriamente");
                this.isModificarUsuario = false;
                this.cedulaEditar = "";
                this.cargarPermisosUsuario();
                this.cargarComboxUsuariosRegistrados();
            }else{
                String cedula = this.getPermisos().getoUsuario().getoPersona().getCedula();
                oD.agregarPermisosUsuarioNuevo(cedula);
                if(!oD.isError()){
                    this.sonido.play();
                    this.conexion.setCommit();
                    JOptionPane.showMessageDialog(rootPane, "Registro agregado satisfactoriamente");
                    this.isAceptar = true;
                    this.cargarPermisosUsuario();
                    this.cargarComboxUsuariosRegistrados();
                }else{
                    this.sonidoError.play();
                    this.conexion.setRollBack();
                    JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
                }
            }
            this.limpiarNuevoUsuario();
            this.limpiarPermisos();
        }else{
            this.sonidoError.play();
            this.conexion.setRollBack();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
            this.limpiarNuevoUsuario();
        }
    }
    
    public void guardarPermisos(String pCedulaActualizar){
        PermisosD oD = new PermisosD(conexion);
        this.conexion.IniciarTransacciones();
        oD.actualizarPermisos(this.getPermisos2(), pCedulaActualizar);
        if(!oD.isError()){
            this.sonido.play();
            this.conexion.setCommit();
            JOptionPane.showMessageDialog(rootPane, "Registro modificado satisfactoriamente");
            this.limpiarPermisos();
            this.cargarPermisosUsuario();
        }else{
            this.sonidoError.play();
            this.conexion.setRollBack();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    public void limpiarNuevoUsuario(){
        this.cbxColaborador.setSelectedIndex(-1);
        this.cpContrasenna.setText("");
        this.cpContrasenna2.setText("");
        this.cpUser.setText("");
    }
    
    public void limpiarPermisos(){
        this.cbxColaborador2.setSelectedIndex(-1);
        this.chkAsignarNuevoNombramiento.setSelected(false);
        this.chkAsignarPermisos.setSelected(false);
        this.chkEliminarColaborador.setSelected(false);
        this.chkEliminarPuesto.setSelected(false);
        this.chkEnCurso.setSelected(false);
        this.chkListaActivos.setSelected(false);
        this.chkListaGeneral.setSelected(false);
        this.chkListaPasivos.setSelected(false);
        this.chkModificarColaborador.setSelected(false);
        this.chkModificarPuesto.setSelected(false);
        this.chkModificarUsuario.setSelected(false);
        this.chkNuevoColaborador.setSelected(false);
        this.chkNuevoPermiso.setSelected(false);
        this.chkNuevoPuesto.setSelected(false);
    }
    
    public void eliminarUsuario(String cedula){
        PermisosD oD = new PermisosD(conexion);
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de borrar el registro?", "Borrar", JOptionPane.YES_NO_OPTION); // Muestra un mensaje de alerta
        if ( respuesta == JOptionPane.YES_OPTION ) 
        {
            this.conexion.IniciarTransacciones();
            oD.borrarUsuario(cedula);
            if (!oD.isError())
            {
                this.sonido.play();
                this.conexion.setCommit();
                JOptionPane.showMessageDialog(rootPane, "Registro eliminado satisfactoriamente");
                this.cargarComboxUsuariosRegistrados();
            }
            else
            {
                this.sonidoError.play();
                this.conexion.setRollBack();
                JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
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

        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbxColaborador = new javax.swing.JComboBox();
        cpContrasenna = new javax.swing.JPasswordField();
        cpContrasenna2 = new javax.swing.JPasswordField();
        btnAgregarNuevoUsuario = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cpUser = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cbxColaborador2 = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        chkEnCurso = new javax.swing.JCheckBox();
        chkAsignarNuevoNombramiento = new javax.swing.JCheckBox();
        chkListaGeneral = new javax.swing.JCheckBox();
        chkListaActivos = new javax.swing.JCheckBox();
        chkListaPasivos = new javax.swing.JCheckBox();
        chkNuevoPuesto = new javax.swing.JCheckBox();
        chkModificarPuesto = new javax.swing.JCheckBox();
        chkEliminarPuesto = new javax.swing.JCheckBox();
        chkNuevoColaborador = new javax.swing.JCheckBox();
        chkModificarColaborador = new javax.swing.JCheckBox();
        chkEliminarColaborador = new javax.swing.JCheckBox();
        chkNuevoPermiso = new javax.swing.JCheckBox();
        chkAsignarPermisos = new javax.swing.JCheckBox();
        chkModificarUsuario = new javax.swing.JCheckBox();
        chkEliminarUsuario = new javax.swing.JCheckBox();
        btnAsignarPermisos = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JTable();
        btnModificarUsuario = new javax.swing.JButton();
        btnEliminarUsuario = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nuevo usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel1.setText("Colaborador:");

        jLabel2.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel2.setText("Contraseña:");

        jLabel3.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel3.setText("Repetir Contraseña:");

        cbxColaborador.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cbxColaborador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cbxColaborador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxColaboradorActionPerformed(evt);
            }
        });

        cpContrasenna.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cpContrasenna.setNextFocusableComponent(cpContrasenna2);

        cpContrasenna2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cpContrasenna2.setNextFocusableComponent(btnAgregarNuevoUsuario);

        btnAgregarNuevoUsuario.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        btnAgregarNuevoUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-aceptar2.png"))); // NOI18N
        btnAgregarNuevoUsuario.setText("Agregar");
        btnAgregarNuevoUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarNuevoUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarNuevoUsuarioActionPerformed(evt);
            }
        });
        btnAgregarNuevoUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgregarNuevoUsuarioKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel9.setText("Usuario:");

        cpUser.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cpUser.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cpContrasenna, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                    .addComponent(cpContrasenna2)
                    .addComponent(cpUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAgregarNuevoUsuario)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAgregarNuevoUsuario)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(cpUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(cpContrasenna, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cpContrasenna2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel4.setText("Usuarios del sistema:");

        cbxColaborador2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cbxColaborador2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cbxColaborador2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxColaborador2ActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("NOMBRAMIENTOS");

        jLabel6.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("PUESTO");

        jLabel7.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("COLABORADORES");

        jLabel8.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("PERMISOS");

        jSeparator1.setForeground(new java.awt.Color(0, 102, 153));

        chkEnCurso.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkEnCurso.setText("En curso");
        chkEnCurso.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkAsignarNuevoNombramiento.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkAsignarNuevoNombramiento.setText("Asignar nuevo");
        chkAsignarNuevoNombramiento.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkListaGeneral.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkListaGeneral.setText("Lista general");
        chkListaGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkListaActivos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkListaActivos.setText("Lista activos");
        chkListaActivos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkListaPasivos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkListaPasivos.setText("Lista pasivos");
        chkListaPasivos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkNuevoPuesto.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkNuevoPuesto.setText("Asignar nuevo");
        chkNuevoPuesto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chkNuevoPuesto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        chkModificarPuesto.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkModificarPuesto.setText("Modificar");
        chkModificarPuesto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkEliminarPuesto.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkEliminarPuesto.setText("Eliminar");
        chkEliminarPuesto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkNuevoColaborador.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkNuevoColaborador.setText("Asignar nuevo");
        chkNuevoColaborador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkModificarColaborador.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkModificarColaborador.setText("Modificar");
        chkModificarColaborador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkEliminarColaborador.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkEliminarColaborador.setText("Eliminar");
        chkEliminarColaborador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkNuevoPermiso.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkNuevoPermiso.setText("Nuevo usuario");
        chkNuevoPermiso.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkAsignarPermisos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkAsignarPermisos.setText("Asignar permiso");
        chkAsignarPermisos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkModificarUsuario.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkModificarUsuario.setText("Modificar usuario");
        chkModificarUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkEliminarUsuario.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        chkEliminarUsuario.setText("Eliminar");
        chkEliminarUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(chkEnCurso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chkAsignarNuevoNombramiento, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(chkListaGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chkListaActivos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chkListaPasivos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(89, 89, 89)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(chkModificarPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkNuevoPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkEliminarPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(60, 60, 60)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(chkNuevoColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(chkModificarColaborador, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(chkEliminarColaborador, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkModificarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkAsignarPermisos, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkNuevoPermiso, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkEliminarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)))
                        .addGap(10, 10, 10))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkEnCurso)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkNuevoColaborador)
                        .addComponent(chkNuevoPermiso)
                        .addComponent(chkNuevoPuesto)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(chkAsignarPermisos)
                        .addGap(18, 18, 18)
                        .addComponent(chkModificarUsuario)
                        .addGap(18, 18, 18)
                        .addComponent(chkEliminarUsuario))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkAsignarNuevoNombramiento)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(chkModificarColaborador)
                                .addGap(18, 18, 18)
                                .addComponent(chkEliminarColaborador))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(chkModificarPuesto)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(chkEliminarPuesto)
                                    .addComponent(chkListaGeneral))))
                        .addGap(18, 18, 18)
                        .addComponent(chkListaActivos)))
                .addGap(18, 18, 18)
                .addComponent(chkListaPasivos)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        btnAsignarPermisos.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        btnAsignarPermisos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-aceptar2.png"))); // NOI18N
        btnAsignarPermisos.setText("Asignar permisos");
        btnAsignarPermisos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAsignarPermisos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsignarPermisosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxColaborador2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAsignarPermisos)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(cbxColaborador2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAsignarPermisos))
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Asignar permisos de usuario", jPanel3);

        panel.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(panel);

        btnModificarUsuario.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        btnModificarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-modificar.png"))); // NOI18N
        btnModificarUsuario.setText("Modificar");
        btnModificarUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarUsuarioActionPerformed(evt);
            }
        });

        btnEliminarUsuario.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        btnEliminarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-eliminar.png"))); // NOI18N
        btnEliminarUsuario.setText("Eliminar");
        btnEliminarUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarUsuarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addComponent(btnModificarUsuario)
                .addGap(64, 64, 64)
                .addComponent(btnEliminarUsuario)
                .addContainerGap(297, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificarUsuario)
                    .addComponent(btnEliminarUsuario))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Mantenimiento usuarios", jPanel2);

        jMenuBar1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuBar1.setPreferredSize(new java.awt.Dimension(56, 35));

        jMenu1.setText("Archivo");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/menu.png"))); // NOI18N
        jMenuItem1.setText("Ir al panel principal");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        jMenuItem2.setText("Información del desarrollador");
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarNuevoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarNuevoUsuarioActionPerformed
        // TODO add your handling code here:
        int fila = this.cbxColaborador.getSelectedIndex();
        if(fila < 0 || 
            this.cpContrasenna.getText().equals("") ||
            this.cpContrasenna2.getText().equals("")){
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Algunos campos se encuentran vacíos");
        }else{
            if(this.cpContrasenna.getText().equals(this.cpContrasenna2.getText())){
                this.guardarUsuario();
            }else{
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Las contraseñas deben ser iguales");
                this.cpContrasenna2.setText("");
            }
            
        }
    }//GEN-LAST:event_btnAgregarNuevoUsuarioActionPerformed

    private void cbxColaborador2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxColaborador2ActionPerformed
        // TODO add your handling code here:
        int fila = this.cbxColaborador2.getSelectedIndex();
        if(fila > -1){
            Usuario oU = (Usuario)this.pUsuarios.get(fila);
            for(int i = 0; i<this.pPermisos.size(); i++){
                PermisoUsuario oP = (PermisoUsuario)this.pPermisos.get(i);
                if(oP.getoUsuario().getoPersona().getCedula().equals(oU.getoPersona().getCedula())){
                    this.setPermisos(oP);
                    break;
                }
            }
        }
    }//GEN-LAST:event_cbxColaborador2ActionPerformed

    private void btnAsignarPermisosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsignarPermisosActionPerformed
        // TODO add your handling code here:
        int fila = this.cbxColaborador2.getSelectedIndex();
        if(fila > -1){
            Usuario oU = this.getUsuarioPermiso();
            this.guardarPermisos(oU.getoPersona().getCedula());
        }
    }//GEN-LAST:event_btnAsignarPermisosActionPerformed

    private void btnModificarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarUsuarioActionPerformed
        // TODO add your handling code here:
        int fila = this.panel.getSelectedRow();
        if(fila > -1)
        {
            this.isModificarUsuario = true;
            Usuario oU = (Usuario)this.pUsuarios.get(fila);
            this.cedulaEditar = oU.getoPersona().getCedula();
            this.setUsuario(oU);
            this.cpUser.setRequestFocusEnabled(true);
        }
        else
        {
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Debe seleccionar algún registro del panel");
        }
    }//GEN-LAST:event_btnModificarUsuarioActionPerformed

    private void btnEliminarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarUsuarioActionPerformed
        // TODO add your handling code here:
        int fila = this.panel.getSelectedRow();
        if(fila > -1)
        {
            Usuario oU = (Usuario)this.pUsuarios.get(fila);
            if(!oU.getoPersona().getCedula().equals(this.oPermiso.getoUsuario().getoPersona().getCedula())){
                this.eliminarUsuario(oU.getoPersona().getCedula());
            }else{
                JOptionPane.showMessageDialog(rootPane, "No es posible eliminar un usuario en sesión.");
            }
            
        }
        else
        {
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Debe seleccionar algún registro del panel");
        }
    }//GEN-LAST:event_btnEliminarUsuarioActionPerformed

    private void btnAgregarNuevoUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgregarNuevoUsuarioKeyPressed
        // TODO add your handling code here:
        if(evt.getExtendedKeyCode() == evt.VK_ENTER)
        {
            int fila = this.cbxColaborador.getSelectedIndex();
            if(fila < 0 || 
                this.cpContrasenna.getText().equals("") ||
                this.cpContrasenna2.getText().equals("")){
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Algunos campos se encuentran vacíos");
            }else{
                if(this.cpContrasenna.getText().equals(this.cpContrasenna2.getText())){
                    this.guardarUsuario();
                }else{
                    this.sonidoError.play();
                    JOptionPane.showMessageDialog(rootPane, "Las contraseñas deben ser iguales");
                    this.cpContrasenna2.setText("");
                }

            }
        }
    }//GEN-LAST:event_btnAgregarNuevoUsuarioKeyPressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        FrmContacto oC = new FrmContacto(null, true);
        oC.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void cbxColaboradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxColaboradorActionPerformed
        // TODO add your handling code here:
        
        if(isPermitido){
            if(this.cbxColaborador.getSelectedIndex() > -1){
                Personal oR = (Personal)this.pColaboradores.get(this.cbxColaborador.getSelectedIndex());
                this.cpUser.setText(this.obtenerNombraUsuario(oR));
            }
        }
    }//GEN-LAST:event_cbxColaboradorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarNuevoUsuario;
    private javax.swing.JButton btnAsignarPermisos;
    private javax.swing.JButton btnEliminarUsuario;
    private javax.swing.JButton btnModificarUsuario;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbxColaborador;
    private javax.swing.JComboBox cbxColaborador2;
    private javax.swing.JCheckBox chkAsignarNuevoNombramiento;
    private javax.swing.JCheckBox chkAsignarPermisos;
    private javax.swing.JCheckBox chkEliminarColaborador;
    private javax.swing.JCheckBox chkEliminarPuesto;
    private javax.swing.JCheckBox chkEliminarUsuario;
    private javax.swing.JCheckBox chkEnCurso;
    private javax.swing.JCheckBox chkListaActivos;
    private javax.swing.JCheckBox chkListaGeneral;
    private javax.swing.JCheckBox chkListaPasivos;
    private javax.swing.JCheckBox chkModificarColaborador;
    private javax.swing.JCheckBox chkModificarPuesto;
    private javax.swing.JCheckBox chkModificarUsuario;
    private javax.swing.JCheckBox chkNuevoColaborador;
    private javax.swing.JCheckBox chkNuevoPermiso;
    private javax.swing.JCheckBox chkNuevoPuesto;
    private javax.swing.JPasswordField cpContrasenna;
    private javax.swing.JPasswordField cpContrasenna2;
    private javax.swing.JTextField cpUser;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable panel;
    // End of variables declaration//GEN-END:variables
}
