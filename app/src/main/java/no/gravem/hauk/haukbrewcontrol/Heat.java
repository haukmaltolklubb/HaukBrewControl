package no.gravem.hauk.haukbrewcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by GTG on 06.01.2015.
 */
public class Heat extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();

    private Button startButton, stopButton;
    private TextView heatTempText, heatTimeText;
    private EditText heatTemperatureEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);

        startButton = (Button)findViewById(R.id.heatStartBtn);
        stopButton = (Button)findViewById(R.id.heatStopBtn);

        heatTempText = (TextView) findViewById(R.id.currentHeatTemp);
        heatTimeText = (TextView) findViewById(R.id.currentHeatTime);

        heatTemperatureEditText = (EditText) findViewById(R.id.heatTemp);

        updateValuesFromPLS();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_heat, menu);
        return true;
    }

    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(HttpURLConnection result) {
                try {
                    StatusXml statusXml = new StatusXml(result.getInputStream());
                    setValuesInView(statusXml.getTemp1Value(), statusXml.getVar2Value());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String temp1Value, final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                heatTempText.setText(temp1Value);
                heatTimeText.setText(getTimeFromCounter(time));
            }
        });
    }

    public void getCurrentProsessData(View view) {
        //Temp fra t1
        //Tid fra RAM9
        updateValuesFromPLS();
    }

    private String getTimeFromCounter(String time) {
        //TODO: Regn om tid fra sekunder fra år 2000 til nå. Husk sommertid/vintertid
        //DateTime timeSet = new DateTime(year, month + 1, day, hour, minute);
        DateTime zeroPointTime = new DateTime(2000, 1, 1, 0, 0);
        //Duration duration = new Duration(zeroPointTime, time);
        //int secondsSince2000 = (int) duration.getStandardSeconds();

        return "NA";
    }

    private String getPLSFormattedTemperatureString(String temperature){
        return temperature.replace(".", "");
    }

    public void startHeatingProcess(View view) {
        startButton.setEnabled(false);
        startButton.setActivated(false);

        stopButton.setEnabled(true);
        stopButton.setActivated(true);

        startHeatProcessInPLS();

        Toast toast = Toast.makeText(getApplicationContext(), "Koking startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void startHeatProcessInPLS(){
        //Set VAR1 = Temp (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
        String heatTemperatureValue = getPLSFormattedTemperatureString(heatTemperatureEditText.getText().toString());

        Log.d(this.getClass().getName(), "Heat temp set to: " + heatTemperatureValue);
        controllerService.setVariable("varid=1&value=" + heatTemperatureValue, new ControllerResult() {
            @Override
            public void done(HttpURLConnection result) {
            }
        });
        //Set UROM1=1 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=1)
        controllerService.setUROMVariable("uromid=1&value=1", new ControllerResult() {
            public void done(HttpURLConnection result) {
            }
        });
    }

    private void stopHeatProcessInPLS(){
        controllerService.setUROMVariable("uromid=1&value=0", new ControllerResult() {
            public void done(HttpURLConnection result) {
            }
        });

        controllerService.setVariable("varid=1&value=0", new ControllerResult() {
            @Override
            public void done(HttpURLConnection result) {
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
