package no.gravem.hauk.haukbrewcontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Calendar;


public class StartBrew extends ActionBarActivity {

    private TextView dateDisplay;
    private ImageButton pickDateBtn;
    private Calendar calendar;
    Bundle dateTimeBundle;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int secondsToStart;
    private ControllerService controllerService = new ControllerService();
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;

    DialogFragment timePicker;
    DialogFragment datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_brew);

        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        pickDateBtn = (ImageButton) findViewById(R.id.setStartDatoBtn);

        createTimeDatePicker();
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePicker.setArguments(dateTimeBundle);
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int yearSet, final int monthSet, final int daySet) {
                        Log.d(this.getClass().getName(), "Date is: " + yearSet + "\\" + monthSet + "\\" + daySet);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDate(yearSet, monthSet, daySet);

                                timePicker.setArguments(dateTimeBundle);
                                timePicker.show(getSupportFragmentManager(), "timePicker");
                            }
                        });

                    }
                };
        ((DatePickerFragment) datePicker).setDatePickerListener(dateSetListener);

        progressBar = (ProgressBar) findViewById(R.id.startBrewProcessDataProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_startBrewContainer);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                updateValuesFromPLS();
            }
        });

        updateValuesFromPLS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_brew, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                Log.i(this.getClass().getName(), "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                swipeLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                updateValuesFromPLS();

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

    private void createTimeDatePicker(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
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

    private void updateDate(final int yearSet, final int monthSet, final int daySet) {
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


    private void updateTime(final int hourOfDay, final int minuteSet) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hour = hourOfDay;
                minute = minuteSet;
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                dateTimeBundle.putInt("HOUR", hourOfDay);
                dateTimeBundle.putInt("MINUTE", minuteSet);

                calculateTime();
            }
        });
    }

    private void calculateTime() {
        // Month is 0 based so add 1
        DateTime timeSet = new DateTime(year, month+1, day, hour, minute);
        Log.d(this.getClass().getName(), "Date set: " + timeSet.toString());
        int secondsSince2000 = TimeService.getSecondsToStart(timeSet);

        Log.d(this.getClass().getName(), "Updating starttime ");
        controllerService.setUrom(2, secondsSince2000, new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    Log.d(this.getClass().getName(), "SetUromResult: " + result);
                } catch (PLSConnectionException e) {
                    Log.e(this.getClass().getName(), "SetUromResult NORESULT: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        Toast toast = Toast.makeText(getApplicationContext(), "Oppdatert starttid", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    StatusXml statusXml = new StatusXml(result);
                    setValuesInView(statusXml.getUrom2Value());
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String startTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DateTime timeSet = TimeService.getDateTimeFromSeconds(Integer.parseInt(startTime));

                dateDisplay.setText( timeSet.toString("d.MM.yy HH:mm"));
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
