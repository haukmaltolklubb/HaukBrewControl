package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Pump extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();

    ImageButton startButton, stopButton;
    private TextView temperatureTop, temperatureBottom, temperatureHeater, pumpHeading;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);

        startButton = (ImageButton) findViewById(R.id.pumpStartBtn);
        stopButton = (ImageButton) findViewById(R.id.pumpStopBtn);

        pumpHeading = (TextView)findViewById(R.id.pumpHeading);
        temperatureTop = (TextView) findViewById(R.id.tempTopRoste);
        temperatureBottom = (TextView) findViewById(R.id.tempBottomRoste);
        temperatureHeater = (TextView) findViewById(R.id.tempHeater);
        progressBar = (ProgressBar) findViewById(R.id.pumpProcessDataProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_pumpContainer);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                updateValuesFromPLS();
            }
        });


        updateValuesFromPLS();
    }

    /*
 * Listen for option item selections so that we receive a notification
 * when the user requests a refresh by selecting the refresh action bar item.
 */
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

        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pump, menu);
        return true;
    }


    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    StatusXml statusXml = new StatusXml(result);
                    setValuesInView(statusXml.getTemp1Value(), statusXml.getTemp2Value(), statusXml.getTemp3Value(), BrewProcess.createFrom(statusXml.getUrom1Value()));
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateButtonStatuses(BrewProcess currentProcess){
        if (currentProcess == BrewProcess.PUMP) {
            pumpHeading.setText("Pumping pågår");
            startButton.setEnabled(false);
            startButton.setActivated(false);
            stopButton.setEnabled(true);
            stopButton.setActivated(true);
        } else {
            pumpHeading.setText("Pumping ikke aktiv");
            startButton.setEnabled(true);
            startButton.setActivated(true);
            stopButton.setEnabled(false);
            stopButton.setActivated(false);
        }
    }

    private void setValuesInView(final String tempTopValue, final String tempBottomValue, final String tempHeaterValue, final BrewProcess brewProcess) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperatureTop.setText(tempTopValue);
                temperatureBottom.setText(tempBottomValue);
                temperatureHeater.setText(tempHeaterValue);

                updateButtonStatuses(brewProcess);

                progressBar.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            }
        });
    }

    public void startPumpProcess(View view) {

        try{
            startPumpProcessInPLS();
            updateButtonStatuses(BrewProcess.PUMP);
            Toast toast = Toast.makeText(getApplicationContext(), "Pumping startet", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch(PLSConnectionException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void stopPumpProcess(View view) {
        try{
            stopPumpProcessInPLS();

            updateButtonStatuses(BrewProcess.NONE);
            Toast toast = Toast.makeText(getApplicationContext(), "Pumping stoppet", Toast.LENGTH_SHORT);
            toast.show();

            startActivity(new Intent(this, MainActivity.class));
        }
        catch(PLSConnectionException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void startPumpProcessInPLS() {
        Log.d(this.getClass().getName(), "Start pumping!");

        controllerService
                .setUrom(1,3)
                .setVar(1,1)
                .execute();
    }

    private void stopPumpProcessInPLS() {
        controllerService.setUrom(1, 0).execute();
    }
}
