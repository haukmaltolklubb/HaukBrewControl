package no.gravem.hauk.haukbrewcontrol;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by agravem on 02.04.2015.
 */
public class BrewControlConnectionMockup implements IBrewControlConnection {


    @Override
    public Document getStatusXmlDocument(Context context) {

        try {
            Log.d(this.getClass().getName(), "Using mocked XML connection");

            InputStream xml = context.getResources().getAssets().open("status.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setPLSVarValue(String query) throws Exception {
        return true;
    }

    public boolean setPLSUromValue(String query) throws Exception{
        return true;
    }
}
