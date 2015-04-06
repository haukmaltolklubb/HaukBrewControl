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
import android.widget.TextView;
import android.widget.Toast;


public class Pump extends ActionBarActivity {

    BrewControlOperations operations = null;
    PumpTask pumpTask;
    Button startButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);

        startButton = (Button)findViewById(R.id.pumpStartBtn);
        stopButton = (Button)findViewById(R.id.pumpStopBtn);

        pumpTask = new PumpTask();
        pumpTask.execute("");
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

    public void getCurrentProsessData(View view){
        pumpTask.updateValues();
    }

    public void startPumpProcess(View view){
        //Start pump task
        pumpTask.startPumping();

        stopButton.setEnabled(true);
        stopButton.setActivated(true);
        startButton.setEnabled(false);
        startButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Pumping startet", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stopPumpProcess(View view){
        //Stop heating task
        pumpTask.stopPumping();
        startButton.setEnabled(true);
        startButton.setActivated(true);
        stopButton.setEnabled(false);
        stopButton.setActivated(false);

        Toast toast = Toast.makeText(getApplicationContext(), "Pumping stoppet", Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this, MainActivity.class));
    }

    private class PumpTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());

            updateValues();
            return null;
        }

        private void updateValues(){
            String tempTopValue = operations.getNodeValue("ts1");
            String tempBottomValue = operations.getNodeValue("ts2");
            String tempHeaterValue = operations.getNodeValue("ts3");

            TextView tempTop = (TextView) findViewById(R.id.tempTopRoste);
            tempTop.setText(tempTopValue);

            TextView tempBottom = (TextView)findViewById(R.id.tempBottomRoste);
            tempBottom.setText(tempBottomValue);

            TextView tempHeater = (TextView)findViewById(R.id.tempHeater);
            tempHeater.setText(tempHeaterValue);

            updateButtonStatus();
        }

        private void updateButtonStatus() {
            String brewProcessValue = operations.getNodeValue("urom1");
            BrewProcess brewProcess = operations.getBrewProcess(brewProcessValue);
            if(brewProcess == BrewProcess.Pump){
                Log.d(this.getClass().getName(), "Pump in progress. Disabling START");
                stopButton.setEnabled(true);
                stopButton.setActivated(true);
                startButton.setEnabled(false);
                startButton.setActivated(false);
            }
            else{
                Log.d(this.getClass().getName(), "Pump not in progress. Enabling START");
                startButton.setEnabled(true);
                startButton.setActivated(true);
                stopButton.setEnabled(false);
                stopButton.setActivated(false);
            }
        }

        private void startPumping(){
            Log.d(this.getClass().getName(), "startPumping!");
            //TODO: Start pump process!
            //Set UROM1=3 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=3)

            updateValues();
        }

        private void stopPumping(){
            Log.d(this.getClass().getName(), "stopPumping!");
            //TODO: Stop pump process
            //Set UROM1=0 (http://88.84.50.37/api/seturom.cgi?uromid=1&value=0)
        }
    }
}
