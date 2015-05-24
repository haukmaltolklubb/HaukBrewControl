package no.gravem.hauk.haukbrewcontrol;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
            return BrewProcess.NONE;
        if(brewProcessInt == 1)
            return BrewProcess.HEAT;
        if(brewProcessInt == 2)
            return BrewProcess.MASH;
        if(brewProcessInt == 3)
            return BrewProcess.PUMP;
        if(brewProcessInt == 4)
            return  BrewProcess.FERMENT;
        return BrewProcess.NONE;
    }
}
