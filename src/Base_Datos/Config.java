/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Base_Datos;

/**
 *
 * @author FabricioVega
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
public class Config 
{
    private Properties configFile;
    private boolean isOk=true;
    private String[] parametros = new String[7]; 
        
    public Config(String pRutaArchivo)
    {
      configFile = new java.util.Properties();
      String fileName = pRutaArchivo;
      InputStream is = null;
  
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            System.out.print("\nError: No se encontro el archivo de configuracion");
            isOk=false;
        }
  
        try {
            configFile.load(is);
        } catch (IOException e) {
            System.out.print("\nError: " + e.getMessage());
            isOk=false;
        }
    }
   
    public String getProperty(String key)
    {
        String value = this.configFile.getProperty(key);		
        return value;
    }
      
    public boolean getIsOK() {
        return isOk;
    }

    public String[] leerConfiguracion() 
    {
               
        for (int k=0; k < parametros.length; k++)
            parametros[k] = "";        
        
        //Cargo variables                      
        parametros[0] = this.getProperty("Server");
        parametros[1] = this.getProperty("Port");
        parametros[2] = this.getProperty("Base");
        parametros[3] = this.getProperty("User");
        parametros[4] = this.getProperty("Pass");
        parametros[5] = "org.postgresql.Driver";
        parametros[6] = this.getProperty("Schema");        
        return parametros;
    }
    
}

