package no.gravem.hauk.haukbrewcontrol;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onMainNextMenuClick(View view) {
        Log.d("BrewControl", "Hello!");
        RadioGroup mainGroup = (RadioGroup) findViewById(R.id.mainMenuItems);
        int id = mainGroup.getCheckedRadioButtonId();
        String value = ((RadioButton)findViewById(mainGroup.getCheckedRadioButtonId() )).getText().toString();
        if(id==-1){
            //You have to choose something!
            return;
        }
        Log.d("BrewControl", "Yep, value is: " + value);
        switch (id){
            case R.id.start_hlt:
                startActivity(new Intent(this, HeatMenuScreen.class));
                break;
            case R.id.start_mlt:
                startActivity(new Intent(this, MashMenuScreen.class));
                break;
            case R.id.start_pump:
                break;
            case R.id.start_ferment:
                break;
        }
    }
}
