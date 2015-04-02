package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends ActionBarActivity {

    public boolean brewInProgress = true;
    IBrewControlOperations operations = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HaukBrewControlApplication myApp = (HaukBrewControlApplication) getApplication();


        /**if(brewInProgress){
            startActivity(new Intent(this, BrewStatus.class));
        }
        else{
            startActivity(new Intent(this, StartBrew.class));
        }**/
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

    public void openStartBrewActivity(View view){
        startActivity(new Intent(this, StartBrew.class));
    }
}
