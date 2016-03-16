package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class Ferment extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();
    ImageButton startButton, stopButton;
    private EditText fermentTemperatureEditText;
    private TextView currentFermentTemperatureTextView, currentFermentTimeTextView, fermentHeading;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ferment);

        startButton = (ImageButton)findViewById(R.id.fermentStartBtn);
        stopButton = (ImageButton)findViewById(R.id.fermentStopBtn);

        fermentHeading = (TextView)findViewById(R.id.fermentHeading);
        fermentTemperatureEditText = (EditText) findViewById(R.id.fermentTemp);
        currentFermentTemperatureTextView = (TextView) findViewById(R.id.currentFermentTemp);
        currentFermentTimeTextView = (TextView) findViewById(R.id.currentFermentTime);

        progressBar = (ProgressBar) findViewById(R.id.fermentProcessDataProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_fermentContainer);
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
        getMenuInflater().inflate(R.menu.menu_ferment, menu);
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

    public void setFermentTemperature(View view){
        //TODO: Trenger vi denne metoden?

        updateValuesFromPLS();
    }

    public void startFermentProcess(View view){

        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        startFermentProcessInPLS();

        Toast toast = Toast.makeText(getApplicationContext(), "Gjæring startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stopFermentProcess(View view){

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        stopFermentProcessInPLS();

        Toast toast = Toast.makeText(getApplicationContext(), "Gjæring stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    private void startFermentProcessInPLS(){
        Log.d(this.getClass().getName(), "Start gjæring!");
        final int fermentTemperatureValue = TemperatureService.getPLSFormattedTemperatureInt(fermentTemperatureEditText.getText().toString());

        controllerService
                .setUrom(1, 4)
                .setVar(1, fermentTemperatureValue)
                .execute();
    }

    private void stopFermentProcessInPLS(){
        //Set UROM1=0 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=0)
        controllerService.setUrom(1, 0).execute();
    }

    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    StatusXml statusXml = new StatusXml(result);
                    setValuesInView(statusXml.getTemp4Value(), statusXml.getProcessRunningTimeInMinutes(), BrewProcess.createFrom(statusXml.getUrom1Value()));
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String tempFerment, final int timeSpent, final BrewProcess brewProcess) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFermentTemperatureTextView.setText(tempFerment);
                currentFermentTimeTextView.setText(timeSpent + " min");

                if(brewProcess == BrewProcess.FERMENT){
                    fermentHeading.setText("Gjæring pågår");
                    stopButton.setEnabled(true);
                    stopButton.setActivated(true);
                    startButton.setEnabled(false);
                    startButton.setActivated(false);
                }
                else{
                    fermentHeading.setText("Gjæring ikke aktiv");
                    startButton.setEnabled(true);
                    startButton.setActivated(true);
                    stopButton.setEnabled(false);
                    stopButton.setActivated(false);
                }
                progressBar.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            }
        });
    }
}
