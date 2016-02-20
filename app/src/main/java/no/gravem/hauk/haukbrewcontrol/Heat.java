package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private Button startButton, stopButton;
    private TextView heatTempText, heatTimeText, startTimeText;
    private EditText heatTemperatureEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);

        startButton = (Button)findViewById(R.id.heatStartBtn);
        stopButton = (Button)findViewById(R.id.heatStopBtn);

        heatTempText = (TextView) findViewById(R.id.currentHeatTemp);
        heatTimeText = (TextView) findViewById(R.id.currentHeatTime);
        //startTimeText = (TextView) findViewById(R.id.startTime);

        heatTemperatureEditText = (EditText) findViewById(R.id.mashLevel1Temperature);

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

    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    StatusXml statusXml = new StatusXml(result);
                    setValuesInView(statusXml.getTemp1Value(), statusXml.getProcessRunningTimeInMinutes(), statusXml.getUrom2Value());
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String temp1Value, final int time, final String startTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                heatTempText.setText(temp1Value);
                heatTimeText.setText(time + " min");
                //startTimeText.setText(getTimeFromCounter(startTime));
            }
        });
    }

    public void getCurrentProsessData(View view) {
        //Temp fra t1
        //Tid fra RAM9
        updateValuesFromPLS();
    }

    private String getTimeFromCounter(String time) {
        Log.d(this.getClass().getName(), "Start time is: " + time);
        long milliSeconds = Long.valueOf(time)*1000;

        DateTime zeroPointTime = new DateTime(2000, 1, 1, 0, 0);

        Duration duration = new Duration(milliSeconds);
        //duration.

        //TODO: Duration does not work


        return "time";

        //return duration.toString();
    }

    private String getPLSFormattedTemperatureString(String temperature){
        String formattedTemp = temperature.replace(".", "");
        if(formattedTemp.length()<=2)
            return formattedTemp+"0";
        return  formattedTemp;
    }

    public void startHeatingProcess(View view) {
        if(requiredVariablesAreSet()) {
            startButton.setEnabled(false);
            startButton.setActivated(false);

            stopButton.setEnabled(true);
            stopButton.setActivated(true);

            try {
                startHeatProcessInPLS();
            }catch(PLSConnectionException e){
                Toast toast = Toast.makeText(getApplicationContext(), "Fikk ikke kontakt med PLS", Toast.LENGTH_SHORT);
                toast.show();
            }

            Toast toast = Toast.makeText(getApplicationContext(), "Koking startet", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Sett temperatur først!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean requiredVariablesAreSet(){
        Log.d(this.getClass().getName(), "Heat temp is: " + heatTemperatureEditText.getText().toString());
        return !Strings.isNullOrEmpty(heatTemperatureEditText.getText().toString());
    }

    private void startHeatProcessInPLS(){
        final String heatTemperatureValue = getPLSFormattedTemperatureString(heatTemperatureEditText.getText().toString());

        Log.d(this.getClass().getName(), "Heat temp set to: " + heatTemperatureValue);
        controllerService.setUrom(1, 1, new ControllerResult() {
            public void done(String result) {
                controllerService.setVariable("varid=1&value=" + heatTemperatureValue);
            }
        });
    }

    private void stopHeatProcessInPLS(){
        controllerService.setUrom(1, 0, new ControllerResult() {
            public void done(String result) {
            }
        });
    }

    public void stopHeatingProcess(View view) {
        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        stopHeatProcessInPLS();

        Toast toast = Toast.makeText(getApplicationContext(), "Koking stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    public void updateHeatTemperature(View view) {

        //TODO: Trenger vi denne metoden?
        updateValuesFromPLS();
    }
}
