package no.gravem.hauk.haukbrewcontrol;

import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by agravem on 30.03.2015.
 */
public class BrewControlOperations {

    private String tempIkkeFunnet = "Temperatur ikke funnet";
    Document xmlDocument = null;

    public BrewControlOperations(Document document){
        xmlDocument = document;
    }

    public String getNodeValue(String tagName){
        if(xmlDocument != null){
            NodeList nodelist = xmlDocument.getElementsByTagName(tagName);
            if(nodelist !=  null){
                Node node = nodelist.item(0);
                if(node != null){
                    Log.d(this.getClass().getName(), "NodeValue: "+ node.getTextContent());
                    return node.getTextContent();
                }
            }
        }
        Log.d(this.getClass().getName(), "Nodevalue not found for '"+tagName+"'");
        return "NA";
    }

    public void setNodeValue(String tagName, String value){

    }

    public BrewProcess getBrewProcess(String brewProcess){
        int brewProcessInt = Integer.parseInt(brewProcess);
        if(brewProcessInt == 0)
            return BrewProcess.None;
        if(brewProcessInt == 1)
            return BrewProcess.Heat;
        if(brewProcessInt == 2)
            return BrewProcess.Mash;
        if(brewProcessInt == 3)
            return BrewProcess.Pump;
        if(brewProcessInt == 4)
            return  BrewProcess.Ferment;
        return BrewProcess.None;
    }
}
