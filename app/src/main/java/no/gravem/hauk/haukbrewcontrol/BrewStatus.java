package no.gravem.hauk.haukbrewcontrol;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;

public class BrewStatus extends ActionBarActivity {

    private ControllerService controllerService = new ControllerService();
    private TextView t1TextView, t2TextView, t3TextView, t4TextView, processTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brew_status);

        this.t1TextView = (TextView) findViewById(R.id.temp1);
        this.t2TextView = (TextView) findViewById(R.id.temp2);
        this.t3TextView = (TextView) findViewById(R.id.temp3);
        this.t4TextView = (TextView) findViewById(R.id.temp4);
        this.processTextView = (TextView) findViewById(R.id.currentProcess);

        updateFromPLS();
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

    public void updateFromPLS() {
        updateTemperaturesFromPLS();
        updateCurrentProcessFromPLS();
    }

    private void setTemperaturesInView(final String t1, final String t2, final String t3, final String t4) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t1TextView.setText(t1);
                t2TextView.setText(t2);
                t3TextView.setText(t3);
                t4TextView.setText(t4);
            }
        });
    }

    private void updateTemperaturesFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(HttpURLConnection result) {
                try {
                    StatusXml statusXml = new StatusXml(result.getInputStream());
                    setTemperaturesInView(statusXml.getTemp1Value(), statusXml.getTemp2Value(), statusXml.getTemp3Value(), statusXml.getTemp4Value());
                } catch (IOException e) {
                    // TODO: Vis feilmelding.
                    e.printStackTrace();
                }
            }
        });
    }

    private void setProcessInView(final BrewProcess process){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processTextView.setText(BrewProcess.getProcessText(process));
            }
        });
    }

    private void updateCurrentProcessFromPLS() {

        controllerService.getStatusXml(new ControllerResult() {

            @Override
            public void done(HttpURLConnection result) {
                try {
                    StatusXml statusXml = new StatusXml(result.getInputStream());
                    BrewProcess brewProcess = BrewProcess.createFrom(statusXml.getUrom1Value());
                    setProcessInView(brewProcess);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCurrentProsessData(View view) {
        updateTemperaturesFromPLS();
    }
}
