package nz.al4.airclock;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.w3c.dom.Text;

public class MainActivity
        extends AppCompatActivity
        implements TimePickerFragment.OnTimePickedListener {

    TextView OriginText;
    TextView DestText;

    MutableDateTime OriginDate = new DateTime().toMutableDateTime();
    MutableDateTime DestDate = new DateTime().toMutableDateTime();

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

        this.OriginText = (TextView) findViewById(R.id.origin_text);
        this.DestText = (TextView) findViewById(R.id.dest_text);
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
        if (event == "takeoff") {
            this.OriginText.setText("Selected time: " + this.OriginDate.toString());
            this.OriginDate.setHourOfDay(hour);
            this.OriginDate.setMinuteOfDay(minute);
        } else if (event == "landing") {
            this.DestText.setText("Selected time: " + this.OriginDate.toString());
            this.DestDate.setHourOfDay(hour);
            this.DestDate.setMinuteOfDay(minute);
        }

//        Context context = getApplicationContext();
//        Toast.makeText(context, "Event: " +event+ " Selected time: " + this.OriginDate.toString(),
//                Toast.LENGTH_LONG).show();
    }

//    public void onDateSet()
}
