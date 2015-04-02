package no.gravem.hauk.haukbrewcontrol;

import android.app.Application;
import org.w3c.dom.Document;

/**
 * Created by agravem on 02.04.2015.
 */
public class HaukBrewControlApplication extends Application {

    private Document xmlDocument = null;
    private static HaukBrewControlApplication ourInstance = new HaukBrewControlApplication();

    public static HaukBrewControlApplication getInstance() {
        return ourInstance;
    }

    public HaukBrewControlApplication() {
    }

    public Document getXmlDocument(){
        if(xmlDocument == null){
            BrewControlConnection controlConnection = new BrewControlConnection();
            xmlDocument = controlConnection.initXmlDocument();
        }
        return xmlDocument;
    }

    public void setXmlDocument(Document xmlDocument){
        this.xmlDocument = xmlDocument;
    }
}
