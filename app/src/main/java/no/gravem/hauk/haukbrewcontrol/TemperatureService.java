package no.gravem.hauk.haukbrewcontrol;

import android.widget.EditText;

/**
 * Created by anne-marit.gravem on 21.02.2016.
 */
public abstract class TemperatureService {


    public static int getPLSFormattedTemperatureInt(String temperature){
        double temp = Double.valueOf(temperature);
        //runder opp
        double rounded = Math.round( temp * 10 );

        return (int)rounded;

    }
}
