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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GTG on 20.01.2015.
 */
public class Mash extends ActionBarActivity {

    Button startButton, stopButton;
    private TextView mashTempBottom, mashTempTop, mashTime;
    private EditText level1Temperature, level1Time, level2Temperature, level2Time, level3Temperature, level3Time;


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

        level1Temperature = (EditText) findViewById(R.id.mashLevel1Temperature);
        level1Time = (EditText)findViewById(R.id.mashLevel1Time);
        level2Temperature = (EditText)findViewById(R.id.mashLevel2Temperature);
        level2Time = (EditText)findViewById(R.id.mashLevel2Time);
        level3Temperature = (EditText)findViewById(R.id.mashLevel3Temperature);
        level3Time = (EditText)findViewById(R.id.mashLevel3Time);

        updateValuesFromPLS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mash, menu);
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
                    setValuesInView(statusXml.getTemp2Value(), statusXml.getTemp3Value(), statusXml.getProcessRunningTimeInMinutes(), BrewProcess.createFrom(statusXml.getUrom1Value()));
                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValuesInView(final String t2, final String t3, final int time, final BrewProcess brewProcess){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mashTempTop.setText(t2);
                mashTempBottom.setText(t3);
                mashTime.setText(time + " min");

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
        if(requiredVariablesAreSet()) {
            Log.d(this.getClass().getName(), "startMash");

            startMashProcessInPLS();

            stopButton.setEnabled(true);
            stopButton.setActivated(true);
            startButton.setEnabled(false);
            startButton.setActivated(false);

            Toast toast = Toast.makeText(getApplicationContext(), "Mesking startet", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Mangler påkrevde verdier", Toast.LENGTH_SHORT);
            toast.show();
        }
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

    private String getPLSFormattedTemperatureString(EditText temperature){
        return temperature.getText().toString().replace(".", "");
    }

    private String getPLSFormattedTimeString(EditText time){
        //time angis i minutt. PLS ønsker sekunder.
        return String.valueOf(Integer.valueOf(time.getText().toString())*60);
    }

    private boolean requiredVariablesAreSet(){
        return !Strings.isNullOrEmpty(level1Temperature.getText().toString()) &&
                !Strings.isNullOrEmpty(level1Time.getText().toString()) &&
                !Strings.isNullOrEmpty(level2Temperature.getText().toString()) &&
                !Strings.isNullOrEmpty(level2Time.getText().toString()) &&
                !Strings.isNullOrEmpty(level3Temperature.getText().toString()) &&
                !Strings.isNullOrEmpty(level3Time.getText().toString());
    }

    public void startMashProcessInPLS() {
        //Set VAR1 = Temp1 (http://88.84.50.37/api/setvar.cgi?varid=1&value=xxx) (999 = 99,9°C)

        //Set UROM1=2 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=2)
        controllerService.setUROMVariable(1, 2, new ControllerResult() {
            public void done(String result) {
                List<String> variablesToSet = new ArrayList<String>();
                variablesToSet.add("varid=1&value=" + getPLSFormattedTemperatureString(level1Temperature));
                variablesToSet.add("varid=3&value=" + getPLSFormattedTemperatureString(level2Temperature));
                variablesToSet.add("varid=4&value=" + getPLSFormattedTemperatureString(level3Temperature));
                variablesToSet.add("varid=5&value=" + getPLSFormattedTimeString(level1Time));
                variablesToSet.add("varid=6&value=" + getPLSFormattedTimeString(level2Time));
                variablesToSet.add("varid=7&value=" + getPLSFormattedTimeString(level3Time));

                controllerService.setVariables(variablesToSet);

                //TODO: Create self-returning method for setting multiple variables
               // controllerService
                 //       .setVariables()
                   //     .setVar(1, getPLSFormattedTemperatureString(level1Temperature))
                     //   .setVar(2, getPLSFormattedTemperatureString(level1Temperatu))
                       // .setVar(3, )=

            }
        });
    }

    public void stopMashProcessInPLS(){

        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        controllerService.setUROMVariable(1, 0, new ControllerResult() {
            public void done(String result) {}
        });
    }
}
