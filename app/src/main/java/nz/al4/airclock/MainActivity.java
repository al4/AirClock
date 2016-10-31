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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity
        implements AirClockFragment.OnTouchListener
{

    TextView OriginText;
    TextView DestText;

    MutableDateTime OriginDate = new DateTime().toMutableDateTime();
    MutableDateTime DestDate = new DateTime().toMutableDateTime();

    AirClockFragment clockFragment = new AirClockFragment();

    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

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
        getPreferences();

        // Setup the clock fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.card_view_clock_layout, clockFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        clockFragment.updateClock();
    }

    private void getPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Must set time zones before the hour and minute
        String originTimeZone = prefs.getString("originTimeZone", null);
        if (originTimeZone != null) {
            DateTimeZone zone = DateTimeZone.forOffsetHours(Integer.parseInt(originTimeZone));
            this.OriginDate.setZone(zone);
        }

        String originDate = prefs.getString("takeOffDate", null);
        if (originDate != null) {
            this.OriginDate.setYear(DatePreference.getYear(originDate));
            this.OriginDate.setMonthOfYear(DatePreference.getMonth(originDate));
            this.OriginDate.setDayOfMonth(DatePreference.getDay(originDate));
        } else {
            Log.i("prefs", "takeOffDate is null");
        }

        String originTime = prefs.getString("takeOffTime", null);
        if (originTime != null) {
            this.OriginDate.setHourOfDay(TimePreference.getHour(originTime));
            this.OriginDate.setMinuteOfHour(TimePreference.getMinute(originTime));
        } else {
            Log.i("prefs", "takeOffTime is null");
        }

        String destTimeZone = prefs.getString("destTimeZone", null);
        if (destTimeZone != null) {
            DateTimeZone zone = DateTimeZone.forOffsetHours(Integer.parseInt(destTimeZone));
            this.DestDate.setZone(zone);
        }

        String destDate = prefs.getString("landingDate", null);
        if (destDate != null) {
            this.DestDate.setYear(DatePreference.getYear(destDate));
            this.DestDate.setMonthOfYear(DatePreference.getMonth(destDate));
            this.DestDate.setDayOfMonth(DatePreference.getDay(destDate));
        } else {
            Log.i("prefs", "landingDate is null");
        }

        String destTime = prefs.getString("landingTime", null);
        if (destTime != null) {
            this.DestDate.setHourOfDay(TimePreference.getHour(destTime));
            this.DestDate.setMinuteOfHour(TimePreference.getMinute(destTime));
        } else {
            Log.i("prefs", "landingTime is null");
        }

        setOriginDestText();
        clockFragment.originTime = OriginDate.toDateTime();
        clockFragment.destTime = DestDate.toDateTime();
    }

    private void setOriginDestText() {
        this.OriginText.setText(dateTimeFormatter.print(OriginDate) + " " + OriginDate.getZone().toString());
        this.DestText.setText(dateTimeFormatter.print(DestDate) + " " + DestDate.getZone().toString());
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
        } else if (id == R.id.action_about) {
            DialogFragment aboutFragment = new AboutFragment();
            aboutFragment.show(getSupportFragmentManager(), "aboutFragment");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClockTouch() {

    }
}
