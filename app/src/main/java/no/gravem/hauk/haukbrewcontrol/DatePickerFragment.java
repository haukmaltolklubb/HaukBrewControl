package no.gravem.hauk.haukbrewcontrol;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by agravem on 01.04.2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public DatePickerDialog.OnDateSetListener dateSetListener;
    int year;
    int month;
    int day;

    public DatePickerFragment(){}

    public void setDatePickerListener(DatePickerDialog.OnDateSetListener listener){
        dateSetListener = listener;
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("YEAR");
        month = args.getInt("MONTH");
        day = args.getInt("DAY");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if(savedInstanceState != null) {
            year = savedInstanceState.getInt("YEAR");
            month = savedInstanceState.getInt("MONTH");
            day = savedInstanceState.getInt("DAY");
        }
        else{
            if(year == 0 || month == 0 || day == 0){
                Log.d(this.getClass().getName(), "Using Cal");
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_WEEK);
            }
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) { }
}
