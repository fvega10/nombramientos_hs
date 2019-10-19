/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataSurceReport;

import java.util.ArrayList;
import java.util.List;
import logica.Nombramiento;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author Fabricio
 */
public class ReporteListaGeneral implements JRDataSource{
    private int indice = -1;
    private List<Nombramiento> lista = new ArrayList<Nombramiento>();
    private int dias = 0;
    
    @Override
    public boolean next() throws JRException {
        return ++indice < lista.size();
    }
    
    public void setDias(int pdias){
        dias = pdias;
    }
    
    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;  

        if("persona".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getCandidato().toString();
        }
        if("puesto".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getPuesto().getNombre_puesto();
        }
        else if("fechaNombramiento".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getFecha_nombramiento();
        } 
        else if("dias".equals(jrf.getName())) 
        { 
            valor = dias;
        }
        else if("diasTotalNombramiento".equals(jrf.getName()))
        {
            valor = lista.get(indice).getCandidato().getVeces_nombrado();
        }
        
        return valor; 
    }
    
    public void addReporte(Nombramiento reporte){
        this.lista.add(reporte);
    }
}
