package no.gravem.hauk.haukbrewcontrol;

import android.util.Log;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by agravem on 02.04.2015.
 */
public class BrewControlConnection{

    Document xmlDocument = null;
    private String sensorUrl = "http://88.84.50.37/api/status.xml";
    private String userName = "admin";
    private String password = "hannah2002";

        public Document initXmlDocument() {
            Log.d(this.getClass().getName(), "initXmlDocument");
            try {
                URLConnection connection = getUrlConnection();
                InputStream xml = connection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                xmlDocument = db.parse(xml);
                return xmlDocument;

            } catch (Exception e) {
                Log.e(this.getClass().getName(), "InitXMLDocument", e);
            }
            return null;
        }

        private URLConnection getUrlConnection() throws Exception {
            Log.d(this.getClass().getName(), "Opening connection for PLC...");
            URL url = new URL(sensorUrl);
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
}
