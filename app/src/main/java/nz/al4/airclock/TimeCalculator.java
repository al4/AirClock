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

import java.util.Arrays;
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
    String mDirection; // forward or reverse
    Boolean mCrossesDateLine;
    String mStatus; // atOrigin, atDestination or inFlight

    /**
     * Setter for mStatus
     * @param status
     * @throws AirClockException
     */
    private void setmStatus(String status) throws AirClockException {
        String[] statuses = {"atOrigin", "arDestination", "inFlight"};
        if (Arrays.asList(statuses).contains(status)) {
            mStatus = status;
        } else {
            throw new AirClockException("Invalid status value");
        }
    }

    /**
     * Setter for mDirection
     * @param direction
     * @throws AirClockException
     */
    private void setmDirection(String direction) throws AirClockException {
        String[] directions = {"forward", "reverse"};
        if (Arrays.asList(directions).contains(direction)) {
            mDirection = direction;
        } else {
            throw new AirClockException("Invalid direction");
        }
    }


    /**
     * Get flight length in milliseconds
     *
     * @return
     * @throws AirClockSpaceTimeException
     */

    public float getFlightLength() throws AirClockSpaceTimeException {
        // Length of flight
        if (mDestTime.isBefore(mOriginTime)) {
            String msg = "You can not land before you take off";
            Log.e("getFlightLength", msg);
            throw new AirClockSpaceTimeException(msg);
        }

        Long flightLength = mDestTime.getMillis() - mOriginTime.getMillis();

        float flightLengthHours = (float) (flightLength / (1000 * 60 * 60));
        Log.d("offsetCalc", "flightLength: " + flightLengthHours + " hours");

        return flightLength.intValue();
    }

    /**
     * Get elapsed time during the flight
     *
     * @return
     */
    public float getElapsed() {
        long nowMillis = new DateTime().getMillis();
        Long elapsed = nowMillis - mOriginTime.getMillis();
        Log.d("timeCalc", "elapsed millis: " + elapsed);

        int elapsedHours = msToHours(elapsed.intValue());
        Log.d("timeCalc", "elapsed: " + elapsedHours + " hours");

        return elapsed.floatValue();
    }

    /**
     * Get the total amount of time we need to shift in minutes
     *
     * @return
     */
    public float getTotalTimeShift() throws AirClockException {
        // Amount we're shifting time by in minutes
        int originOffset = getTimeZoneOffset(mOriginTime.getZone());
        Log.d("getTotalTimeShift", "origin offset: " + String.valueOf(originOffset));
        int destOffset = getTimeZoneOffset(mDestTime.getZone());
        Log.d("getTotalTimeShift", "dest offset: " + String.valueOf(destOffset));

        int timeShift = (int) (24*60 - (abs(destOffset) + abs(originOffset)));

        // The offset we shift by is simply mod 12 hours of the sum of differences from UTC
        timeShift = timeShift % 1440;
        Log.d("getTotalTimeShift", "Time Shift: " + String.valueOf(timeShift));

        Integer relativeShift;
        // Figure out if we're going forwards or backwards
        if (originOffset + timeShift == destOffset) {
            // forward!
            Log.d("getTotalTimeShift", "direction: forward!");
            relativeShift = timeShift;
            this.setmDirection("forward");
            mCrossesDateLine = false;
        } else if (originOffset - timeShift == destOffset) {
            // reverse!
            Log.d("getTotalTimeShift", "direction: reverse!");
            relativeShift = timeShift * -1;
            this.setmDirection("reverse");
            mCrossesDateLine = false;
        } else if (originOffset < 0) {
            // reverse across international date line
            Log.d("getTotalTimeShift", "direction: reverse across date line");
            relativeShift = timeShift * -1;
            this.setmDirection("reverse");
            mCrossesDateLine = true;
        } else if (originOffset >= 0) {
            // forward across international date line
            Log.d("getTotalTimeShift", "direction: forward across date line");
            relativeShift = timeShift;
            this.setmDirection("forward");
            mCrossesDateLine = true;
        } else {
            throw new AirClockException("Unhandled case");
        }

        Log.d("getTotalTimeShift", "final shift: " + String.valueOf(relativeShift));
        return relativeShift;
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
//            throw new AirClockSpaceTimeException("You can not land before you take off");
            return "Destination is before Origin";
        }

        if (DateTime.now().isAfter(mDestTime)) {
            Log.w("getEffectiveOffset", "Outside of flight time");
            return "You have arrived";
        }

        if (DateTime.now().isBefore(mOriginTime)){
            Log.w("getEffectiveOffset", "Outside of flight time");
            return "You have not taken off yet";
        }

        // Length of flight
        float flightLength = getFlightLength();

        // Time elapsed since departure
        float elapsed = getElapsed();

        // Shift ratio
        Float shiftRatio = elapsed / flightLength;
        Log.d("offsetCalc", "shift ratio: " + shiftRatio.toString());

        // Total time shift across the journey
        float totalTimeShift = getTotalTimeShift();

        // Time shift at current time
        float shiftMinutes = totalTimeShift * shiftRatio;
        Log.d("offsetCalc", "shift minutes: " + shiftMinutes);

        // Get origin UTC offset minutes
        int originOffsetMinutes = getTimeZoneOffset(mOriginTime.getZone());
        Log.d("offsetCalc", "origin offset minutes: " + originOffsetMinutes);

        // Add our shift to origin UTC offset
        float offsetMins = shiftMinutes + originOffsetMinutes;
        Log.i("offsetCalc", "effective offset minutes: " + offsetMins);

        // Invert if over the date line
        offsetMins = invertOffsetIfDateLineCrossed(offsetMins);

        // Convert minutes to hours+minutes
        Integer[] offsetHoursMinutes = minutesToHoursMinutes((int) offsetMins);
        Integer offsetHours = offsetHoursMinutes[0];
        Integer offsetMinutes = offsetHoursMinutes[1];
        Log.d("offsetCalc", "effective offset hours: " + offsetHours + " mins: " + offsetMinutes);

        // Output time zone string
        // Forwards or backwards?
        String hours = String.format("%02d", (int) abs(offsetHours));
        String minutes = String.format("%02d", (int) abs(offsetMinutes));

        if (offsetMins >= 0) {
            Log.i("offsetCalc", "GMT+" + hours + minutes);
            return("GMT+" + hours + minutes);
        } else {
            Log.i("offsetCalc", "GMT-" + hours + minutes);
            return("GMT-" + hours + minutes);
        }
    }

    /**
     * Invert time zone when crossing the dateline
     *
     * When we head west and the offset is < GMT-12 or head east and we reach and offset
     * is > GMT+13 it makes sense to invert the offset so it is closer to the destination.
     *
     * This is imperfect because we're making assumptions about what the actual desired time zone
     * is. Currently, it just inverts if the time zone is ahead of GMT+13 or behind GMT-12.
     *
     * Apologies to anyone who lives on an affected island in the Pacific Ocean :-)
     */
    public float invertOffsetIfDateLineCrossed(float offsetMinutes) {
        if (offsetMinutes < -12*60) {
            Log.d("TimeCalc", "Inverting timezone positively");
            return offsetMinutes + 1440;
        }

        if (offsetMinutes > 13*60) {
            Log.d("TimeCalc", "Inverting timezone negatively");
            return offsetMinutes - 1440;
        }

        return offsetMinutes;
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

    /**
     * Convert milliseconds to minutes
     */
    public int msToMinutes(int millis) {
        int minutes = (millis / (1000 * 60));
        return minutes;
    }

    /**
     * Convert milliseconds to hours
     */
    public int msToHours(int millis) {
        int hours = (millis / (1000 * 60 * 60));
        return hours;
    }
}
