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

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by GTG on 06.01.2015.
 */
public class Heat extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();

    private ImageButton startButton, stopButton, updateButton;
    private TextView heatTempText, heatTimeText, startTimeText, heatHeading;
    private EditText heatTemperatureEditText;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);

        startButton = (ImageButton)findViewById(R.id.heatStartBtn);
        stopButton = (ImageButton)findViewById(R.id.heatStopBtn);
        updateButton = (ImageButton)findViewById(R.id.heatUpdateBtn);

        heatHeading = (TextView)findViewById(R.id.heatHeading);
        heatTempText = (TextView) findViewById(R.id.currentHeatTemp);
        heatTimeText = (TextView) findViewById(R.id.currentHeatTime);
        startTimeText = (TextView) findViewById(R.id.startTime);

        heatTemperatureEditText = (EditText) findViewById(R.id.mashLevel1Temperature);
        progressBar = (ProgressBar) findViewById(R.id.heatProcessDataProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_heatContainer);
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
        getMenuInflater().inflate(R.menu.menu_heat, menu);
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
                    setValuesInView(statusXml.getVar1Value(), statusXml.getTemp1Value(), statusXml.getProcessRunningTimeInMinutes(), statusXml.getUrom2Value(), BrewProcess.createFrom(statusXml.getUrom1Value()));
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String var1Value, final String temp1Value, final int time, final String startTime, final BrewProcess currentProcess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateButtonStatuses(currentProcess);
                heatTempText.setText(temp1Value);
                heatTimeText.setText(time + " min");
                //startTimeText.setText(getTimeFromCounter(startTime));
                if(currentProcess == BrewProcess.HEAT)
                    heatTemperatureEditText.setText(TemperatureService.getFormattedTemperatureString(var1Value));

                progressBar.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void updateButtonStatuses(BrewProcess currentProcess){
        if (currentProcess == BrewProcess.HEAT) {
            heatHeading.setText("Koker opp");
            startButton.setEnabled(false);
            startButton.setActivated(false);
            updateButton.setEnabled(true);
            updateButton.setActivated(true);
            stopButton.setEnabled(true);
            stopButton.setActivated(true);
        } else {
            heatHeading.setText("Koking ikke aktiv");
            startButton.setEnabled(true);
            startButton.setActivated(true);
            updateButton.setActivated(false);
            updateButton.setEnabled(false);
            stopButton.setEnabled(false);
            stopButton.setActivated(false);
        }
    }

    private boolean requiredVariablesAreSet(){
        Log.d(this.getClass().getName(), "Heat temp is: " + heatTemperatureEditText.getText().toString());
        return !Strings.isNullOrEmpty(heatTemperatureEditText.getText().toString());
    }


    public void startHeatingProcess(View view) {
        if(requiredVariablesAreSet()) {
            try {
                startHeatProcessInPLS();
                updateButtonStatuses(BrewProcess.HEAT);
                Toast toast = Toast.makeText(getApplicationContext(), "Koking startet", Toast.LENGTH_SHORT);
                toast.show();
            }catch(PLSConnectionException e){
                Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Sett temperatur først!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void startHeatProcessInPLS(){
        final int heatTemperatureValue = TemperatureService.getPLSFormattedTemperatureInt(heatTemperatureEditText.getText().toString());
        Log.d(this.getClass().getName(), "Heat temp set to: " + heatTemperatureValue);

        controllerService
                .setUrom(1,1)
                .setVar(1, heatTemperatureValue)
                .execute();
    }

    private void stopHeatProcessInPLS(){
        controllerService.setUrom(1, 0).execute();
    }

    private void updateHeatProcessInPLS(){
        final int heatTemperatureValue = TemperatureService.getPLSFormattedTemperatureInt(heatTemperatureEditText.getText().toString());
        Log.d(this.getClass().getName(), "Heat temp set to: " + heatTemperatureValue);

        controllerService.setVar(1, heatTemperatureValue).execute();
    }

    public void stopHeatingProcess(View view) {
        try{
            stopHeatProcessInPLS();
            updateButtonStatuses(BrewProcess.NONE);
            Toast toast = Toast.makeText(getApplicationContext(), "Koking stoppet", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch(PLSConnectionException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
            toast.show();
        }
        startActivity(new Intent(this, MainActivity.class));
    }

    public void updateValuesInView(View view){
        updateValuesFromPLS();
    }

    public void updateHeatTemperature(View view) {

        try{
            updateHeatProcessInPLS();
            Toast toast = Toast.makeText(getApplicationContext(), "Koking oppdatert", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch(PLSConnectionException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
