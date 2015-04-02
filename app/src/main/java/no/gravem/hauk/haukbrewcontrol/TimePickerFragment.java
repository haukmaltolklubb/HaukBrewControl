package no.gravem.hauk.haukbrewcontrol;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by agravem on 01.04.2015.
 */
public class TimePickerFragment extends DialogFragment
        /*implements TimePickerDialog.OnTimeSetListener */{

    private TimePickerDialog.OnTimeSetListener listener;

    public TimePickerFragment() {}

    public void setTimePickerListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour;
        int minute;
        if(savedInstanceState != null) {
            hour = savedInstanceState.getInt("HOUR");
            minute = savedInstanceState.getInt("MINUTE");
        }
        else{
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Log.d(this.getClass().getName(), "" + hourOfDay + ":" + minute);

    }
}

