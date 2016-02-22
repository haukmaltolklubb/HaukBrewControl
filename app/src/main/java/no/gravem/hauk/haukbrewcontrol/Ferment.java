package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Ferment extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();
    Button startButton, stopButton;
    private EditText fermentTemperatureEditText;
    private TextView currentFermentTemperatureTextView, currentFermentTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ferment);

        startButton = (Button)findViewById(R.id.fermentStartBtn);
        stopButton = (Button)findViewById(R.id.fermentStopBtn);

        fermentTemperatureEditText = (EditText) findViewById(R.id.fermentTemp);
        currentFermentTemperatureTextView = (TextView) findViewById(R.id.currentFermentTemp);
        currentFermentTimeTextView = (TextView) findViewById(R.id.currentFermentTime);

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

    public void getCurrentProsessData(View view){
        updateValuesFromPLS();
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
                    Log.d(this.getClass().getName(), "FERMENT in progress. Disabling START");
                    stopButton.setEnabled(true);
                    stopButton.setActivated(true);
                    startButton.setEnabled(false);
                    startButton.setActivated(false);
                }
                else{
                    Log.d(this.getClass().getName(), "FERMENT not in progress. Enabling START");
                    startButton.setEnabled(true);
                    startButton.setActivated(true);
                    stopButton.setEnabled(false);
                    stopButton.setActivated(false);
                }
            }
        });
    }
}
