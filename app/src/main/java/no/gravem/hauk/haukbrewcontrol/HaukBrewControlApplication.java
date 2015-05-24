package no.gravem.hauk.haukbrewcontrol;

import android.app.Application;
import android.util.Log;

import org.w3c.dom.Document;

/**
 * Created by agravem on 02.04.2015.
 */
public class HaukBrewControlApplication extends Application {

    private static HaukBrewControlApplication ourInstance = new HaukBrewControlApplication();

    public static HaukBrewControlApplication getInstance() {
        return ourInstance;
    }

}
