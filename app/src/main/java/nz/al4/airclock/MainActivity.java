/**
 Copyright 2016 Alex Forbes

 This file is part of AirClock.

 AirClock is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 AirClock is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with AirClock.  If not, see <http://www.gnu.org/licenses/>.
 */
package nz.al4.airclock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

public class MainActivity extends AppCompatActivity
        implements DatePickerFragment.OnDatePickedListener,
                   TimePickerFragment.OnTimePickedListener,
                   TimeZonePickerFragment.OnZonePickedListener,
        AirClockFragment.OnTouchListener
{

    TextView OriginText;
    TextView DestText;

    MutableDateTime OriginDate = new DateTime().toMutableDateTime();
    MutableDateTime DestDate = new DateTime().toMutableDateTime();

    AirClockFragment clockFragment = new AirClockFragment();

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

                Snackbar.make(view, "Clock updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                clockFragment.updateClock();

            }
        });

        this.OriginText = (TextView) findViewById(R.id.origin_text);
        this.DestText = (TextView) findViewById(R.id.dest_text);

        // Setup the clock fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.card_view_clock_layout, clockFragment);
        ft.commit();
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
            this.OriginDate.setHourOfDay(hour);
            this.OriginDate.setMinuteOfHour(minute);
            this.OriginText.setText("Selected time: " + this.OriginDate.toString());
        } else if (event == "landing") {
            this.DestDate.setHourOfDay(hour);
            this.DestDate.setMinuteOfHour(minute);
            this.DestText.setText("Selected time: " + this.DestDate.toString());
        }

        Context context = getApplicationContext();
        Toast.makeText(context, "Event: " +event+ " Selected time: " + this.OriginDate.toString(),
                Toast.LENGTH_LONG).show();

        clockFragment.originTime = OriginDate.toDateTime();
        clockFragment.destTime = DestDate.toDateTime();
    }

    @Override
    public void onDatePicked(String event, int year, int month, int day) {
        if (event == "takeoff") {
            this.OriginDate.setYear(year);
            this.OriginDate.setMonthOfYear(month + 1);
            this.OriginDate.setDayOfMonth(day);
            this.OriginText.setText("Selected time: " + this.OriginDate.toString());
        } else if (event == "landing") {
            this.DestDate.setYear(year);
            this.DestDate.setMonthOfYear(month + 1);
            this.DestDate.setDayOfMonth(day);
            this.DestText.setText("Selected time: " + this.DestDate.toString());
        }
    }

    @Override
    public void onZonePicked(String event, int offset) {
        if (event == "takeoff") {
            DateTimeZone zone = DateTimeZone.forOffsetHours(offset);
            this.OriginDate.setZone(zone);
            this.OriginText.setText("Selected time: " + this.OriginDate.toString());
        } else if (event == "landing") {
            DateTimeZone zone = DateTimeZone.forOffsetHours(offset);
            this.DestDate.setZone(zone);
            this.DestText.setText("Selected time: " + this.DestDate.toString());
        }
    }

    @Override
    public void onClockTouch() {

    }
}
