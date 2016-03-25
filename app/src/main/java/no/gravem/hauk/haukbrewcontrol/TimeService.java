package no.gravem.hauk.haukbrewcontrol;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by anne-marit.gravem on 21.02.2016.
 */
public abstract class TimeService {

    static DateTime zeroPointTime = new DateTime(2000, 1, 1, 0, 0);

    public static int getPLSFormattedTime(String time){
        //time angis i minutt. PLS ønsker sekunder.
        return Integer.valueOf(time)*60;
    }

    public static String getFormattedTimeFromPLS(String time){
        return String.valueOf(Integer.valueOf(time)/60);
    }

    public static int getSecondsToStart(DateTime timeSet){

        Duration duration = new Duration(zeroPointTime, timeSet);
        int secondsSince2000 = (int) duration.getStandardSeconds();

        return secondsSince2000;
    }

    public static DateTime getDateTimeFromSeconds(int seconds){
        DateTime givenTime = zeroPointTime.plusSeconds(seconds);

        return givenTime;
    }
}
