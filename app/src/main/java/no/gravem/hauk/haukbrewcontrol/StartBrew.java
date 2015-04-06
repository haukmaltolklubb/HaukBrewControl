package no.gravem.hauk.haukbrewcontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Calendar;


public class StartBrew extends ActionBarActivity {

    private TextView dateDisplay;
    private Button pickDateBtn;
    private Calendar calendar;
    Bundle dateTimeBundle;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    DialogFragment timePicker;
    DialogFragment datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_brew);

        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        pickDateBtn = (Button) findViewById(R.id.setStartDatoBtn);

        StartBrewTask startBrewTask = new StartBrewTask();
        startBrewTask.execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_brew, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openMashWindow(View view) {
        startActivity(new Intent(this, Mash.class));
    }

    public void openHeatWindow(View view) {
        startActivity(new Intent(this, Heat.class));
    }

    public void openPumpWindow(View view) {
        startActivity(new Intent(this, Pump.class));
    }

    public void openFermentActivity(View view) {
        startActivity(new Intent(this, Ferment.class));
    }

    private class StartBrewTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            createCalendar();
            createDateTimeBundle();

            timePicker = new TimePickerFragment();
            TimePickerDialog.OnTimeSetListener timeSetListener =
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(android.widget.TimePicker view,
                                              int hourOfDay, int minuteSet) {
                            Log.d("", "" + hourOfDay + ":" + minute);
                            updateTime(hourOfDay, minuteSet);
                            dateDisplay.setText(
                                    new StringBuilder()
                                            // Month is 0 based so add 1
                                            .append(day).append(".")
                                            .append(month + 1).append(".")
                                            .append(year).append("  ")
                                            .append(hour)
                                            .append(":")
                                            .append("" + minute));
                        }
                    };
            ((TimePickerFragment) timePicker).setTimePickerListener(timeSetListener);

            datePicker = new DatePickerFragment();
            pickDateBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    datePicker.setArguments(dateTimeBundle);
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                }
            });
            DatePickerDialog.OnDateSetListener dateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int yearSet, int monthSet, int daySet) {
                            Log.d(this.getClass().getName(), "Date is: " + yearSet + "\\" + monthSet + "\\" + daySet);
                            updateDate(yearSet, monthSet, daySet);

                            timePicker.setArguments(dateTimeBundle);
                            timePicker.show(getSupportFragmentManager(), "timePicker");
                        }
                    };
            ((DatePickerFragment) datePicker).setDatePickerListener(dateSetListener);

            return null;
        }


        private void createDateTimeBundle() {
            dateTimeBundle = new Bundle();
            dateTimeBundle.putInt("YEAR", year);
            dateTimeBundle.putInt("MONTH", month);
            dateTimeBundle.putInt("DAY", day);
            dateTimeBundle.putInt("HOUR", hour);
            dateTimeBundle.putInt("MINUTE", minute);
        }

        private void createCalendar() {
            Log.d(this.getClass().getName(), "init Calendar with todays date");
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        private void updateDate(int yearSet, int monthSet, int daySet) {
            Log.d(this.getClass().getName(), "updating date.");
            year = yearSet;
            month = monthSet;
            day = daySet;
            calendar.set(Calendar.YEAR, yearSet);
            calendar.set(Calendar.MONTH, monthSet);
            calendar.set(Calendar.DAY_OF_MONTH, daySet);

            dateTimeBundle.putInt("YEAR", year);
            dateTimeBundle.putInt("MONTH", month);
            dateTimeBundle.putInt("DAY", day);
        }

        private void updateTime(int hourOfDay, int minuteSet) {
            hour = hourOfDay;
            minute = minuteSet;
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            dateTimeBundle.putInt("HOUR", hourOfDay);
            dateTimeBundle.putInt("MINUTE", minuteSet);

            calculateTime();
        }

        private void calculateTime() {
            // Month is 0 based so add 1
            DateTime timeSet = new DateTime(year, month + 1, day, hour, minute);
            DateTime zeroPointTime = new DateTime(2000, 1, 1, 0, 0);
            Duration duration = new Duration(zeroPointTime, timeSet);
            int secondsSince2000 = (int) duration.getStandardSeconds();

            Log.d(this.getClass().getName(), "Date set: " + timeSet.toString());
            Log.d(this.getClass().getName(), "Zero point: " + zeroPointTime.toString());
            Log.d(this.getClass().getName(), "Seconds since year 2000: " + secondsSince2000);
            Toast toast = Toast.makeText(getApplicationContext(), "Oppdatert starttid", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
