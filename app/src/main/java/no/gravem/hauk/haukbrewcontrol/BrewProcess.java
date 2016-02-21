package no.gravem.hauk.haukbrewcontrol;

import android.app.Activity;

/**
 * Created by agravem on 01.04.2015.
 */
public enum BrewProcess {
    NONE(null),
    HEAT(no.gravem.hauk.haukbrewcontrol.Heat.class),
    MASH(no.gravem.hauk.haukbrewcontrol.Mash.class),
    PUMP(no.gravem.hauk.haukbrewcontrol.Pump.class),
    FERMENT(no.gravem.hauk.haukbrewcontrol.Ferment.class);

    private Class<Activity> activityClass;

    BrewProcess(Class activityClass) {
        this.activityClass = activityClass;
    }

    public static BrewProcess createFrom(Integer uromValue) {
        return BrewProcess.values()[uromValue];
    }

    public static String getProcessText(BrewProcess process){
        //TODO: Sett bedre tekster her!
        String processText = "";
        switch (process) {
            case FERMENT:
                processText = "Gjæring underveis";
                break;
            case HEAT:
                processText = "Koker opp ";
                break;
            case NONE:
                processText = "Ingen aktiv prosess";
                break;
            case MASH:
                processText = "Mesking pågår";
                break;
            case PUMP:
                processText = "Pumper godsakene";
                break;
            default:
                processText = "Fant ikke prosessdata";
                break;
        }
        return processText;
    }


    public Class getActivityClass() {
        return activityClass;
    }
}