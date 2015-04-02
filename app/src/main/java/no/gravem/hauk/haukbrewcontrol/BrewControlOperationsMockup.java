package no.gravem.hauk.haukbrewcontrol;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by agravem on 01.04.2015.
 */
public class BrewControlOperationsMockup implements IBrewControlOperations {

    Document xmlDocument = null;

    public BrewControlOperationsMockup(Document document){
        xmlDocument = document;
    }


    /**@Override
    public Document initXmlDocument() {
        Log.d(this.getClass().getName(), "initXmlDocument");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputStream is = getClass().getClassLoader().getResourceAsStream("/res/xml/status.xml");
            InputStream xmlFile = new FileInputStream("C:\\dev\\projects\\HaukBrewControl\\app\\src\\main\\res\\xml\\status.xml");
            //File xmlFile = new File("/res/xml/status.xml");
            //Log.d(this.getClass().getName(), "Using xmlFile at: " + xmlFile);
            xmlDocument = db.parse(xmlFile);
            //xmlDocument = db.parse(is);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Could not get XML. Message: " + e.getMessage(), e);
        }
        return xmlDocument;
    }**/

    @Override
    public String getNodeValue(String tagName) {
        if(xmlDocument != null){
            NodeList nodelist = xmlDocument.getElementsByTagName(tagName);
            if(nodelist !=  null){
                Node node = nodelist.item(0);
                if(node != null){
                    Log.d(this.getClass().getName(), "NodeValue: " + node.getTextContent());
                    return node.getTextContent();
                }
            }
        }
        Log.d(this.getClass().getName(), "Nodevalue not found for '"+tagName+"'");
        return "NA";
    }
}
