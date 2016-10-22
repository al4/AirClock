package nz.al4.airclock;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity
        extends AppCompatActivity
        implements TimePickerFragment.OnTimePickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_landing_time) {
            showDatePickerDialogs("landing", this.findViewById(android.R.id.content));
            return true;
        } else if (id == R.id.action_takeoff_time) {
            showDatePickerDialogs("takeoff", this.findViewById(android.R.id.content));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialogs(String event, View v) {
        Bundle bundle = new Bundle();
        bundle.putString("event", event);

        DialogFragment timeFragment = new TimePickerFragment();
        timeFragment.setArguments(bundle);
        timeFragment.show(getSupportFragmentManager(), "timePicker");

        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.setArguments(bundle);
        dateFragment.show(getSupportFragmentManager(), "datePicker");

        DialogFragment timeZoneFragment = new TimeZonePickerFragment();
        timeZoneFragment.setArguments(bundle);
        timeZoneFragment.show(getSupportFragmentManager(), "timeZonePicker");

        // fetch and return the values
    }

    public void onTimePicked(String event, int hour, int minute) {
        Context context = getApplicationContext();
        Toast.makeText(context, "Event: " +event+ " Selected hour: " +hour+ " minute: " +minute,
                Toast.LENGTH_LONG).show();
    }
}
