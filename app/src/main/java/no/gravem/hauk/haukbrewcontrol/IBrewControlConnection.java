package no.gravem.hauk.haukbrewcontrol;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URLConnection;

/**
 * Created by agravem on 01.04.2015.
 */
public interface IBrewControlConnection {


    public Document getStatusXmlDocument(Context context);

    public boolean postURLRequest(String query) throws Exception;
}
