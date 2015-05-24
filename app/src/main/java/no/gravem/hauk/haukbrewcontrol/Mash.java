package no.gravem.hauk.haukbrewcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by GTG on 20.01.2015.
 */
public class Mash extends Activity{

    Button startButton, stopButton;
    private TextView mashTempBottom, mashTempTop, mashTime;

    private ControllerService controllerService = new ControllerService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mash);

        startButton = (Button)findViewById(R.id.mashStartBtn);
        stopButton = (Button)findViewById(R.id.mashStopBtn);

        mashTempTop = (TextView) findViewById(R.id.mashTempTop);
        mashTempBottom = (TextView) findViewById(R.id.mashTempBottom);
        mashTime = (TextView) findViewById(R.id.mashTime);

        updateValuesFromPLS();
    }

    private void updateValuesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(HttpURLConnection result) {
                try {
                    StatusXml statusXml = new StatusXml(result.getInputStream());
                    setValuesInView(statusXml.getTemp2Value(), statusXml.getTemp3Value(), statusXml.getVar2Value(), BrewProcess.createFrom(statusXml.getUrom1Value()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String t2, final String t3, final String time, final BrewProcess brewProcess){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mashTempTop.setText(t2);
                mashTempBottom.setText(t3);
                mashTime.setText(time);

                if (brewProcess == BrewProcess.MASH) {
                    startButton.setEnabled(false);
                    startButton.setActivated(false);
                    stopButton.setEnabled(true);
                    stopButton.setActivated(true);
                } else {
                    startButton.setEnabled(true);
                    startButton.setActivated(true);
                    stopButton.setEnabled(false);
                    stopButton.setActivated(false);
                }
            }
        });
    }


    public void getCurrentProsessData(View view){
        Log.d(this.getClass().getName(), "updateMashData");

        updateValuesFromPLS();
    }

    public void startMash(View view){
        Log.d(this.getClass().getName(), "startMash");

        startMashProcessInPLS();

        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Mesking startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stopMash(View view){
        Log.d(this.getClass().getName(), "stopMash");
        stopMashProcessInPLS();

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Mesking stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    private String getPLSFormattedTemperatureString(String temperature){
        return temperature.replaceAll(".", "").replaceAll("C", "").trim();
    }

    public void startMashProcessInPLS() {

        String tempTopValue = mashTempTop.getText().toString();
        String tempBottomValue = mashTempBottom.getText().toString();
        String time = mashTime.getText().toString();



        controllerService.setVariable("varid=1&value="+getPLSFormattedTemperatureString(tempTopValue), new ControllerResult() {
            @Override
            public void done(HttpURLConnection result) {

            }
        });

        //Set UROM1=1 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=2)
        controllerService.setUROMVariable("uromid=1&value=2", new ControllerResult() {
            public void done(HttpURLConnection result) {
            }
        });


        //TODO: Legg til oppdateringskode!
        //Sjekk temp settpunkt og tider forskjellig fra 0
        //Set VAR1 = Temp1 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
        //Set VAR2 = Temp2 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
        //Set VAR3 = Temp3 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)
        //Set VAR4 = Tid1 (http://88.84.50.37/api/setvar1.cgi?varid=1&value=xxx) (3600 = 1 t)
        //Set VAR5 = Tid2 (http://88.84.50.37/api/setvar1.cgi?varid=1&value=xxx) (3600 = 1 t)
        //Set VAR6 = Tid3 (http://88.84.50.37/api/setvar1.cgi?varid=1&value=xxx) (3600 = 1 t)
        //Set UROM1=2 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=2)

        Toast toast = Toast.makeText(getApplicationContext(), "Mesking startet", Toast.LENGTH_SHORT);
        toast.show();

    }

    public void stopMashProcessInPLS(){

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        controllerService.setUROMVariable("uromid=1&value=0", new ControllerResult() {
            public void done(HttpURLConnection result) {
            }
        });

        Toast toast = Toast.makeText(getApplicationContext(), "Mesking stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

}
