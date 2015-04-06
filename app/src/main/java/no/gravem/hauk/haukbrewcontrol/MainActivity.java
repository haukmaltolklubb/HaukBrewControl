package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.w3c.dom.Document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity {

    BrewControlOperations operations = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityTask activityTask = new MainActivityTask();
        activityTask.execute("");
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

    private class MainActivityTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();
            operations = new BrewControlOperations(myApp.getXmlDocument());

            String urom1 = operations.getNodeValue("urom1");
            BrewProcess brewProcess = operations.getBrewProcess(urom1);

            Log.d(this.getClass().getName(), "BrewProcess: " + brewProcess);

            switch (brewProcess){
                case None:
                    //startActivity(new Intent(this, BrewStatus.class));
                    break;
                case Heat:
                    startActivity(new Intent(getApplicationContext(), Heat.class));
                    break;
                case Mash:
                    startActivity(new Intent(getApplicationContext(), Mash.class));
                    break;
                case Pump:
                    startActivity(new Intent(getApplicationContext(), Pump.class));
                    break;
                case Ferment:
                    startActivity(new Intent(getApplicationContext(), Ferment.class));
                    break;
                default:
                    startActivity(new Intent(getApplicationContext(), BrewStatus.class));
            }
            return null;
        }
    }
}
