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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements AirClockFragment.OnTouchListener, TimePickerFragment.OnAlarmTimePickedListener
{

    TextView mOriginText;
    TextView mDestText;
    TextView mInfoStatus;
    TextView mInfoFlightLength;
    TextView mInfoFlightProgress;
    TextView mInfoShiftDirection;
    TextView mInfoShiftAmount;
    TextView mInfoCrossesDateLine;

    MutableDateTime mOriginDate = new DateTime().toMutableDateTime();
    MutableDateTime mDestDate = new DateTime().toMutableDateTime();
    String mDirection;

    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

    TimeCalculator mTimeCalculator;
    AirClockFragment mClockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClockFragment = new AirClockFragment();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Calculations updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mClockFragment.updateClock();
                setTextViews();

            }
        });

        this.mOriginText = (TextView) findViewById(R.id.origin_text);
        this.mDestText = (TextView) findViewById(R.id.dest_text);
        this.mInfoStatus = (TextView) findViewById(R.id.info_status);
        this.mInfoFlightLength = (TextView) findViewById(R.id.info_flight_length);
        this.mInfoFlightProgress = (TextView) findViewById(R.id.info_flight_progress);
        this.mInfoShiftAmount = (TextView) findViewById(R.id.info_shift_amount);
        this.mInfoShiftDirection = (TextView) findViewById(R.id.info_shift_direction);
        this.mInfoCrossesDateLine = (TextView) findViewById(R.id.info_crosses_dateline);

        getPreferences();

        // Setup the clock fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.card_view_clock_layout, mClockFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        mClockFragment.updateClock();
        setTextViews();
    }

    private void getPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Must set time zones before the hour and minute
        String originTimeZone = prefs.getString("originTimeZone", null);
        if (originTimeZone != null) {
            DateTimeZone zone = DateTimeZone.forOffsetHours(Integer.parseInt(originTimeZone));
            this.mOriginDate.setZone(zone);
        }

        String originDate = prefs.getString("takeOffDate", null);
        if (originDate != null) {
            this.mOriginDate.setYear(DatePreference.getYear(originDate));
            this.mOriginDate.setMonthOfYear(DatePreference.getMonth(originDate));
            this.mOriginDate.setDayOfMonth(DatePreference.getDay(originDate));
        } else {
            Log.i("prefs", "takeOffDate is null");
        }

        String originTime = prefs.getString("takeOffTime", null);
        if (originTime != null) {
            this.mOriginDate.setHourOfDay(TimePreference.getHour(originTime));
            this.mOriginDate.setMinuteOfHour(TimePreference.getMinute(originTime));
        } else {
            Log.i("prefs", "takeOffTime is null");
        }

        String destTimeZone = prefs.getString("destTimeZone", null);
        if (destTimeZone != null) {
            DateTimeZone zone = DateTimeZone.forOffsetHours(Integer.parseInt(destTimeZone));
            this.mDestDate.setZone(zone);
        }

        String destDate = prefs.getString("landingDate", null);
        if (destDate != null) {
            this.mDestDate.setYear(DatePreference.getYear(destDate));
            this.mDestDate.setMonthOfYear(DatePreference.getMonth(destDate));
            this.mDestDate.setDayOfMonth(DatePreference.getDay(destDate));
        } else {
            Log.i("prefs", "landingDate is null");
        }

        String destTime = prefs.getString("landingTime", null);
        if (destTime != null) {
            this.mDestDate.setHourOfDay(TimePreference.getHour(destTime));
            this.mDestDate.setMinuteOfHour(TimePreference.getMinute(destTime));
        } else {
            Log.i("prefs", "landingTime is null");
        }

        mDirection = prefs.getString("calc_direction", "auto");
        Log.e("mainfoo", mDirection);

        setTextViews();
        mClockFragment.direction = mDirection;
        mClockFragment.originTime = mOriginDate.toDateTime();
        mClockFragment.destTime = mDestDate.toDateTime();
    }

    private void setTextViews() {
        mTimeCalculator = new TimeCalculator(
                mOriginDate.toDateTime(),
                mDestDate.toDateTime()
        );
        TimeCalculator tc = mTimeCalculator;
        tc.setDirection(mDirection);

        float flightLength = tc.msToHours((int) tc.getFlightLength());
        float flightProgress = tc.getFlightProgress();
        float shiftMins = 0.0f;

        HashMap h = tc.getTimeShiftHash();
        int t = (int) h.get("timeShift");
        shiftMins = (t <= 0.0f) ? 0.0f - t : t;

        String shiftDirection = (tc.shiftDirection());
        String crossesDateLine = (tc.crossesDateLine()) ? "yes" : "no";
        String flightStatus = tc.getFlightStatusText();

        this.mOriginText.setText(
                dateTimeFormatter.print(mOriginDate).replace(" ", "\n") +
                        " " + mOriginDate.getZone().toString());
        this.mDestText.setText(
                dateTimeFormatter.print(mDestDate).replace(" ", "\n") +
                        " " + mDestDate.getZone().toString());

        this.mInfoStatus.setText(flightStatus);
        this.mInfoFlightLength.setText(String.format("%1.1f hours", flightLength));
        this.mInfoFlightProgress.setText(String.format("%,.0f%%", flightProgress * 100));
        this.mInfoShiftAmount.setText(String.format("%,.0f hours", shiftMins / 60));
        this.mInfoShiftDirection.setText(String.valueOf(shiftDirection));
        this.mInfoCrossesDateLine.setText(String.valueOf(crossesDateLine));
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
        } else if (id == R.id.action_alarm) {
            DialogFragment timeFragment = new TimePickerFragment();
            timeFragment.show(getSupportFragmentManager(), "timePicker");
        } else if (id == R.id.action_about) {
            DialogFragment aboutFragment = new AboutFragment();
            aboutFragment.show(getSupportFragmentManager(), "aboutFragment");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClockTouch() {

    }

    public void startPreferenceActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAlarmTimePicked(int hour, int minute) {

        DateTime currentTime = DateTime.now().toDateTime(
                mTimeCalculator.getTimeZone()
        );
        Log.v("alarmCalc", "current time" + currentTime.toString());
        MutableDateTime desiredTime = currentTime.toMutableDateTime();
        desiredTime.setHourOfDay(hour);
        desiredTime.setMinuteOfHour(minute);

        // if after we set the hour + minute we get an earlier time, user probably means next day
        if (desiredTime.isBefore(currentTime)) {
            desiredTime.addDays(1);
        }

        DateTime alarmTime = mTimeCalculator.timeForAlarm(
                desiredTime.toDateTime().toLocalDateTime());

        DateTime originAlarm = alarmTime.toDateTime(mTimeCalculator.mOriginTime.getZone());
        DateTime destAlarm = alarmTime.toDateTime(mTimeCalculator.mDestTime.getZone());

        String msg = String.format("You should set your alarm for:\n%s (origin)\nor\n%s (destination)",
                dateTimeFormatter.print(originAlarm) + " " + originAlarm.getZone().toString(),
                dateTimeFormatter.print(destAlarm) + " " + destAlarm.getZone().toString()
                );

        new AlertDialog.Builder(this)
                .setTitle("Alarm")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                    }
                }).show();
    }
}
