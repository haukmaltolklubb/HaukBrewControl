package no.gravem.hauk.haukbrewcontrol;

import android.app.Application;
import android.util.Log;

import org.w3c.dom.Document;

/**
 * Created by agravem on 02.04.2015.
 */
public class HaukBrewControlApplication extends Application {

/**    private Document xmlDocument = null;
    IBrewControlConnection controlConnection = null;
    private static HaukBrewControlApplication ourInstance = new HaukBrewControlApplication();

    public static HaukBrewControlApplication getInstance() {
        return ourInstance;
    }

    public HaukBrewControlApplication() {
    }

    public Document getXmlDocument(){
        if(xmlDocument == null){
            Log.d(this.getClass().getName(), "Using URL connection");
            controlConnection = new BrewControlConnection();
            //Log.d(this.getClass().getName(), "Using mocked connection");
            //controlConnection = new BrewControlConnectionMockup();
            xmlDocument = controlConnection.getStatusXmlDocument(getApplicationContext());
        }
        return xmlDocument;
    }

    public IBrewControlConnection getBrewControlConnection(){
        Log.d(this.getClass().getName(), "getBrewControlConnection");
        if(controlConnection != null)
            return controlConnection;
        else{
            controlConnection = new BrewControlConnection();
            return controlConnection;
        }
    }

    public void setXmlDocument(Document xmlDocument){
        this.xmlDocument = xmlDocument;
    }**/
}
