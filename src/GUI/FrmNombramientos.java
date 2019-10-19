/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Base_Datos.ConexionBaseDatos;
import DataSurceReport.ReporteListaGeneral;
import DataSurceReport.ReporteListaNombramientoGeneral;
import Datos.NombramientoD;
import Datos.PersonaD;
import Datos.PuestoD;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import logica.Nombramiento;
import logica.PermisoUsuario;
import logica.Puesto;
import logica.Personal;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JRViewer;

/**
 *
 * @author Fabricio
 */
public class FrmNombramientos extends javax.swing.JDialog {
    ConexionBaseDatos conexion;
    private AudioClip sonido;        // Para agregar sonido a un mensaje
    private AudioClip sonidoError;   // Para agregar sonido a un mensaje
    ArrayList pNombramientos;    // Lista de objetos Nombramientos Actuales
    ArrayList pListaActivos;     // Lista de objetos Elegibles Activos -> Último nombramiento menor a 6 meses o 180 días
    ArrayList pListaPasivos;     // Lista de objetos Elegibles Activos -> Último nombramiento mayor a 6 meses , 180 días o no haber sido nombrado
    ArrayList pListaPersonasSin;   // Lista e objetos Elegibles general
    ArrayList pPuestos;          // Lista de objetos Puesto -> Para cargar los combo box y realizar los filtros de búsqueda
    Personal candidado;          // El candidato a nombrar según seleccionado de la pestaña: Asignar nuevo nombramiento
    Personal candidato_suplido;  // El candidato a sustituir según la selección de la pestaña: Asignar nuevo nombramiento
    Puesto puesto;               // El puesto al cual aplica el candidato según la selección del usuario en la pestaña: Asignar nuevo nombramiento
    Date fechaActual;            // Servirá para capturar la fecha actual, compara y poder aplicar el filtro de Activos y Pasivos
    boolean isPermitido;         // Para que no se aplique el filtro de los combox a la hora de cargalo de datos, hasta que sea true se realizará el filtro de información
    private boolean isModificar;
    private String[] codigoModificar = new String[2];
    private boolean isError;     // Badera de error que servirá en el método GuardarNombramiento(){}
    private String boletaModificar;
    private String cedulaModificar;
    private String msjError;     // Badera de error que servirá en el método GuardarNombramiento(){}
    private int diasAnterior;
    private int cantidadLista;
    final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día, servirá para calcular los días entre dos fechas
    private PermisoUsuario oPermiso;
    
    /*Panel lista de nombramientos*/
    private DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    
    private String[] cabecera = {"Nombramiento", "Colaborador", "Puesto", "Días nombrado", "Ubicación", "Suple A"};
    
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
    
    private String[][] nuevosdatos = new String[0][6];
    private void redimensionarMatrizNuevosDatos() {
        String[][] respaldo = this.nuevosdatos;   // Respalda los datos de la matriz principal en una auxiliar

        this.nuevosdatos = new String[respaldo.length + 1][6]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.nuevosdatos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    
    /*Panel lista de elegibles*/
    private DefaultTableModel modeloPersonasSin = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
     
    private String[] cabeceraPersonasSin = {"Colaborador", "Estado", "Puesto"};
    
    private String[][] datosPersonasSin = new String[0][3];
    
    private void redimensionarMatrizPersonasSin() {
        String[][] respaldo = this.datosPersonasSin;   // Respalda los datos de la matriz principal en una auxiliar

        this.datosPersonasSin = new String[respaldo.length + 1][3]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.datosPersonasSin[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    
    
    /*Panel lista de elegibles activos*/
    private DefaultTableModel modeloElegiblesActivos = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private String[] cabeceraElegiblesActivos = {"Colaborador", "Fecha del nombramiento", "Cantidad de días", "Acumulado"};
    
    private String[][] datosElegiblesActivos = new String[0][4];
    
    private void redimensionarMatrizElegiblesActivos() {
        String[][] respaldo = this.datosElegiblesActivos;   // Respalda los datos de la matriz principal en una auxiliar

        this.datosElegiblesActivos = new String[respaldo.length + 1][4]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.datosElegiblesActivos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    
    
    /*Panel lista de elegibles pasivos*/
    private DefaultTableModel modeloElegiblesPasivos = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private String[] cabeceraElegiblesPasivos = {"Colaborador", "Fecha del nombramiento", "Cantidad de días", "Acumulado"};
    
    private String[][] datosElegiblesPasivos = new String[0][4];
    
    private void redimensionarMatrizElegiblesPasivos() {
        String[][] respaldo = this.datosElegiblesPasivos;   // Respalda los datos de la matriz principal en una auxiliar

        this.datosElegiblesPasivos = new String[respaldo.length + 1][4]; // Inicializa la matriz sumandole un fila adicional, con la cantidad de columnas asginadas por parámetros
        
        // Permite redimensionar la matrix con los datos de la matrix auxiliar más la columna nueva sin datos
        for (int i = 0; i < respaldo.length; i++) {
            for (int j = 0; j < respaldo[0].length; j++) {
                this.datosElegiblesPasivos[i][j] = respaldo[i][j];  // Vuelve agregar los datos de la matrix auxiliar para no perder ningúna datos
            }
        }
    }
    
    /**
     * Creates new form FrmNombramientos2
     */
    public FrmNombramientos(java.awt.Frame parent, boolean modal, ConexionBaseDatos pConexion, PermisoUsuario pPermiso) {
        super(parent, modal);
        initComponents();
        this.conexion = pConexion;
        pNombramientos = new ArrayList();
        pListaActivos = new ArrayList();
        pListaPasivos = new ArrayList();
        pListaPersonasSin = new ArrayList();
        pPuestos = new ArrayList();
        candidado = null;
        candidato_suplido = null;
        fechaActual = new Date();
        this.isError = false;
        this.isPermitido = false;
        this.boletaModificar = "";
        this.cedulaModificar = "";
        this.msjError = "";
        this.diasAnterior = 0;
        cantidadLista = 0; // importante
        sonido = java.applet.Applet.newAudioClip(getClass().getResource("/Media/error.wav")); // Sonido cuando el usuario y contraseña son correctos
        sonidoError = java.applet.Applet.newAudioClip(getClass().getResource("/Media/ok.wav"));    // Sonido cuando usuario y/o contraseña incorrectos
        this.oPermiso = pPermiso;
        this.asignarPermisos();
        this.cargarDatos();
        this.cargarComboPuestos();
        this.cargarDatosPersonasSin();
        this.setTitle("Hospital San Carlos | Control de nombramientos");
        this.setLocationRelativeTo(null);
    }
    
    public void limpiarErrores(){
        this.isError = false;
        this.msjError = "";
    }
    
    public void asignarPermisos(){
        if(!this.oPermiso.isAsignar_nuevo_nombramiento()){
            this.vistaGeneral.remove(1);
        }else if(!this.oPermiso.isLista_activos()){
            this.vistaGeneral.remove(2);
        }else if(!this.oPermiso.isLista_general()){
            this.vistaGeneral.remove(4);
        }else if(!this.oPermiso.isLista_pasivos()){
            this.vistaGeneral.remove(3);
        }else if(!this.oPermiso.isNombramiento_en_curso()){
            this.vistaGeneral.remove(0);
        }
    }
    
    /**
     * Limpia los campos de nuevo nombramiento
     */
    public void limpiarDatosNombramiento(){
        this.CpSuple.setText("");
        this.cpCodigoNombramiento.setText("");
        this.cpColaborador.setText("");
        this.cpFechaFinalizacion.setDate(null);
        this.cpFechaInicio.setDate(null);
        this.cpPuesto.setText("");
        this.cpPlaza.setText("");
        this.cpUbicacion.setText("");
        this.isModificar = false;
        this.boletaModificar = "";
        this.cedulaModificar = "";
        this.diasAnterior = 0;
    }
    
    /**
     * Carga los combos de objetos Puesto
     * Servirá para realizar el filtro de información por puesto
     */
    public void cargarComboPuestos(){
        PuestoD oP = new PuestoD(conexion);
        // Si ya se cargó el combo previamente elimina todos los datos y los vuelve a cargar
        if (this.pPuestos.size() > 0)
        {
            this.cbxPuestosActivos.removeAllItems();
            this.cbxPuestosPasivos.removeAllItems();
            this.cbxPuestoPersonasSin.removeAllItems();
        }
        this.pPuestos = oP.obtenerPuesto(); // Se llena la lista de objetos Puesto
        
        if (!oP.isError())
        {
            if (this.pPuestos.size() > 0)
            {
                for (int i = 0; i<this.pPuestos.size(); i++)
                {
                    Puesto aux = (Puesto)this.pPuestos.get(i);
                    this.cbxPuestosActivos.addItem(aux.getNombre_puesto());
                    this.cbxPuestosPasivos.addItem(aux.getNombre_puesto());
                    this.cbxPuestoPersonasSin.addItem(aux.getNombre_puesto());
                }
                this.cbxPuestosActivos.setSelectedIndex(-1);
                this.cbxPuestosPasivos.setSelectedIndex(-1);
                this.cbxPuestoPersonasSin.setSelectedIndex(-1);
                this.isPermitido = true; // Con este true ya se permite realizar el filtro una vez seleccionado algo en el combo box
            }
        }
        else
        {
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oP.getErrorMsg());
        }
    }
    
    /**
     * Carga todos los nombramientos
     * Si están vigentes en cuanto a fecha
     * los muestra en el panel
     */
    public void cargarDatos(){
        this.limpiarErrores();
        JTableHeader th;
        TableColumn columna;
        ArrayList pDatos = new ArrayList();
        this.datos = new String[0][6];
        NombramientoD oD = new NombramientoD(conexion);
        pDatos = oD.obtenerNombramientos();
        if(!oD.isError()){
            this.cantidadLista = pDatos.size();
            if(this.filtrarNombramientos(pDatos)){
                for(int i = 0; i<this.pNombramientos.size(); i++){
                    Nombramiento aux = (Nombramiento)this.pNombramientos.get(i);
                    this.redimensionarMatriz();
                    this.datos[this.datos.length - 1][0] = aux.getBoleta_nombramiento();
                    this.datos[this.datos.length - 1][1] = aux.getCandidato().toString();
                    this.datos[this.datos.length - 1][2] = aux.getPuesto().getNombre_puesto();
                    this.datos[this.datos.length - 1][3] = String.valueOf(aux.getFecha_nombramiento());
                    this.datos[this.datos.length - 1][4] = String.valueOf(aux.getFecha_finalizacion_nombramiento());
                    this.datos[this.datos.length - 1][5] = aux.getCandidato_suplido().toString();
                }
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }
            columna=panel.getColumnModel().getColumn(0);
            columna.setMinWidth(50);
            columna.setWidth(50);

            columna=panel.getColumnModel().getColumn(1);
            columna.setMinWidth(160);
            columna.setWidth(160);

            columna=panel.getColumnModel().getColumn(2);
            columna.setMinWidth(50);
            columna.setWidth(50);

            columna=panel.getColumnModel().getColumn(3);
            columna.setMinWidth(50);
            columna.setWidth(50);

            columna=panel.getColumnModel().getColumn(4);
            columna.setMinWidth(30);
            columna.setWidth(30);

            columna=panel.getColumnModel().getColumn(5);
            columna.setMinWidth(160);
            columna.setWidth(160);

            th = panel.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    /**
     * Los asigna como Activos o Pasivos
     * @param pDatos
     * @return 
     */
    public boolean filtrarNombramientos(ArrayList pDatos){
        JTableHeader th;
        TableColumn columna;
        boolean isCorrecto = false;
        Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
        int dias = 0;
        Date fechaActual = new Date();
        this.pNombramientos = new ArrayList();
        this.pListaActivos = new ArrayList();
        this.pListaPasivos = new ArrayList();
        if(pDatos.size() > 0){
            isCorrecto = true;
            for(int i = 0; i<pDatos.size(); i++){
                Nombramiento aux = (Nombramiento)pDatos.get(i);
                dias = this.calcularDiasNombramiento(aux.getFecha_finalizacion_nombramiento(), fechaActual);
                if(dias <= 180){
                    this.pNombramientos.add(aux);
                    this.pListaActivos.add(aux);
                }else if(dias > 180 || dias == 0){
                    this.pListaPasivos.add(aux);
                }
            }
        }else{
            this.modelo.setDataVector(null, cabecera);
            this.panel.setModel(modelo);
            th = panel.getTableHeader(); 
            th.setFont(fuente);
            th.setForeground(Color.black);
            
            this.modeloElegiblesActivos.setDataVector(null, cabeceraElegiblesActivos);
            this.panelActivos.setModel(modeloElegiblesActivos);
            th = panelActivos.getTableHeader(); 
            th.setFont(fuente);
            th.setForeground(Color.black);
            
            this.modeloElegiblesPasivos.setDataVector(null, cabeceraElegiblesPasivos);
            this.panelPasivos.setModel(modeloElegiblesPasivos);
            th = panelPasivos.getTableHeader(); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }
        return isCorrecto;
    }
    
    public void cargarDatosPersonasSin(){
        String estado = "";
        JTableHeader th;
        TableColumn columna;
        PersonaD oD = new PersonaD(conexion);
        this.pListaPersonasSin = oD.obtenerPersonalSinNombramiento();
        if(!oD.isError()){
            if(this.pListaPersonasSin.size() > 0){
                for (int i = 0; i < this.pListaPersonasSin.size(); i++) {
                    Personal aux = (Personal)this.pListaPersonasSin.get(i);
                    this.redimensionarMatrizPersonasSin();
                    this.datosPersonasSin[this.datosPersonasSin.length - 1][0] = aux.toString();
                    if(aux.isConPropiedad()){
                        estado = "En propiedad";
                    }else{
                        estado = "Interino";
                    }
                    this.datosPersonasSin[this.datosPersonasSin.length - 1][1] = estado;
                    this.datosPersonasSin[this.datosPersonasSin.length - 1][2] = aux.getCod_puesto().getNombre_puesto();
                }
                this.modeloPersonasSin.setDataVector(datosPersonasSin, cabeceraPersonasSin);
                this.panelPersonaSinNombramientos.setModel(modeloPersonasSin);
            }else{
                this.modeloPersonasSin.setDataVector(null, cabeceraPersonasSin);
                this.panelPersonaSinNombramientos.setModel(modeloPersonasSin);
            }
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th = panelPersonaSinNombramientos.getTableHeader(); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }else{
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    public void seleccionComboBoxActivos(String nombrePuesto){
        JTableHeader th;
        TableColumn columna;
        int dias = 0;
        int cantidad = 0;
        this.datosElegiblesActivos = new String[0][4];
        if(this.pListaActivos.size() > 0){
            for (int i = 0; i < this.pListaActivos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pListaActivos.get(i);
                if(aux.getPuesto().getNombre_puesto().equals(nombrePuesto)){
                    cantidad++;
                    this.redimensionarMatrizElegiblesActivos();
                    this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][0] = aux.getCandidato().toString();
                    this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][1] = String.valueOf(aux.getFecha_nombramiento()) + " al " + String.valueOf(aux.getFecha_finalizacion_nombramiento());
                    dias = this.calcularDiasNombramiento(aux.getFecha_nombramiento(), aux.getFecha_finalizacion_nombramiento());
                    this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][2] = String.valueOf(dias);
                    this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][3] = String.valueOf(aux.getCandidato().getVeces_nombrado());
                }
            }
            if(cantidad > 0){
                this.modeloElegiblesActivos.setDataVector(datosElegiblesActivos, cabeceraElegiblesActivos);
                this.panelActivos.setModel(modeloElegiblesActivos);
            }else{
                this.modeloElegiblesActivos.setDataVector(null, cabeceraElegiblesActivos);
                this.panelActivos.setModel(modeloElegiblesActivos);
            }
            th = panelActivos.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }
    }
    
    public void seleccionComboBoxPasivos(String nombrePuesto){
        JTableHeader th;
        TableColumn columna;
        int dias = 0;
        int cantidad = 0;
        this.datosElegiblesPasivos = new String[0][4];
        
        if(this.pListaPasivos.size() > 0){
            for (int i = 0; i < this.pListaPasivos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pListaPasivos.get(i);
                if(aux.getPuesto().getNombre_puesto().equals(nombrePuesto)){
                    cantidad++;
                    this.redimensionarMatrizElegiblesPasivos();
                    this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][0] = aux.getCandidato().toString();
                    this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][1] = String.valueOf(aux.getFecha_nombramiento()) + " al " + String.valueOf(aux.getFecha_finalizacion_nombramiento());
                    dias = this.calcularDiasNombramiento(aux.getFecha_nombramiento(), aux.getFecha_finalizacion_nombramiento());
                    this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][2] = String.valueOf(dias);
                    this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][3] = String.valueOf(aux.getCandidato().getVeces_nombrado());
                }
            }
            if(cantidad > 0){
                this.modeloElegiblesPasivos.setDataVector(datosElegiblesPasivos, cabeceraElegiblesPasivos);
                this.panelPasivos.setModel(modeloElegiblesPasivos);
            }else{
                this.modeloElegiblesPasivos.setDataVector(null, cabeceraElegiblesPasivos);
                this.panelPasivos.setModel(modeloElegiblesPasivos);
            }
            th = panelPasivos.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }
    }
    
    public void seleccionComboBoxPersonasSin(String nombrePuesto){
        JTableHeader th;
        TableColumn columna;
        String estado = "";
        int cantidad = 0;
        this.datosPersonasSin = new String[0][3];
        if(this.pListaPersonasSin.size() > 0){
            for (int i = 0; i < this.pListaPersonasSin.size(); i++) {
                Personal aux = (Personal)this.pListaPersonasSin.get(i);
                if(aux.getCod_puesto().getNombre_puesto().equals(nombrePuesto)){
                    cantidad++;
                    this.redimensionarMatrizPersonasSin();
                    this.datosPersonasSin[this.datosPersonasSin.length - 1][0] = aux.toString();
                    if(aux.isConPropiedad()){
                        estado = "En propiedad";
                    }else{
                        estado = "Interino";
                    }
                    this.datosPersonasSin[this.datosPersonasSin.length - 1][1] = estado;
                    this.datosPersonasSin[this.datosPersonasSin.length - 1][2] = aux.getCod_puesto().getNombre_puesto();
                }
            }
            if(cantidad > 0){
                this.modeloPersonasSin.setDataVector(datosPersonasSin, cabeceraPersonasSin);
                this.panelPersonaSinNombramientos.setModel(modeloPersonasSin);
            }else{
                this.modeloPersonasSin.setDataVector(null, cabeceraPersonasSin);
                this.panelPersonaSinNombramientos.setModel(modeloPersonasSin);
            }
            th = panelPersonaSinNombramientos.getTableHeader(); 
            Font fuente = new Font("Cambria Math", Font.BOLD, 15); 
            th.setFont(fuente);
            th.setForeground(Color.black);
        }
    }
    
    public void buscarPersonal(){
        FrmBuscarPersonal oBP = new FrmBuscarPersonal(null, true, conexion);
        oBP.setVisible(true);
        if(oBP.isAceptar()){
            this.candidado = oBP.getPersonal();
            this.cpColaborador.setText(this.candidado.toString());
        }
    }
    
    public void buscarPersonalSuple(){
        FrmBuscarPersonal oBP = new FrmBuscarPersonal(null, true, conexion);
        oBP.setVisible(true);
        if(oBP.isAceptar()){
            this.candidato_suplido = oBP.getPersonal();
            this.CpSuple.setText(this.candidato_suplido.toString());
        }
    }
    
    public void buscarPuesto(){
        FrmBuscarPuesto oBP = new FrmBuscarPuesto(null, true, conexion);
        oBP.setVisible(true);
        if(oBP.isAceptar()){
            this.puesto = oBP.getPuesto();
            this.cpPuesto.setText(this.puesto.getNombre_puesto());
        }
    }
    
    /** Métodos para agregar un nuevo nombramiento **/
    
    public void okButton(){
        String persona1 = "";
        String persona2 = "";
        try{
            persona1 = String.valueOf(this.candidado.getNombre());
            persona2 = String.valueOf(this.candidato_suplido.getNombre());
        }catch(Exception e){
            persona1 = "null";
            persona2 = "null";
        }
        String puest = String.valueOf(this.puesto.getNombre_puesto());
        String fecha1 = String.valueOf(this.cpFechaInicio.getDate());
        String fecha2 = String.valueOf(this.cpFechaFinalizacion.getDate());
        if(this.cpCodigoNombramiento.getText().equals("") ||
            persona1.equals("null") ||
            persona2.equals("null") ||
            puest.equals("") ||
            fecha1.equals("null") ||
            fecha2.equals("null") ||
            this.cpUbicacion.getText().equals("") ||
            this.cpPlaza.getText().equals("")){
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Algunos campos se encuentran vacíos");
        }else{
            if(this.cpColaborador.getText().equals(this.CpSuple.getText())){
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "El campo (Suple a:) debe de ser distinto al campo (Colaborador:)");
                this.CpSuple.setText("");
                this.candidato_suplido = null;
            }else{
                if(areTheSameDates(this.cpFechaInicio.getDate(), this.cpFechaFinalizacion.getDate())){
                    this.sonidoError.play();
                    JOptionPane.showMessageDialog(rootPane, "No se permite el registro de dos fecha iguales");
                    this.cpFechaFinalizacion.setDate(null);
                }else{
                    if(this.cpFechaFinalizacion.getDate().before(this.cpFechaInicio.getDate())){
                        this.sonidoError.play();
                        JOptionPane.showMessageDialog(rootPane, "El registro del campo (Fecha de finalización:) es incorrecto");
                        this.cpFechaFinalizacion.setDate(null);
                    }else{
                        this.guardarNombramiento();
                    }
                }
            }
        }
    }
    
    /**
     * Obtiene un objeto de tipo nombramiento
     * @return 
     */
    public Nombramiento getNombramiento(){
        Nombramiento aux = new Nombramiento(this.cpPlaza.getText().trim(), 
                this.candidado, 
                this.puesto, 
                this.cpFechaInicio.getDate(), 
                this.cpFechaFinalizacion.getDate(), 
                this.cpUbicacion.getText().trim(), 
                this.candidato_suplido, 
                this.cpCodigoNombramiento.getText().trim());
        return aux;
    }
    
    public void setNombramiento(Nombramiento oNombramiento){
        this.cpCodigoNombramiento.setText(oNombramiento.getBoleta_nombramiento());
        this.cpColaborador.setText(oNombramiento.getCandidato().toString());
        this.candidado = oNombramiento.getCandidato();
        this.CpSuple.setText(oNombramiento.getCandidato_suplido().toString());
        this.candidato_suplido = oNombramiento.getCandidato_suplido();
        this.puesto = oNombramiento.getPuesto();
        this.cpPuesto.setText(oNombramiento.getPuesto().getNombre_puesto());
        this.cpFechaInicio.setDate(oNombramiento.getFecha_nombramiento());
        this.cpFechaFinalizacion.setDate(oNombramiento.getFecha_finalizacion_nombramiento());
        this.cpUbicacion.setText(oNombramiento.getUbicacion());
        this.cpPlaza.setText(oNombramiento.getNumero_plaza());
    }
    
    /**
     * Guarda los datos del nuevo nombramieto en la base de datos
     */
    public void guardarNombramiento(){
        String mensaje = "";
        NombramientoD oD = new NombramientoD(conexion);
        PersonaD oPersona = new PersonaD(conexion);
        this.conexion.IniciarTransacciones();
        if (isModificar)
        {
            
            int diaNuevo = this.calcularDiasNombramiento(this.getNombramiento().getFecha_nombramiento(), this.getNombramiento().getFecha_finalizacion_nombramiento());
            int dias = diaNuevo - this.diasAnterior;
            
            String[] actualizar = {boletaModificar, cedulaModificar};
            oD.actualizarNombramiento(this.getNombramiento(), actualizar);
            oPersona.actualizarPersonaEnNombramiento(
                this.getNombramiento().getCandidato(), 
                cedulaModificar, 
                dias
            );
            mensaje = "modificado";
        }
        else
        {
            oD.agregarNombramiento(this.getNombramiento());
            int dias = this.calcularDiasNombramiento(this.getNombramiento().getFecha_nombramiento(), this.getNombramiento().getFecha_finalizacion_nombramiento());
            oPersona.agregarDíasNombramiento(this.getNombramiento().getCandidato().getCedula(), dias);
            mensaje = "agregado";
        }
        
        if (!oD.isError())
        {
            // Se guardan los dias de nombramiento en la base de datos
            if(!isError){
                this.sonido.play();
                this.conexion.setCommit();
                JOptionPane.showMessageDialog(rootPane, "Registro " + mensaje + " satisfactoriamente");
                this.limpiarDatosNombramiento();
                this.cargarDatos();
            }
            else
            {
                this.sonidoError.play();
                this.conexion.setRollBack();
                JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: ");
            }
        }
        else
        {
            this.sonidoError.play();
            this.conexion.setRollBack();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
        }
    }
    
    public void eliminarNombramiento(String codigoEliminar, String cedulaEliminar){
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de borrar el registro?", "Borrar", JOptionPane.YES_NO_OPTION); // Muestra un mensaje de alerta
        if ( respuesta == JOptionPane.YES_OPTION ) {
            NombramientoD oD = new NombramientoD(conexion);
            String[] resultado = {codigoEliminar, cedulaEliminar};
            oD.borrarNombramiento(resultado);
            if (!oD.isError()) 
            {
                JOptionPane.showMessageDialog(rootPane, "Registro eliminado satisfactoriamente");
                this.cargarDatos();
            }
            else
            {
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oD.getErrorMsg());
            }
        }
    }
    
    public int calcularDiasNombramiento(Date fechaA, Date fechaB){
        int resultado = 0;
        int diaFinal = 0;
        int mesFinal = 0;
        int annioFinal = 0;
        
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); 
        String fecha = formato.format(fechaA); 
        String[] fecha1 = fecha.split("/"); 
        Integer dia1 = Integer.parseInt(fecha1[0]); 
        Integer mes1 = Integer.parseInt(fecha1[1]); 
        Integer anio1 = Integer.parseInt(fecha1[2]);
        
        String fecha2 = formato.format(fechaB); 
        String[] fecha3 = fecha2.split("/"); 
        Integer dia2 = Integer.parseInt(fecha3[0]); 
        Integer mes2 = Integer.parseInt(fecha3[1]); 
        Integer anio2 = Integer.parseInt(fecha3[2]);
        
        if(anio1 > anio2){
            annioFinal = anio1 - anio2;
        }else{
            annioFinal = anio2 - anio1;
        }
        if(mes1 > mes2){
            mesFinal = mes1 - mes2;
        }else{
            mesFinal = mes2 - mes1;
        }
        if(dia1 > dia2){
            diaFinal = dia1 - dia2;
        }else{
            diaFinal = dia2 - dia1;
        }
        if(annioFinal == 0){
            if(mesFinal == 0){
                resultado = diaFinal + 1;
            }else{
                resultado = ((mesFinal*30) + diaFinal) + 1;
            }
        }else{
            if(mesFinal == 0){
                resultado = ((annioFinal*360) - diaFinal) + 1;
            }else{
                resultado = ((annioFinal*360)-((mesFinal*30) + diaFinal)) + 1;
            }
        }
        return resultado;
    }
    
    public boolean areTheSameDates(Date fechaA, Date fechaB){
        boolean areSame = false;
        String fA = String.valueOf(fechaA);
        String fB = String.valueOf(fechaB);
        int cont = 0;
        for(int i = 0; i<10; i++){
            char aux = fA.charAt(i);
            char auxB = fB.charAt(i);
            if(aux == auxB){
                cont++;
            }
        }
        if(cont == 10){
            areSame = true;
        }
        return areSame;
    }
    
    public void filtroPorNombreNombramiento(String nombrePersona){
        String estado = "";
        int cantidad = 0;
        String[][] respaldo = this.nuevosdatos;
        this.nuevosdatos = new String [0][6];
        if(this.pNombramientos.size() > 0){
            if(!nombrePersona.equals("")){
                for (int i = 0; i < this.pNombramientos.size(); i++) {
                    Nombramiento aux = (Nombramiento)this.pNombramientos.get(i);
                    if(aux.getCandidato().getNombre().contains(nombrePersona)){
                        cantidad++;
                        this.redimensionarMatrizNuevosDatos();
                        this.nuevosdatos[this.nuevosdatos.length - 1][0] = aux.getBoleta_nombramiento();
                        this.nuevosdatos[this.nuevosdatos.length - 1][1] = aux.getCandidato().toString();
                        this.nuevosdatos[this.nuevosdatos.length - 1][2] = aux.getPuesto().getNombre_puesto();
                        this.nuevosdatos[this.nuevosdatos.length - 1][3] = String.valueOf(aux.getFecha_nombramiento());
                        this.nuevosdatos[this.nuevosdatos.length - 1][4] = String.valueOf(aux.getFecha_finalizacion_nombramiento());
                        this.nuevosdatos[this.nuevosdatos.length - 1][5] = aux.getCandidato_suplido().toString();
                    }
                }
                if(cantidad > 0){
                    this.modelo.setDataVector(nuevosdatos, cabecera);
                    this.panel.setModel(modelo);
                }else{
                    this.modelo.setDataVector(null, cabecera);
                    this.panel.setModel(modelo);
                }
            }else{
                this.modelo.setDataVector(datos, cabecera);
                this.panel.setModel(modelo);
            }
        }
    }
    
    public void filtroPorNombreActivos(String nombrePersona){
        int dias = 0;
        if(this.pListaActivos.size() > 0){
            if(!nombrePersona.equals("")){
                this.datosElegiblesActivos = new String[0][4];
                for (int i = 0; i < this.pListaActivos.size(); i++) {
                    Nombramiento aux = (Nombramiento)this.pListaActivos.get(i);
                    if(aux.getCandidato().getNombre().equals(nombrePersona)){
                        this.redimensionarMatrizElegiblesActivos();
                        this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][0] = aux.getCandidato().toString();
                        this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][1] = String.valueOf(aux.getFecha_nombramiento()) + " al " + String.valueOf(aux.getFecha_finalizacion_nombramiento());
                        dias = this.calcularDiasNombramiento(aux.getFecha_nombramiento(), aux.getFecha_finalizacion_nombramiento());
                        this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][2] = String.valueOf(dias);
                        this.datosElegiblesActivos[this.datosElegiblesActivos.length - 1][3] = String.valueOf(aux.getCandidato().getVeces_nombrado());
                    }
                }
                this.modeloElegiblesActivos.setDataVector(datosElegiblesActivos, cabeceraElegiblesActivos);
                this.panelActivos.setModel(modeloElegiblesActivos);
            }
        }
    }
    
    public void filtroPorNombrePasivo(String nombrePersona){
        int dias = 0;
        if(this.pListaPasivos.size() > 0){
            if(!nombrePersona.equals("")){
                this.datosElegiblesPasivos = new String[0][4];
                for (int i = 0; i < this.pListaPasivos.size(); i++) {
                    Nombramiento aux = (Nombramiento)this.pListaPasivos.get(i);
                    if(aux.getCandidato().getNombre().equals(nombrePersona)){
                        this.redimensionarMatrizElegiblesActivos();
                        this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][0] = aux.getCandidato().toString();
                        this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][1] = String.valueOf(aux.getFecha_nombramiento()) + " al " + String.valueOf(aux.getFecha_finalizacion_nombramiento());
                        dias = this.calcularDiasNombramiento(aux.getFecha_nombramiento(), aux.getFecha_finalizacion_nombramiento());
                        this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][2] = String.valueOf(dias);
                        this.datosElegiblesPasivos[this.datosElegiblesPasivos.length - 1][3] = String.valueOf(aux.getCandidato().getVeces_nombrado());
                    }
                }
                this.modeloElegiblesPasivos.setDataVector(datosElegiblesPasivos, cabeceraElegiblesPasivos);
                this.panelPasivos.setModel(modeloElegiblesPasivos);
            }
        }
    }
    
    public void filtroPorNombrePersonaSin(String nombrePersona){
        String estado = "";
        this.datosPersonasSin = new String[0][3];
        if(this.pListaPersonasSin.size() > 0){
            if(!nombrePersona.equals("")){
                this.datosPersonasSin = new String[0][3];
                for (int i = 0; i < this.pListaPersonasSin.size(); i++) {
                    Personal aux = (Personal)this.pListaPersonasSin.get(i);
                    if(aux.getCod_puesto().getNombre_puesto().equals(nombrePersona)){
                        this.redimensionarMatrizPersonasSin();
                        this.datosPersonasSin[this.datosPersonasSin.length - 1][0] = aux.toString();
                        if(aux.isConPropiedad()){
                            estado = "En propiedad";
                        }else{
                            estado = "Interino";
                        }
                        this.datosPersonasSin[this.datosPersonasSin.length - 1][1] = estado;
                        this.datosPersonasSin[this.datosPersonasSin.length - 1][2] = aux.getCod_puesto().getNombre_puesto();
                    }
                }
                this.modeloPersonasSin.setDataVector(datosPersonasSin, cabeceraPersonasSin);
                this.panelPersonaSinNombramientos.setModel(modeloPersonasSin);
            }
        }
    }
    
    /**METODOS PARA GENERAR REPORTES**/
    public void reporteElegiblesActivos(){
        ReporteListaGeneral oR = new ReporteListaGeneral();
        if(this.pListaActivos.size() > 0){
            for (int i = 0; i < this.pListaActivos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pListaActivos.get(i);
                oR.setDias(this.calcularDiasNombramiento(aux.getFecha_nombramiento(), aux.getFecha_finalizacion_nombramiento()));
                oR.addReporte(aux);
            }
            try {
                // TODO add your handling code here:
                //String path = System.getProperty("user.dir") + 
                //      System.getProperty("file.separator") + 
                //      "src\\Reportes\\GeneralListReport.jrxml";
                String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "reports/GeneralListReport.jrxml";
                
                JasperReport reporteJasper = JasperCompileManager.compileReport(path);
                JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper, null, oR);

                JDialog viewer = new JDialog(new JFrame(),"Vista previa del reporte", true);
                viewer.setSize(800,1000);
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(mostrarReporte);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }catch(JRException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "No existen registro para generar un reporte");
        }
    }
    
    public void reporteElegiblesPasivos(){
        ReporteListaGeneral oR = new ReporteListaGeneral();
        if(this.pListaPasivos.size() > 0){
            for (int i = 0; i < this.pListaPasivos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pListaPasivos.get(i);
                oR.addReporte(aux);
            }
            try {
                // TODO add your handling code here:
//                String path = System.getProperty("user.dir") + 
//                      System.getProperty("file.separator") + 
//                      "src\\Reportes\\GeneralListReportPasive.jrxml";
                
                String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "reports/GeneralListReportPasive.jrxml";
                JasperReport reporteJasper = JasperCompileManager.compileReport(path);
                JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper, null, oR);

                JDialog viewer = new JDialog(new JFrame(),"Vista previa del reporte", true);
                viewer.setSize(800,1000);
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(mostrarReporte);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }catch(JRException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "No existen registro para generar un reporte");
        }
    }
    
    public void reporteListaActivoPorPuesto(String puesto){
        ReporteListaGeneral oR = new ReporteListaGeneral();
        
        if(this.pListaActivos.size() > 0){
            for (int i = 0; i < this.pListaActivos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pListaActivos.get(i);
                if(aux.getPuesto().getNombre_puesto().equals(puesto)){
                    oR.setDias(this.calcularDiasNombramiento(aux.getFecha_nombramiento(), aux.getFecha_finalizacion_nombramiento()));
                    oR.addReporte(aux);
                }
            }
            try {
                // TODO add your handling code here:
//                String path = System.getProperty("user.dir") + 
//                      System.getProperty("file.separator") + 
//                      "src\\Reportes\\GeneralListReport.jrxml";
                String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "reports/GeneralListReport.jrxml";
                
                JasperReport reporteJasper = JasperCompileManager.compileReport(path);
                JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper, null, oR);

                JDialog viewer = new JDialog(new JFrame(),"Vista previa del reporte", true);
                viewer.setSize(800,1000);
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(mostrarReporte);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }catch(JRException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "No existen registro para generar un reporte");
        }
    }
    
    public void reporteListaPasivoPorPuesto(String puesto){
        ReporteListaGeneral oR = new ReporteListaGeneral();
        if(this.pListaPasivos.size() > 0){
            for (int i = 0; i < this.pListaPasivos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pListaPasivos.get(i);
                if(aux.getPuesto().getNombre_puesto().equals(puesto)){
                    oR.addReporte(aux);
                }
            }
            try {
                // TODO add your handling code here:
//                String path = System.getProperty("user.dir") + 
//                      System.getProperty("file.separator") + 
//                      "src\\Reportes\\GeneralListReportPasive.jrxml";
                String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "reports/GeneralListReportPasive.jrxml";
                JasperReport reporteJasper = JasperCompileManager.compileReport(path);
                JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper, null, oR);

                JDialog viewer = new JDialog(new JFrame(),"Vista previa del reporte", true);
                viewer.setSize(800,1000);
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(mostrarReporte);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }catch(JRException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "No existen registro para generar un reporte");
        }
    }
    
    public void reporteNombramientoGeneral(){
        ReporteListaNombramientoGeneral oR = new ReporteListaNombramientoGeneral();
        if(this.pNombramientos.size() > 0){
            for (int i = 0; i < this.pNombramientos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pNombramientos.get(i);
                oR.addReporte(aux);
            }
            try {
                // TODO add your handling code here:
//                String path = System.getProperty("user.dir") + 
//                      System.getProperty("file.separator") + 
//                      "src\\Reportes\\GeneralListReportNombramiento.jrxml";
                String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "reports/GeneralListReportNombramiento.jrxml";
                JasperReport reporteJasper = JasperCompileManager.compileReport(path);
                JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper, null, oR);

                JDialog viewer = new JDialog(new JFrame(),"Vista previa del reporte", true);
                viewer.setSize(800,1000);
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(mostrarReporte);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }catch(JRException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "No existen registro para generar un reporte");
        }
    }
    
    public void reporteNombramientoPorPersona(String candidato){
        ReporteListaNombramientoGeneral oR = new ReporteListaNombramientoGeneral();
        if(this.pNombramientos.size() > 0){
            for (int i = 0; i < this.pNombramientos.size(); i++) {
                Nombramiento aux = (Nombramiento)this.pNombramientos.get(i);
                if(aux.getCandidato().toString().equals(candidato)){
                    oR.addReporte(aux);
                }
            }
            try {
                // TODO add your handling code here:
//                String path = System.getProperty("user.dir") + 
//                      System.getProperty("file.separator") + 
//                      "src\\Reportes\\GeneralListReportNombramiento.jrxml";
                String path = System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "reports/GeneralListReportNombramiento.jrxml";
                JasperReport reporteJasper = JasperCompileManager.compileReport(path);
                JasperPrint mostrarReporte = JasperFillManager.fillReport(reporteJasper, null, oR);

                JDialog viewer = new JDialog(new JFrame(),"Vista previa del reporte", true);
                viewer.setSize(800,1000);
                viewer.setLocationRelativeTo(null);
                JRViewer jrv = new JRViewer(mostrarReporte);
                viewer.getContentPane().add(jrv);
                viewer.setVisible(true);
            }catch(JRException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "No existen registro para generar un reporte");
        }
    }
    
    public String recuperarNombrePuestoDeseado(){
        String[] carreras = new String[this.pPuestos.size()];
        for (int i = 0; i < this.pPuestos.size(); i++) {
            Puesto aux = (Puesto)this.pPuestos.get(i);
            carreras[i] = aux.getNombre_puesto();
        }
        String resp = (String) JOptionPane.showInputDialog(null, "Seleccione el puesto a filtrar", "Puesto", JOptionPane.DEFAULT_OPTION, null, carreras, carreras[0]);
        return resp;
    }
    
    public String recuperarNombreCandidato(){
        String resp = "";
        PersonaD oP = new PersonaD(conexion);
        ArrayList oPersona = oP.obtenerPersonal();
        if(!oP.isError()){
            if(oPersona.size() > 0){
            String[] carreras = new String[oPersona.size()];
            for (int i = 0; i < oPersona.size(); i++) {
                Personal oR = (Personal)oPersona.get(i); 
                carreras[i] = oR.toString();
            }
            resp = (String) JOptionPane.showInputDialog(null, "Seleccione el candidato deseado", "Candidato", JOptionPane.DEFAULT_OPTION, null, carreras, carreras[0]);
            }else{
                this.sonidoError.play();
                JOptionPane.showMessageDialog(rootPane, "No existen registros para generar el reporte");
            }
        }else{
            this.sonidoError.play();
            JOptionPane.showMessageDialog(rootPane, "Ocurrió un error: " + oP.getErrorMsg());
        }
        return resp;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        vistaGeneral = new javax.swing.JTabbedPane();
        vistaNombramientosCuros = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JTable();
        btnModificarNombramiento = new javax.swing.JButton();
        btnEliminarNombramiento = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        cpFiltroNombramientos = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        vistaAsignarNuevoNombramiento = new javax.swing.JPanel();
        btnLimpiar = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cpCodigoNombramiento = new javax.swing.JTextField();
        cpColaborador = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cpPuesto = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cpFechaInicio = new com.toedter.calendar.JDateChooser();
        cpFechaFinalizacion = new com.toedter.calendar.JDateChooser();
        cpUbicacion = new javax.swing.JTextField();
        CpSuple = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cpPlaza = new javax.swing.JTextField();
        vistaActivos = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        panelActivos = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        cbxPuestosActivos = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        cpFiltroActivos = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        vistaPasivos = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        cbxPuestosPasivos = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        cpFiltroPasivos = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        panelPasivos = new javax.swing.JTable();
        vistaPersonaSin = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelPersonaSinNombramientos = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        cbxPuestoPersonasSin = new javax.swing.JComboBox();
        jPanel16 = new javax.swing.JPanel();
        cpFiltroPersonasSin = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnElegiblesActivosGeneral = new javax.swing.JMenuItem();
        mnElegiblesActivosPorPuesto = new javax.swing.JMenuItem();
        mnElegiblesPasivosGeneral = new javax.swing.JMenuItem();
        mnElegiblesPasivosPorPuesto = new javax.swing.JMenuItem();
        mnNombramientoPorPersona = new javax.swing.JMenuItem();
        mnNombramientoGeneral = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        vistaGeneral.setForeground(new java.awt.Color(0, 102, 153));
        vistaGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        vistaGeneral.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        vistaGeneral.setMinimumSize(new java.awt.Dimension(149, 130));
        vistaGeneral.setPreferredSize(new java.awt.Dimension(1134, 700));

        vistaNombramientosCuros.setBackground(new java.awt.Color(255, 255, 255));

        panel.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        panel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        panel.setRowHeight(20);
        jScrollPane1.setViewportView(panel);

        btnModificarNombramiento.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnModificarNombramiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-modificar.png"))); // NOI18N
        btnModificarNombramiento.setText("Modificar");
        btnModificarNombramiento.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificarNombramiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarNombramientoActionPerformed(evt);
            }
        });

        btnEliminarNombramiento.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnEliminarNombramiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-eliminar.png"))); // NOI18N
        btnEliminarNombramiento.setText("Eliminar");
        btnEliminarNombramiento.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminarNombramiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarNombramientoActionPerformed(evt);
            }
        });

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        cpFiltroNombramientos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cpFiltroNombramientosKeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Por nombre:");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(195, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cpFiltroNombramientos)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE))
                .addGap(192, 192, 192))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cpFiltroNombramientos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout vistaNombramientosCurosLayout = new javax.swing.GroupLayout(vistaNombramientosCuros);
        vistaNombramientosCuros.setLayout(vistaNombramientosCurosLayout);
        vistaNombramientosCurosLayout.setHorizontalGroup(
            vistaNombramientosCurosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaNombramientosCurosLayout.createSequentialGroup()
                .addGroup(vistaNombramientosCurosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vistaNombramientosCurosLayout.createSequentialGroup()
                        .addGap(313, 313, 313)
                        .addComponent(btnModificarNombramiento, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
                        .addComponent(btnEliminarNombramiento, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vistaNombramientosCurosLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(vistaNombramientosCurosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        vistaNombramientosCurosLayout.setVerticalGroup(
            vistaNombramientosCurosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaNombramientosCurosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(vistaNombramientosCurosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificarNombramiento, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarNombramiento, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        vistaGeneral.addTab("Nombramientos en curso", vistaNombramientosCuros);

        vistaAsignarNuevoNombramiento.setBackground(new java.awt.Color(255, 255, 255));

        btnLimpiar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-cancelar2.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnAceptar.setFont(new java.awt.Font("Cambria Math", 1, 18)); // NOI18N
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/btn-aceptar2.png"))); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nuevo registro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel1.setText("Código nombramiento:");

        jLabel2.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel2.setText("Colaborador:");

        jLabel3.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel3.setText("Puesto:");

        jLabel4.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel4.setText("Fecha inicio:");

        jLabel5.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel5.setText("Fecha finalización:");

        jLabel6.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel6.setText("Ubicación:");

        jLabel7.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel7.setText("Suple a:");

        jLabel8.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel8.setText("En plaza:");

        cpCodigoNombramiento.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N

        cpColaborador.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N
        cpColaborador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cpColaboradorKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Buscar persona: F1");

        cpPuesto.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N
        cpPuesto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cpPuestoKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Buscar puesto: F2");

        cpFechaInicio.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N

        cpFechaFinalizacion.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N

        cpUbicacion.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N

        CpSuple.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N
        CpSuple.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CpSupleKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(153, 153, 153));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Buscar persona: F1");

        cpPlaza.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cpUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addComponent(cpCodigoNombramiento, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cpColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cpPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cpFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cpPlaza, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CpSuple, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cpFechaFinalizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cpCodigoNombramiento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(31, 31, 31)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(cpColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cpPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(cpFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(cpFechaFinalizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cpUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addComponent(CpSuple, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cpPlaza, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        javax.swing.GroupLayout vistaAsignarNuevoNombramientoLayout = new javax.swing.GroupLayout(vistaAsignarNuevoNombramiento);
        vistaAsignarNuevoNombramiento.setLayout(vistaAsignarNuevoNombramientoLayout);
        vistaAsignarNuevoNombramientoLayout.setHorizontalGroup(
            vistaAsignarNuevoNombramientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaAsignarNuevoNombramientoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(352, 352, 352))
            .addGroup(vistaAsignarNuevoNombramientoLayout.createSequentialGroup()
                .addGap(228, 228, 228)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(260, Short.MAX_VALUE))
        );
        vistaAsignarNuevoNombramientoLayout.setVerticalGroup(
            vistaAsignarNuevoNombramientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaAsignarNuevoNombramientoLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vistaAsignarNuevoNombramientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpiar)
                    .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        vistaGeneral.addTab("Asignar un nuevo nombramiento", vistaAsignarNuevoNombramiento);

        vistaActivos.setBackground(new java.awt.Color(255, 255, 255));

        panelActivos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        panelActivos.setRowHeight(20);
        jScrollPane3.setViewportView(panelActivos);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtro de información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Seleccione el puesto:");

        cbxPuestosActivos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cbxPuestosActivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxPuestosActivosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(cbxPuestosActivos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                        .addGap(34, 34, 34))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(cbxPuestosActivos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        cpFiltroActivos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cpFiltroActivosKeyReleased(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Por nombre:");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                    .addComponent(cpFiltroActivos))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cpFiltroActivos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout vistaActivosLayout = new javax.swing.GroupLayout(vistaActivos);
        vistaActivos.setLayout(vistaActivosLayout);
        vistaActivosLayout.setHorizontalGroup(
            vistaActivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaActivosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vistaActivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1109, Short.MAX_VALUE)
                    .addGroup(vistaActivosLayout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vistaActivosLayout.setVerticalGroup(
            vistaActivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vistaActivosLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(vistaActivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        vistaGeneral.addTab("Lista de elegibles activos", vistaActivos);

        vistaPasivos.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtro de información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        jLabel15.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Seleccione el puesto:");

        cbxPuestosPasivos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        cbxPuestosPasivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxPuestosPasivosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(cbxPuestosPasivos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                        .addGap(34, 34, 34))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(cbxPuestosPasivos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        cpFiltroPasivos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cpFiltroPasivosKeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Por nombre:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                    .addComponent(cpFiltroPasivos))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cpFiltroPasivos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        panelPasivos.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        panelPasivos.setRowHeight(20);
        jScrollPane4.setViewportView(panelPasivos);

        javax.swing.GroupLayout vistaPasivosLayout = new javax.swing.GroupLayout(vistaPasivos);
        vistaPasivos.setLayout(vistaPasivosLayout);
        vistaPasivosLayout.setHorizontalGroup(
            vistaPasivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaPasivosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vistaPasivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(vistaPasivosLayout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vistaPasivosLayout.setVerticalGroup(
            vistaPasivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vistaPasivosLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(vistaPasivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        vistaGeneral.addTab("Lista de elegibles pasivos", vistaPasivos);

        vistaPersonaSin.setBackground(new java.awt.Color(255, 255, 255));

        panelPersonaSinNombramientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(panelPersonaSinNombramientos);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtro por nombre de puesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        jLabel17.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Seleccione el puesto:");

        cbxPuestoPersonasSin.setFont(new java.awt.Font("Cambria Math", 0, 12)); // NOI18N
        cbxPuestoPersonasSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxPuestoPersonasSinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(164, 164, 164))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cbxPuestoPersonasSin, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(cbxPuestoPersonasSin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar información", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria Math", 0, 14), new java.awt.Color(153, 153, 153))); // NOI18N

        cpFiltroPersonasSin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cpFiltroPersonasSinKeyReleased(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Por nombre:");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cpFiltroPersonasSin))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cpFiltroPersonasSin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout vistaPersonaSinLayout = new javax.swing.GroupLayout(vistaPersonaSin);
        vistaPersonaSin.setLayout(vistaPersonaSinLayout);
        vistaPersonaSinLayout.setHorizontalGroup(
            vistaPersonaSinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vistaPersonaSinLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(vistaPersonaSinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1085, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vistaPersonaSinLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        vistaPersonaSinLayout.setVerticalGroup(
            vistaPersonaSinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vistaPersonaSinLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(vistaPersonaSinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        vistaGeneral.addTab("Personas sin nombramientos", vistaPersonaSin);

        jMenuBar1.setPreferredSize(new java.awt.Dimension(56, 35));

        jMenu1.setText("Archivo");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jMenuItem2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/menu.png"))); // NOI18N
        jMenuItem2.setText("Ir al menú principal");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Reportes");
        jMenu3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu3.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        mnElegiblesActivosGeneral.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        mnElegiblesActivosGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/mnReporteActivoGeneral.png"))); // NOI18N
        mnElegiblesActivosGeneral.setText("Lista de elegibles activos general");
        mnElegiblesActivosGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mnElegiblesActivosGeneral.setPreferredSize(new java.awt.Dimension(360, 30));
        mnElegiblesActivosGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElegiblesActivosGeneralActionPerformed(evt);
            }
        });
        jMenu3.add(mnElegiblesActivosGeneral);

        mnElegiblesActivosPorPuesto.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        mnElegiblesActivosPorPuesto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/mnReporteActivoPorPuesto.png"))); // NOI18N
        mnElegiblesActivosPorPuesto.setText("Lista de elegibles activos por puesto");
        mnElegiblesActivosPorPuesto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mnElegiblesActivosPorPuesto.setPreferredSize(new java.awt.Dimension(360, 30));
        mnElegiblesActivosPorPuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElegiblesActivosPorPuestoActionPerformed(evt);
            }
        });
        jMenu3.add(mnElegiblesActivosPorPuesto);

        mnElegiblesPasivosGeneral.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        mnElegiblesPasivosGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/mnReportePavisoGeneral.png"))); // NOI18N
        mnElegiblesPasivosGeneral.setText("Lista elegibles pasivos general");
        mnElegiblesPasivosGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mnElegiblesPasivosGeneral.setPreferredSize(new java.awt.Dimension(360, 30));
        mnElegiblesPasivosGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElegiblesPasivosGeneralActionPerformed(evt);
            }
        });
        jMenu3.add(mnElegiblesPasivosGeneral);

        mnElegiblesPasivosPorPuesto.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        mnElegiblesPasivosPorPuesto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/mnReportePavisoPorPuesto.png"))); // NOI18N
        mnElegiblesPasivosPorPuesto.setText("Lista de elegibles pasivos por puesto");
        mnElegiblesPasivosPorPuesto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mnElegiblesPasivosPorPuesto.setPreferredSize(new java.awt.Dimension(360, 30));
        mnElegiblesPasivosPorPuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElegiblesPasivosPorPuestoActionPerformed(evt);
            }
        });
        jMenu3.add(mnElegiblesPasivosPorPuesto);

        mnNombramientoPorPersona.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        mnNombramientoPorPersona.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/mnReporteNombramientoPorPersona.png"))); // NOI18N
        mnNombramientoPorPersona.setText("Lista de nombramientos por persona");
        mnNombramientoPorPersona.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mnNombramientoPorPersona.setPreferredSize(new java.awt.Dimension(360, 30));
        mnNombramientoPorPersona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnNombramientoPorPersonaActionPerformed(evt);
            }
        });
        jMenu3.add(mnNombramientoPorPersona);

        mnNombramientoGeneral.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        mnNombramientoGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/mnReporteNombramientoGeneral.png"))); // NOI18N
        mnNombramientoGeneral.setText("Lista de nombramientos general");
        mnNombramientoGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mnNombramientoGeneral.setPreferredSize(new java.awt.Dimension(360, 30));
        mnNombramientoGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnNombramientoGeneralActionPerformed(evt);
            }
        });
        jMenu3.add(mnNombramientoGeneral);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Ayuda");
        jMenu2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu2.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Cambria Math", 0, 14)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Media/logo-softclean.png"))); // NOI18N
        jMenuItem1.setText("Acerca del desarrollador");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vistaGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 1134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vistaGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        FrmContacto fC = new FrmContacto(null, true);
        fC.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mnElegiblesActivosGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnElegiblesActivosGeneralActionPerformed
        // TODO add your handling code here:
        this.reporteElegiblesActivos();
    }//GEN-LAST:event_mnElegiblesActivosGeneralActionPerformed

    private void mnElegiblesPasivosGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnElegiblesPasivosGeneralActionPerformed
        // TODO add your handling code here:
        this.reporteElegiblesPasivos();
    }//GEN-LAST:event_mnElegiblesPasivosGeneralActionPerformed

    private void mnElegiblesActivosPorPuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnElegiblesActivosPorPuestoActionPerformed
        // TODO add your handling code here:
        String resp = this.recuperarNombrePuestoDeseado();
        if(!resp.equals("")){
            this.reporteListaActivoPorPuesto(resp);
        }
    }//GEN-LAST:event_mnElegiblesActivosPorPuestoActionPerformed

    private void mnElegiblesPasivosPorPuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnElegiblesPasivosPorPuestoActionPerformed
        // TODO add your handling code here:
        String resp = this.recuperarNombrePuestoDeseado();
        if(!resp.equals("")){
            this.reporteListaPasivoPorPuesto(resp);
        }
    }//GEN-LAST:event_mnElegiblesPasivosPorPuestoActionPerformed

    private void mnNombramientoPorPersonaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnNombramientoPorPersonaActionPerformed
        // TODO add your handling code here:
        String resp = this.recuperarNombreCandidato();
        if(!resp.equals("")){
            this.reporteNombramientoPorPersona(resp);
        }
    }//GEN-LAST:event_mnNombramientoPorPersonaActionPerformed

    private void mnNombramientoGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnNombramientoGeneralActionPerformed
        // TODO add your handling code here:
        this.reporteNombramientoGeneral();
    }//GEN-LAST:event_mnNombramientoGeneralActionPerformed

    private void cpFiltroPasivosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpFiltroPasivosKeyReleased
        // TODO add your handling code here:
        this.filtroPorNombrePasivo(this.cpFiltroPasivos.getText());
    }//GEN-LAST:event_cpFiltroPasivosKeyReleased

    private void cbxPuestosPasivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxPuestosPasivosActionPerformed
        // TODO add your handling code here:
        int fila = this.cbxPuestosPasivos.getSelectedIndex();
        if(fila > -1){
            if(isPermitido){
                Puesto oP = (Puesto)this.pPuestos.get(this.cbxPuestosPasivos.getSelectedIndex());
                this.seleccionComboBoxPasivos(oP.getNombre_puesto());
            }
        }else{

        }
    }//GEN-LAST:event_cbxPuestosPasivosActionPerformed

    private void cpFiltroActivosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpFiltroActivosKeyReleased
        // TODO add your handling code here:
        
        this.filtroPorNombreActivos(this.cpFiltroActivos.getText());
        
    }//GEN-LAST:event_cpFiltroActivosKeyReleased

    private void cbxPuestosActivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxPuestosActivosActionPerformed
        // TODO add your handling code here:
        int fila = this.cbxPuestosActivos.getSelectedIndex();
        if(fila > -1){
            if(isPermitido){
                Puesto oP = (Puesto)this.pPuestos.get(this.cbxPuestosActivos.getSelectedIndex());
                this.seleccionComboBoxActivos(oP.getNombre_puesto());
            }
        }else{

        }
    }//GEN-LAST:event_cbxPuestosActivosActionPerformed

    private void CpSupleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CpSupleKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            this.buscarPersonalSuple();
        }
    }//GEN-LAST:event_CpSupleKeyPressed

    private void cpPuestoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpPuestoKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_F2){
            this.buscarPuesto();
        }
    }//GEN-LAST:event_cpPuestoKeyPressed

    private void cpColaboradorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpColaboradorKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_F1){
            this.buscarPersonal();
        }
    }//GEN-LAST:event_cpColaboradorKeyPressed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        // TODO add your handling code here:
        this.okButton();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        this.limpiarDatosNombramiento();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnEliminarNombramientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarNombramientoActionPerformed
        // TODO add your handling code here:
        int fila = this.panel.getSelectedRow();
        if(fila > -1){
            Nombramiento aux = (Nombramiento)this.pNombramientos.get(fila);
            this.eliminarNombramiento(aux.getBoleta_nombramiento(), aux.getCandidato().getCedula());
        }
    }//GEN-LAST:event_btnEliminarNombramientoActionPerformed

    private void btnModificarNombramientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarNombramientoActionPerformed
        // TODO add your handling code here:
        int fila = this.panel.getSelectedRow();
        if (fila > -1)
        {
            Nombramiento oN = (Nombramiento)this.pNombramientos.get(fila);
            this.diasAnterior = this.calcularDiasNombramiento(oN.getFecha_nombramiento(), oN.getFecha_finalizacion_nombramiento());
            this.setNombramiento(oN);
            this.isModificar = true;
            this.boletaModificar = oN.getBoleta_nombramiento();
            this.cedulaModificar = oN.getCandidato().getCedula();
            this.vistaGeneral.setSelectedIndex(1);
        }
    }//GEN-LAST:event_btnModificarNombramientoActionPerformed

    private void cpFiltroPersonasSinKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpFiltroPersonasSinKeyReleased
        // TODO add your handling code here:
        
        this.filtroPorNombrePersonaSin(this.cpFiltroPersonasSin.getText());
        
    }//GEN-LAST:event_cpFiltroPersonasSinKeyReleased

    private void cbxPuestoPersonasSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxPuestoPersonasSinActionPerformed
        // TODO add your handling code here:
        if(isPermitido){
            if(this.cbxPuestoPersonasSin.getSelectedIndex() > -1){
                Puesto oP = (Puesto)this.pPuestos.get(this.cbxPuestoPersonasSin.getSelectedIndex());
                this.seleccionComboBoxPersonasSin(oP.getNombre_puesto());
            }
        }
    }//GEN-LAST:event_cbxPuestoPersonasSinActionPerformed

    private void cpFiltroNombramientosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpFiltroNombramientosKeyReleased
        // TODO add your handling code here:
        this.filtroPorNombreNombramiento(this.cpFiltroNombramientos.getText());
    }//GEN-LAST:event_cpFiltroNombramientosKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField CpSuple;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnEliminarNombramiento;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificarNombramiento;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbxPuestoPersonasSin;
    private javax.swing.JComboBox cbxPuestosActivos;
    private javax.swing.JComboBox cbxPuestosPasivos;
    private javax.swing.JTextField cpCodigoNombramiento;
    private javax.swing.JTextField cpColaborador;
    private com.toedter.calendar.JDateChooser cpFechaFinalizacion;
    private com.toedter.calendar.JDateChooser cpFechaInicio;
    private javax.swing.JTextField cpFiltroActivos;
    private javax.swing.JTextField cpFiltroNombramientos;
    private javax.swing.JTextField cpFiltroPasivos;
    private javax.swing.JTextField cpFiltroPersonasSin;
    private javax.swing.JTextField cpPlaza;
    private javax.swing.JTextField cpPuesto;
    private javax.swing.JTextField cpUbicacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JMenuItem mnElegiblesActivosGeneral;
    private javax.swing.JMenuItem mnElegiblesActivosPorPuesto;
    private javax.swing.JMenuItem mnElegiblesPasivosGeneral;
    private javax.swing.JMenuItem mnElegiblesPasivosPorPuesto;
    private javax.swing.JMenuItem mnNombramientoGeneral;
    private javax.swing.JMenuItem mnNombramientoPorPersona;
    private javax.swing.JTable panel;
    private javax.swing.JTable panelActivos;
    private javax.swing.JTable panelPasivos;
    private javax.swing.JTable panelPersonaSinNombramientos;
    private javax.swing.JPanel vistaActivos;
    private javax.swing.JPanel vistaAsignarNuevoNombramiento;
    private javax.swing.JTabbedPane vistaGeneral;
    private javax.swing.JPanel vistaNombramientosCuros;
    private javax.swing.JPanel vistaPasivos;
    private javax.swing.JPanel vistaPersonaSin;
    // End of variables declaration//GEN-END:variables
}
