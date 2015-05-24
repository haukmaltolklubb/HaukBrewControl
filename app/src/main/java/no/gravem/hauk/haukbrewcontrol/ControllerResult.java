package no.gravem.hauk.haukbrewcontrol;

import java.net.HttpURLConnection;

/**
 * Created by agravem on 21.05.2015.
 */

public interface ControllerResult {
    void done(HttpURLConnection result);
}
