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

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by alex on 17/10/2016.
 */

public class TimeCalculator {

    public TimeCalculator(DateTime originTime, DateTime destTime) {
        mOriginTime = originTime;
        mDestTime = destTime;
    }

    DateTime mOriginTime;
    DateTime mDestTime;

    public float getFlightLength(Boolean as_hours) throws AirClockSpaceTimeException {
        // Length of flight
        if (mDestTime.isBefore(mOriginTime)) {
            Log.e("getFlightLength", "You can not land before you take off");
            return 0;
        }

        Long flightLength = mDestTime.getMillis() - mOriginTime.getMillis();
        float flightLengthHours = (float) (flightLength / (1000 * 60 * 60));
        Log.i("offsetCalc", "flightLength: " + flightLengthHours + " hours");

        if (as_hours) {
            return flightLengthHours;
        } else {
            return flightLength.intValue();
        }
    }

    public float getElapsed(Boolean hours) {
        long nowMillis = new DateTime().getMillis();
        Long elapsed = nowMillis - mOriginTime.getMillis();
        Log.i("timeCalc", "elapsed millis: " + elapsed);
        float elapsedHours = (elapsed.floatValue() / (1000*60*60));
        Log.i("timeCalc", "elapsed: " + elapsedHours + " hours");
        if (hours) {
            return elapsedHours;
        } else {
            return elapsed.floatValue();
        }
    }

    /**
     * @param as_hours whether to return the shift in hours or raw int
     * @return
     */
    public float getTotalTimeShift(Boolean as_hours) {
        // Amount we're shifting time by in minutes
        int originOffset = getTimeZoneOffset(mOriginTime.getZone());
        Log.d("origin offset", String.valueOf(originOffset));
        int destOffset = getTimeZoneOffset(mDestTime.getZone());
        Log.d("dest offset", String.valueOf(destOffset));

//        // calculate which way is better to shift
//        int timeShift;
//        // destination as ahead of origin
//        int forwardTimeShift = (int) abs(destOffset - originOffset);
//        Log.d("forward shift", String.valueOf(forwardTimeShift));
//
//        // destination as behind origin
//        int reverseTimeShift = (int) abs(originOffset - destOffset);
//        Log.d("reverse shift", String.valueOf(reverseTimeShift));
//
//        if (forwardTimeShift <= reverseTimeShift) {
//            // better to go the other way around the globe!
//            timeShift = forwardTimeShift;
//            Log.i("timeCalc", "using forward time shift");
//        } else {
//            timeShift = reverseTimeShift;
//            Log.i("timeCalc", "using reverse time shift");
//        }
//
//        int timeShiftHours = (timeShift / 60);
//        Log.i("timeCalc", "shift: " + timeShiftHours + " hours");


        List<Integer> shiftValues = new ArrayList<>();

        shiftValues.add(destOffset - originOffset);
        shiftValues.add(destOffset + originOffset);
        shiftValues.add(originOffset - destOffset);
        shiftValues.add((int) (abs(originOffset) + abs(destOffset)));

        for (int i = 0; i < shiftValues.size(); i++) {
            Log.d("shift-value", String.valueOf(shiftValues.get(i)));
        }

        Integer timeShift = Collections.max(shiftValues);
        timeShift = (int) (24*60 - (abs(destOffset) + abs(originOffset)));

        Log.d("getTotalTimeShift", "");
        Log.d("getTotalTimeShift", "max shift: " + String.valueOf(timeShift));

        // The offset we shift by is simply mod 12 hours of the difference
        timeShift = timeShift % 1440;

        if (as_hours) {
            return timeShift / 60;
        } else {
            return timeShift;
        }
    }


    /**
     * Calculates the current effective time zone offset in hours and minutes
     *
     * We work in hours and minutes mostly, because that is what the Android TextClock accepts as
     * its timezone.
     *
     * @return
     * @throws Exception
     */
    public String getEffectiveOffset() throws AirClockException {
        if (mDestTime.isBefore(mOriginTime)) {
            Log.e("getEffectiveOffset", "Cannot get offset, destination time is before origin time");
            throw new AirClockSpaceTimeException("You can not land before you take off");
        }
        // Length of flight
        float flightLength = getFlightLength(false);

        // Time elapsed since departure
        float elapsed = getElapsed(false);

        // Shift ratio
        Float shiftRatio = elapsed / flightLength;
        Log.i("offsetCalc", "shift ratio: " + shiftRatio.toString());

        // Total time shift across the journey
        float totalTimeShift = getTotalTimeShift(false);

        // Time shift at current time
        Float shiftMillisFloat = totalTimeShift * shiftRatio;
        int shiftMillis = shiftMillisFloat.intValue();
        int shiftMinutes = shiftMillis / (1000*60);
        Log.i("offsetCalc", "shift minutes: " + shiftMinutes);

        // Get origin UTC offset minutes
        int originOffsetMinutes = getTimeZoneOffset(mOriginTime.getZone());
        Log.i("offsetCalc", "origin offset minutes: " + originOffsetMinutes);

        // Add our shift to origin UTC offset
        int offsetMins = shiftMinutes + originOffsetMinutes;
        Log.i("offsetCalc", "effective offset minutes: " + offsetMins);

        // Convert minutes to hours+minutes
        Integer[] offsetHoursMinutes = minutesToHoursMinutes(offsetMins);
        Integer offsetHours = offsetHoursMinutes[0];
        Integer offsetMinutes = offsetHoursMinutes[1];
        Log.i("offsetCalc", "effective offset hours: " + offsetHours + " mins: " + offsetMinutes);

        // Output time zone string
        // Forwards or backwards?
        String hours = String.format("%02d", offsetHours);
        String minutes = String.format("%02d", offsetMinutes);

        if (offsetMins >= 0) {
            Log.i("offsetCalc", "GMT+" + hours + minutes);
            return("GMT+" + hours + minutes);
        } else {
            Log.i("offsetCalc", "GMT-" + hours + minutes);
            return("GMT-" + hours + minutes);
        }
    }

    /**
     * minutesToHoursMinutes
     *
     * Convert minutes to a time-zone style string of hours and minutes
     *
     * @return
     */
    public Integer[] minutesToHoursMinutes(int nMinutes) throws AirClockException {
        int minutes = nMinutes % 60;
        int hours = (nMinutes - minutes) / 60;

        if (hours > 99 || minutes > 99) {
            String msg = "a value is greater than 99! hard to express this as a time zone...";
            Log.e("minutesToHoursMinutes", msg);
            throw new AirClockException(msg);
        }

        return new Integer[] {hours, minutes};
    }

    /**
     * With thanks to Josh Pinter at http://stackoverflow.com/questions/10900494/
     *
     * @param tz Time zone to get the offset for
     * @return
     */
    public static Integer getTimeZoneOffset(DateTimeZone tz) {
        Long instant = DateTime.now().getMillis();

        long offsetInMilliseconds = tz.getOffset(instant);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(offsetInMilliseconds);

        return (int) minutes;
    }

    /**
     * Return absolute value of a number
     *
     * @param a
     * @return
     */
    private static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }

}
