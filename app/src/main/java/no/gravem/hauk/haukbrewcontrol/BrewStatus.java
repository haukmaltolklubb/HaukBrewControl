package no.gravem.hauk.haukbrewcontrol;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class BrewStatus extends ActionBarActivity {

    private String sensorUrl = "http://88.84.50.37/api/status.xml";
    BrewStatusTask brewStatusTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brew_status);

        getStatusFromPLS();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_brew_status, menu);
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

    public void getStatusFromPLS(){
        brewStatusTask = new BrewStatusTask();
        brewStatusTask.execute("");
    }

    public void updateValues(View view){
        brewStatusTask.getTemperatures();
    }

    private class BrewStatusTask extends AsyncTask<String, Integer, String> {

        IBrewControlOperations operations = null;
        private String tempIkkeFunnet = "Temperatur ikke funnet";

        @Override
        protected String doInBackground(String... params) {

            HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());

            //operations = new BrewControlOperationsMockup();
            //myApp.setXmlDocument(operations.initXmlDocument());
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            getTemperatures();
            getCurrentProcess();
        }

        private void getCurrentProcess(){
            String urom1 = operations.getNodeValue("urom1");
            BrewProcess brewProcess = getBrewProcess(urom1);

            TextView currentProcess = (TextView)findViewById(R.id.currentProcess);

            switch (brewProcess){
                case None:
                    currentProcess.setText(BrewProcess.None.toString());
                    break;
                case Heat:
                    currentProcess.setText(BrewProcess.Heat.toString());
                    break;
                case Mash:
                    currentProcess.setText(BrewProcess.Mash.toString());
                    break;
                case Pump:
                    currentProcess.setText(BrewProcess.Pump.toString());
                    break;
                case Ferment:
                    currentProcess.setText(BrewProcess.Ferment.toString());
                    break;
                default:
                    currentProcess.setText(BrewProcess.None.toString());
            }

        }

        private void getTemperatures(){

            String temp1 = operations.getNodeValue("ts1");
            String temp2 = operations.getNodeValue("ts2");
            String temp3 = operations.getNodeValue("ts3");
            String temp4 = operations.getNodeValue("ts4");

            getTemperature(temp1, R.id.temp1);
            getTemperature(temp2, R.id.temp2);
            getTemperature(temp3, R.id.temp3);
            getTemperature(temp4, R.id.temp4);
        }

        private void getTemperature(String nodeValue, int textViewId){
            TextView editText = (TextView) findViewById(textViewId);
            editText.setText(nodeValue);
        }

        public  BrewProcess getBrewProcess(String brewProcess){
            int brewProcessInt = Integer.parseInt(brewProcess);
            if(brewProcessInt == 0)
                return BrewProcess.None;
            if(brewProcessInt == 1)
                return BrewProcess.Heat;
            if(brewProcessInt == 2)
                return BrewProcess.Mash;
            if(brewProcessInt == 3)
                return BrewProcess.Pump;
            if(brewProcessInt == 4)
                return  BrewProcess.Ferment;
            return BrewProcess.None;
        }

    }
}
