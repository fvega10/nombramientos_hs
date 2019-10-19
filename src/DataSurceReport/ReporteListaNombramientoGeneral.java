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
public class ReporteListaNombramientoGeneral implements JRDataSource{
    private int indice = -1;
    private List<Nombramiento> lista = new ArrayList<Nombramiento>();
    
    @Override
    public boolean next() throws JRException {
        return ++indice < lista.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;  

        if("candidato".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getCandidato().toString();
        }
        if("suple".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getCandidato_suplido().toString();
        }
        else if("fechaInicio".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getFecha_nombramiento();
        } 
        else if("fechaFinalizacion".equals(jrf.getName())) 
        { 
            valor = lista.get(indice).getFecha_finalizacion_nombramiento();
        }
        else if("puesto".equals(jrf.getName()))
        {
            valor = lista.get(indice).getPuesto().getNombre_puesto();
        }
        
        return valor; 
    }
    
    public void addReporte(Nombramiento reporte){
        this.lista.add(reporte);
    }
}
