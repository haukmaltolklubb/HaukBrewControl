package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.net.HttpURLConnection;

import static no.gravem.hauk.haukbrewcontrol.BrewProcess.NONE;


public class MainActivity extends ActionBarActivity {

    ControllerService controllerService = new ControllerService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateCurrentProcessFromPLS();
    }

    private void startProcessActivity(final BrewProcess brewProcess){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(brewProcess != NONE) {
                    startActivity(new Intent(getApplicationContext(), brewProcess.getActivityClass()));
                }
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
                    startProcessActivity(brewProcess);
                } catch (IOException e) {
                    e.printStackTrace();
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

    public void openStatusActivity(View view){
        startActivity(new Intent(this, BrewStatus.class));
    }

    public void openStartBrewActivity(View view){startActivity(new Intent(this, StartBrew.class)); }


}
