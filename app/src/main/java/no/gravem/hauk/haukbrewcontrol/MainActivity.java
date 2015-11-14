package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();
    private TextView currentProcessTextView;
    private ImageButton startNewBrewButton;
    BrewProcess brewProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brewProcess = BrewProcess.NONE;
        currentProcessTextView = (TextView) findViewById(R.id.currentProcess);
        startNewBrewButton = (ImageButton)findViewById(R.id.startNewBrewBtn);
        updateCurrentProcessFromPLS();
    }


    private void updateCurrentProcessFromPLS() {
        controllerService.getStatusXml(new ControllerResult() {
            @Override
            public void done(String result) {
                try {
                    StatusXml statusXml = new StatusXml(result);
                    brewProcess = BrewProcess.createFrom(statusXml.getUrom1Value());
                    setProcessInView();

                } catch (PLSConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setProcessInView(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startNewBrewButton.setEnabled(false);
                currentProcessTextView.setText(BrewProcess.getProcessText(brewProcess));
                if (brewProcess.equals(BrewProcess.NONE)) {
                    startNewBrewButton.setEnabled(true);
                    startNewBrewButton.setActivated(true);
                } else {
                    startNewBrewButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void openCurrentActivity(View view){
        if (brewProcess.equals(BrewProcess.HEAT))
            startActivity(new Intent(this, Heat.class));
        else if (brewProcess.equals(BrewProcess.MASH))
            startActivity(new Intent(this, Mash.class));
        else if (brewProcess.equals(BrewProcess.FERMENT))
            startActivity(new Intent(this, Ferment.class));
        else if (brewProcess.equals(BrewProcess.PUMP))
            startActivity(new Intent(this, Pump.class));
        else {
            startActivity(new Intent(this, BrewStatus.class));
        }
    }

    public void openStartBrewActivity(View view) {
        startActivity(new Intent(this, StartBrew.class));
    }
}
