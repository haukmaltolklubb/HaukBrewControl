package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Ferment extends ActionBarActivity {

    FermentTask fermentTask;
    Button startButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ferment);

        startButton = (Button)findViewById(R.id.fermentStartBtn);
        stopButton = (Button)findViewById(R.id.fermentStopBtn);

        fermentTask = new FermentTask();
        fermentTask.execute("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ferment, menu);
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

    public void setFermentTemperature(View view){
        //TODO: Set ferment temp!
        fermentTask.updateValues();
    }

    public void startFermentProcess(View view){
        fermentTask.startFermenting();

        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Gjæring startet", Toast.LENGTH_SHORT);
        toast.show();
    }


    public void stopFermentProcess(View view){
        fermentTask.stopFermenting();
        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Gjæring stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    public void getCurrentProsessData(View view){
        Log.d(this.getClass().getName(), "updateFermentData");
        fermentTask.updateValues();
    }

    private class FermentTask extends AsyncTask<String, Integer, String> {

        BrewControlOperations operations = null;

        @Override
        protected String doInBackground(String... params) {
            HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());
            return null;
        }

        private void startFermenting(){
            Log.d(this.getClass().getName(), "Start fermenting!");
            //TODO: Legg til kode for oppstart Gjæring
            //Sjekk temp settpunkt forskjellig fra 0
            //Set VAR1 = Temp1 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
            //Set UROM1=4 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=4
            updateValues();
        }

        private void stopFermenting(){
            Log.d(this.getClass().getName(), "Stop fermenting!");
            //TODO: Legg til kode for stop av Gjæringsprosess
            //Set UROM1=0 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=0)
        }

        private void updateValues(){
            //TODO: Update values here!
            Log.d(this.getClass().getName(), "updating Ferment values....");

            String tempFerment = operations.getNodeValue("ts4");
            String timeSpent = operations.getNodeValue("var2");

            updateButtonStatus();
        }

        private void updateButtonStatus() {
            String brewProcessValue = operations.getNodeValue("urom1");
            BrewProcess brewProcess = operations.getBrewProcess(brewProcessValue);
            if(brewProcess == BrewProcess.Ferment){
                Log.d(this.getClass().getName(), "Ferment in progress. Disabling START");
                stopButton.setEnabled(true);
                stopButton.setActivated(true);
                startButton.setEnabled(false);
                startButton.setActivated(false);
            }
            else{
                Log.d(this.getClass().getName(), "Ferment not in progress. Enabling START");
                startButton.setEnabled(true);
                startButton.setActivated(true);
                stopButton.setEnabled(false);
                stopButton.setActivated(false);
            }
        }
    }
}
