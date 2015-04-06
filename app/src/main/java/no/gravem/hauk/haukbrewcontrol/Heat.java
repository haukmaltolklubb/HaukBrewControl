package no.gravem.hauk.haukbrewcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

/**
 * Created by GTG on 06.01.2015.
 */
public class Heat extends Activity {

    public HeatTask heatTask;
    ExecuteHeatTask executeHeatTask;
    Button startButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);

        startButton = (Button)findViewById(R.id.heatStartBtn);
        stopButton = (Button)findViewById(R.id.heatStopBtn);

        heatTask = new HeatTask();
        heatTask.execute();
        executeHeatTask = new ExecuteHeatTask();
        executeHeatTask.execute("");
    }

    public void getCurrentProsessData(View view){
        //Temp fra t1
        //Tid fra RAM9
        heatTask.updateValues();
    }

    public void startHeatingProcess(View view){
        //Start heating task

        //executeHeatTask.startHeating();

        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Koking startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stopHeatingProcess(View view){
        //Stop heating task
        executeHeatTask = new ExecuteHeatTask();
        executeHeatTask.execute("");
        executeHeatTask.stopHeating();

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Koking stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    public void updateHeatTemperature(View view){
        heatTask.updateHeatTemperature();

    }

    private class HeatTask extends AsyncTask<String, Integer, String> {

        HaukBrewControlApplication myApp;
        BrewControlOperations operations = null;

        @Override
        protected String doInBackground(String... params) {
            myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            updateValues();
        }



        public void updateHeatTemperature(){
            Log.d(this.getClass().getName(), "Set heat temperature");
            EditText tempValueView = (EditText)findViewById(R.id.heatTemp);
            String tempValue = tempValueView.getText().toString();
            Log.d(this.getClass().getName(), "Temp set to: " + tempValue);

            //Set var1
            //TODO: Set var1
        }

        private void updateValues(){
            String temp1 = operations.getNodeValue("ts1");
            String time = operations.getNodeValue("var2");

            updateButtonStatus();

            TextView heatTempText = (TextView) findViewById(R.id.currentHeatTemp);
            heatTempText.setText(temp1);

            TextView heatTimeText = (TextView) findViewById(R.id.currentHeatTime);
            heatTimeText.setText(getTimeFromCounter(time));
        }

        private void updateButtonStatus() {
            String brewProcessValue = operations.getNodeValue("urom1");
            BrewProcess brewProcess = operations.getBrewProcess(brewProcessValue);
            if(brewProcess == BrewProcess.Heat){
                Log.d(this.getClass().getName(), "Heat in progress. Disabling START");
                stopButton.setEnabled(true);
                stopButton.setActivated(true);
                startButton.setEnabled(false);
                startButton.setActivated(false);
            }
            else{
                Log.d(this.getClass().getName(), "Heat not in progress. Enabling START");
                startButton.setEnabled(true);
                startButton.setActivated(true);
                stopButton.setEnabled(false);
                stopButton.setActivated(false);
            }
        }

        private String getTimeFromCounter(String time){
            //TODO: Regn om tid fra sekunder fra år 2000 til nå. Husk sommertid/vintertid
            return "NA";
        }
    }

    private class ExecuteHeatTask extends AsyncTask<String, Integer, String>{

        HaukBrewControlApplication myApp;

        @Override
        protected String doInBackground(String... params) {
            myApp = (HaukBrewControlApplication) getApplication();
            return null;
        }

        private void startHeating(){
            Log.d(this.getClass().getName(), "Start heating!");
            if(myApp == null){
                Log.d(this.getClass().getName(), "myApp is null!");
            }
            IBrewControlConnection brewControlConnection = myApp.getBrewControlConnection();
            try {
                //TODO: Legg til oppdateringskode for koking!
                //Sjekk temp settpunkt forskjellig fra 0
                //Set VAR1 = Temp (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
                //Set UROM1=1 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=1)
                EditText tempValueView = (EditText)findViewById(R.id.heatTemp);
                String tempValue = tempValueView.getText().toString();
                //if(!Strings.isNullOrEmpty(tempValue)){
                Log.d(this.getClass().getName(), "Temp is: " + tempValue);
                //String queryString = "setvar.cgi?varid=1&value="+tempValue;
                String queryString = "seturom.cgi?uromid=1&value=3";
                brewControlConnection.postURLRequest(queryString);
                Log.d(this.getClass().getName(), "Satt urom til 3.");
                //}

            } catch (Exception e) {
                Log.e(this.getClass().getName(), "Error during start heating: " + e.getMessage(), e);
            }
        }

        private void stopHeating(){
            Log.d(this.getClass().getName(), "Stop heating!");

            //TODO: Legg til oppdateringskode for koking!
            // Set UROM1=0 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=0)
        }
    }
}
