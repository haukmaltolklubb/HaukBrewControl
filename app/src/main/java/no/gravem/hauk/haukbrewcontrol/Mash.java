package no.gravem.hauk.haukbrewcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by GTG on 20.01.2015.
 */
public class Mash extends Activity{

    MashTask mashTask;
    Button startButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mash);

        startButton = (Button)findViewById(R.id.mashStartBtn);
        stopButton = (Button)findViewById(R.id.mashStopBtn);

        mashTask = new MashTask();
        mashTask.execute("");
    }

    public void getCurrentProsessData(View view){
        Log.d(this.getClass().getName(), "updateMashData");

        mashTask.updateValues();
    }

    public void startMash(View view){
        Log.d(this.getClass().getName(), "startMash");
        mashTask.startMashProcess();

        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Mesking startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stopMash(View view){
        Log.d(this.getClass().getName(), "stopMash");
        mashTask.stopMashProcess();

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Mesking stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    private class MashTask extends AsyncTask<String, Integer, String> {
        BrewControlOperations operations = null;

        @Override
        protected String doInBackground(String... params) {
            HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            updateValues();
        }

        public void startMashProcess() {
            //TODO: Legg til oppdateringskode!
            //Sjekk temp settpunkt og tider forskjellig fra 0
            //Set VAR1 = Temp1 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
            //Set VAR2 = Temp2 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
            //Set VAR3 = Temp3 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
            //Set VAR4 = Tid1 (http://88.84.50.37/api/setvar1.cgi?varid=1&value=xxx) (3600 = 1 t)
            //Set VAR5 = Tid2 (http://88.84.50.37/api/setvar1.cgi?varid=1&value=xxx) (3600 = 1 t)
            //Set VAR6 = Tid3 (http://88.84.50.37/api/setvar1.cgi?varid=1&value=xxx) (3600 = 1 t)
            //Set UROM1=2 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=2)

            updateValues();
        }

        public void stopMashProcess(){
            //TODO: Legg til oppdateringskode!
            //Set UROM1=0 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=0)
            //Gå til hovedmeny

        }

        private void updateValues(){
            //TODO: Update values here!
            Log.d(this.getClass().getName(), "updating Mash values....");

            String tempTop = operations.getNodeValue("ts2");
            String tempBottom = operations.getNodeValue("ts3");
            String time = operations.getNodeValue("var2");

            TextView mashTempTop = (TextView) findViewById(R.id.mashTempTop);
            mashTempTop.setText(tempTop);

            TextView mashTempBottom = (TextView) findViewById(R.id.mashTempBottom);
            mashTempBottom.setText(tempBottom);

            TextView mashTime = (TextView) findViewById(R.id.mashTime);
            mashTime.setText("NA");

            updateButtonStatus();
        }

        private void updateButtonStatus() {
            String brewProcessValue = operations.getNodeValue("urom1");
            BrewProcess brewProcess = operations.getBrewProcess(brewProcessValue);

            if(brewProcess == BrewProcess.Mash){
                Log.d(this.getClass().getName(), "Mash in progress. Disabling START");
                stopButton.setEnabled(true);
                stopButton.setActivated(true);
                startButton.setEnabled(false);
                startButton.setActivated(false);
            }
            else{
                Log.d(this.getClass().getName(), "Mash not in progress. Enabling START");
                startButton.setEnabled(true);
                startButton.setActivated(true);
                stopButton.setEnabled(false);
                stopButton.setActivated(false);
            }
        }

    }
}
