package no.gravem.hauk.haukbrewcontrol;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by GTG on 06.01.2015.
 */
public class Heat extends Activity {

    public HeatTask heatTask;
    Button heatStartButton;
    Button heatStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);

        heatStartButton = (Button) findViewById(R.id.heatStartBtn);
        heatStopButton = (Button)findViewById(R.id.heatStopBtn);

        heatTask = new HeatTask();
        heatTask.execute();
    }

    public void getCurrentProsessData(View view){
        //Temp fra t1
        //Tid fra RAM9
        heatTask.updateValues();
    }

    public void startHeatingProcess(View view){
        //Start heating task

        //Disable button
        heatStartButton.setEnabled(false);
        heatStopButton.setEnabled(true);
    }

    public void stopHeatingProcess(View view){
        //Stop heating task

        //Disable button
        heatStartButton.setEnabled(true);
        heatStopButton.setEnabled(false);
    }

    private class HeatTask extends AsyncTask<String, Integer, String> {

        BrewControlOperations operations = null;

        @Override
        protected String doInBackground(String... params) {

            HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            updateValues();
        }

        private void updateValues(){
            String temp1 = operations.getNodeValue("ts1");
            String time = operations.getNodeValue("counter");

            TextView heatTempText = (TextView) findViewById(R.id.currentHeatTemp);
            heatTempText.setText(temp1);

            TextView heatTimeText = (TextView) findViewById(R.id.currentHeatTime);
            heatTimeText.setText(time);
        }
    }
}
