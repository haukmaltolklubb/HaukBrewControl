package no.gravem.hauk.haukbrewcontrol;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by agravem on 21.05.2015.
 */
public class StatusXml {

    private Document xmlDocument;

    public StatusXml(String xml) {
        Log.d(this.getClass().getName(), "initXmlDocument");
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Log.d(this.getClass().getName(), "XML: " + xml);
            xmlDocument = db.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "InitXMLDocument", e);
        }
    }

    public Integer getUrom1Value(){
        return Integer.valueOf(getNodeValue("urom1"));
    }

    public String getUrom2Value(){
        return getNodeValue("urom2");
    }

    public String getUrom3Varlue(){
        return getNodeValue("urom3");
    }

    public String getUrom4Value(){
        return getNodeValue("urom4");
    }

    public String getTemp1Value(){
        return getNodeValue("ts1");
    }

    public String getTemp2Value(){
        return getNodeValue("ts2");
    }

    public String getTemp3Value(){
        return getNodeValue("ts3");
    }

    public String getTemp4Value(){
        return getNodeValue("ts4");
    }

    public String getVar1Value(){
        return getNodeValue("var1");
    }

    public String getVar2Value(){
        return getNodeValue("var2");
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


    private String getNodeValue(String tagName){

        NodeList nodelist = xmlDocument.getElementsByTagName(tagName);
        if(nodelist !=  null){
            Node node = nodelist.item(0);
            if(node != null){
                Log.d(this.getClass().getName(), "NodeValue: "+ node.getTextContent());
                return node.getTextContent();
            }
        }
        return null;
    }
}
