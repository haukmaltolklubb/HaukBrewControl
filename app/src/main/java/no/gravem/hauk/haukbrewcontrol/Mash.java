package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

/**
 * Created by GTG on 20.01.2015.
 */
public class Mash extends ActionBarActivity {

    ImageButton startButton, stopButton, updateButton;
    private TextView mashTempBottom, mashTempTop, mashTime, mashHeading;
    private EditText level1Temperature, level1Time, level2Temperature, level2Time, level3Temperature, level3Time;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;

    private ControllerService controllerService = new ControllerService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mash);

        startButton = (ImageButton)findViewById(R.id.mashStartBtn);
        stopButton = (ImageButton)findViewById(R.id.mashStopBtn);
        updateButton = (ImageButton)findViewById(R.id.mashUpdateBtn);

        mashHeading = (TextView)findViewById(R.id.mashHeading);
        mashTempTop = (TextView) findViewById(R.id.mashTempTop);
        mashTempBottom = (TextView) findViewById(R.id.mashTempBottom);
        mashTime = (TextView) findViewById(R.id.mashTime);

        level1Temperature = (EditText) findViewById(R.id.mashLevel1Temperature);
        level1Time = (EditText)findViewById(R.id.mashLevel1Time);
        level2Temperature = (EditText)findViewById(R.id.mashLevel2Temperature);
        level2Time = (EditText)findViewById(R.id.mashLevel2Time);
        level3Temperature = (EditText)findViewById(R.id.mashLevel3Temperature);
        level3Time = (EditText)findViewById(R.id.mashLevel3Time);

        progressBar = (ProgressBar) findViewById(R.id.mashProcessDataProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_mashContainer);
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
        getMenuInflater().inflate(R.menu.menu_mash, menu);
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

    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    StatusXml statusXml = new StatusXml(result);
                    setValuesInView(statusXml.getTemp2Value(), statusXml.getTemp3Value(), statusXml.getProcessRunningTimeInMinutes(), BrewProcess.createFrom(statusXml.getUrom1Value()));

                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateButtonStatuses(BrewProcess currentProcess){
        if (currentProcess == BrewProcess.MASH) {
            mashHeading.setText("Mesking pågår");
            startButton.setEnabled(false);
            startButton.setActivated(false);
            updateButton.setEnabled(true);
            updateButton.setActivated(true);
            stopButton.setEnabled(true);
            stopButton.setActivated(true);
        } else {
            mashHeading.setText("Mesking ikke aktiv");
            startButton.setEnabled(true);
            startButton.setActivated(true);
            updateButton.setEnabled(false);
            updateButton.setActivated(false);
            stopButton.setEnabled(false);
            stopButton.setActivated(false);
        }
    }

    private void setValuesInView(final String t2, final String t3, final int time, final BrewProcess brewProcess){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mashTempTop.setText(t2);
                mashTempBottom.setText(t3);
                mashTime.setText(time + " min");

                updateButtonStatuses(brewProcess);
                progressBar.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            }
        });
    }

    public void startMash(View view){
        if(requiredVariablesAreSet()) {
            Log.d(this.getClass().getName(), "startMash");
            try{
                startMashProcessInPLS();
                updateButtonStatuses(BrewProcess.MASH);
                Toast toast = Toast.makeText(getApplicationContext(), "Mesking startet", Toast.LENGTH_SHORT);
                toast.show();
            }
            catch(PLSConnectionException e){
                Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Mangler påkrevde verdier", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void updateMashProcess(View view){
        Log.d(this.getClass().getName(), "updateMash");
        try{
            updateMashProcessInPLS();
            Toast toast = Toast.makeText(getApplicationContext(), "Mesking oppdatert", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch(PLSConnectionException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void stopMash(View view){
        Log.d(this.getClass().getName(), "stopMash");

        try{
            stopMashProcessInPLS();
            updateButtonStatuses(BrewProcess.NONE);
            Toast toast = Toast.makeText(getApplicationContext(), "Mesking stoppet", Toast.LENGTH_SHORT);
            toast.show();

            startActivity(new Intent(this, MainActivity.class));
        }
        catch(PLSConnectionException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean requiredVariablesAreSet(){
        return !Strings.isNullOrEmpty(level1Temperature.getText().toString()) &&
                !Strings.isNullOrEmpty(level1Time.getText().toString()) &&
                !Strings.isNullOrEmpty(level2Temperature.getText().toString()) &&
                !Strings.isNullOrEmpty(level2Time.getText().toString()) &&
                !Strings.isNullOrEmpty(level3Temperature.getText().toString()) &&
                !Strings.isNullOrEmpty(level3Time.getText().toString());
    }

    public void startMashProcessInPLS() {
        //Set VAR1 = Temp1 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)

         controllerService
             .setUrom(1,2)
             .setVar(1, TemperatureService.getPLSFormattedTemperatureInt(level1Temperature.getText().toString()))
             .setVar(3, TemperatureService.getPLSFormattedTemperatureInt(level2Temperature.getText().toString()))
             .setVar(4, TemperatureService.getPLSFormattedTemperatureInt(level3Temperature.getText().toString()))
             .setVar(5, TimeService.getPLSFormattedTime(level1Time.getText().toString()))
             .setVar(6, TimeService.getPLSFormattedTime(level2Time.getText().toString()))
             .setVar(7, TimeService.getPLSFormattedTime(level3Time.getText().toString()))
             .execute();
    }

    private void updateMashProcessInPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {

                try {
                    StatusXml statusXml = new StatusXml(result);
                    boolean isMash = BrewProcess.createFrom(statusXml.getUrom1Value()) == BrewProcess.MASH;
                    if (isMash) {
                        controllerService
                                .setVar(1, TemperatureService.getPLSFormattedTemperatureInt(level1Temperature.getText().toString()))
                                .setVar(3, TemperatureService.getPLSFormattedTemperatureInt(level2Temperature.getText().toString()))
                                .setVar(4, TemperatureService.getPLSFormattedTemperatureInt(level3Temperature.getText().toString()))
                                .setVar(5, TimeService.getPLSFormattedTime(level1Time.getText().toString()))
                                .setVar(6, TimeService.getPLSFormattedTime(level2Time.getText().toString()))
                                .setVar(7, TimeService.getPLSFormattedTime(level3Time.getText().toString()))
                                .execute();
                    }
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopMashProcessInPLS(){

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        controllerService.setUrom(1, 0).execute();
    }
}
