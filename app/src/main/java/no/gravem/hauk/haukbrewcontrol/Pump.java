package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class Pump extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();

    Button startButton, stopButton;
    private TextView temperatureTop, temperatureBottom, temperatureHeater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);

        startButton = (Button) findViewById(R.id.pumpStartBtn);
        stopButton = (Button) findViewById(R.id.pumpStopBtn);

        temperatureTop = (TextView) findViewById(R.id.tempTopRoste);
        temperatureBottom = (TextView) findViewById(R.id.tempBottomRoste);
        temperatureHeater = (TextView) findViewById(R.id.tempHeater);

        updateValuesFromPLS();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pump, menu);
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

    public void getCurrentProsessData(View view) {
        updateValuesFromPLS();
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

    private void setValuesInView(final String tempTopValue, final String tempBottomValue, final String tempHeaterValue, final BrewProcess brewProcess) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperatureTop.setText(tempTopValue);
                temperatureBottom.setText(tempBottomValue);
                temperatureHeater.setText(tempHeaterValue);

                if (brewProcess == BrewProcess.PUMP) {
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

    public void startPumpProcess(View view) {
        //Start pump task
        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        startPumpProcessInPLS();

        Toast toast = Toast.makeText(getApplicationContext(), "Pumping startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stopPumpProcess(View view) {
        stopPumpProcessInPLS();

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Pumping stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    private void startPumpProcessInPLS() {
        Log.d(this.getClass().getName(), "Start pumping!");
        //Set UROM1=3 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=3)
        controllerService.setUROMVariable("uromid=1&value=3", new ControllerResult() {
            public void done(String result) {
                Log.d(this.getClass().getName(), "Set Var1 to 1");
                controllerService.setVariable("varid=1&value=1");
            }
        });
    }

    private void stopPumpProcessInPLS() {
        //Set UROM1=0 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=0)
        controllerService.setUROMVariable("uromid=1&value=0", new ControllerResult() {
            public void done(String result) {
            }
        });
    }
}
