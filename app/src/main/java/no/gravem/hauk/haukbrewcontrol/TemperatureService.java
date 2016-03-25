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

    public static String getFormattedTemperatureString(String temperature){
        double temp = Double.valueOf(temperature);
        double fixed = temp / 10;

        if(fixed % 1 == 0)
            return String.valueOf((int)fixed);
        return String.valueOf(fixed);

    }
}
