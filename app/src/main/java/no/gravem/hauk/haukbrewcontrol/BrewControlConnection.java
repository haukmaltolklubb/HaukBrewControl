package no.gravem.hauk.haukbrewcontrol;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by agravem on 02.04.2015.
 */
public class BrewControlConnection implements IBrewControlConnection{

    private String sensorUrl = "http://88.84.50.37/api/";
    private String statusXmlName = "status.xml";

    private String userName = "admin";
    private String password = "hannah2002";

    public Document getStatusXmlDocument(Context context) {
        Log.d(this.getClass().getName(), "initXmlDocument");
        try {
            URLConnection connection = getUrlGETConnection();
            InputStream xml = connection.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(xml);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "InitXMLDocument", e);
        }
        return null;
    }

    private URLConnection getUrlGETConnection() throws Exception {
        Log.d(this.getClass().getName(), "Opening connection for PLS");
        URL url = new URL(sensorUrl+statusXmlName);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password.toCharArray());
            }
        });

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");
        Log.d(this.getClass().getName(), "Opened URL connection for PLC.");
        return connection;
    }

    public boolean postURLRequest(String query) throws Exception {
        Log.d(this.getClass().getName(), "Opening SET connection for PLS");

        URL url = new URL(sensorUrl);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password.toCharArray());
            }
        });

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept", "application/xml");

        Log.d(this.getClass().getName(), "Writing to buffer....");
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();

        connection.connect();

        return true;
    }
}
