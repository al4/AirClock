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
    DateTime mOriginTime;
    DateTime mDestTime;

    // Zone offsets in minutes
    Integer mOriginOffset;
    Integer mDestOffset;

    String mDirection; // forward or reverse
    String mStatus; // atOrigin, atDestination or inFlight

    // Upper and lower bounds for time zones
    int UPPER_TZ_LIMIT = 13;
    int LOWER_TZ_LIMIT = -12;

    public TimeCalculator(DateTime originTime, DateTime destTime) {
        mOriginTime = originTime;
        mDestTime = destTime;
        mOriginOffset = getTimeZoneOffset(mOriginTime.getZone());
        mDestOffset = getTimeZoneOffset(mDestTime.getZone());
    }

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

    public float getFlightLength() {
        // Length of flight
        if (mDestTime.isBefore(mOriginTime)) {
            String msg = "You can not land before you take off";
            Log.e("getFlightLength", msg);
            return 0;
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

        if (elapsed > 0) {
            return elapsed.floatValue();
        } else {
            return 0;
        }
    }

    /**
     * Get the total amount of time we need to shift in minutes
     *
     * @return
     */
    public int getTotalTimeShift() {
        // Amount we're shifting time by in minutes
        int timeShift;

        int simpleDiff = mDestOffset - mOriginOffset;

        Log.d("Simple diff", String.valueOf(simpleDiff));
        if (simpleDiff <= 12*60 && simpleDiff >= -12*60) {
            // Use simple difference. If it's less than 12 hours going the other way is not going
            // to be faster.
            timeShift = simpleDiff;
            Log.d("getTotalTimeShift",
                    "Using simple difference for timeshift: " + String.valueOf(simpleDiff));
        }
        else {
            // Opposite way must be faster, subtract from 24h
            if (simpleDiff > 0) {
                timeShift = -24 * 60 + simpleDiff;  // should be negative
            }
            else if (simpleDiff < 0) {
                timeShift = 24 * 60 + simpleDiff;  // should be positive
            }
            else {
                // must be zero...
                timeShift = 0;
            }
        }

        Log.d("getTotalTimeShift", "final shift: " + String.valueOf(timeShift));
        return timeShift;
    }

    /**
     * Get the total length of the flight
     * @return
     */
    public float getFlightProgress() {
        float flightLength = getFlightLength();

        // Time elapsed since departure
        float elapsed = getElapsed();

        // Shift ratio
        Float shiftRatio = elapsed / flightLength;
        Log.d("getFlightProgress", shiftRatio.toString());

        if (shiftRatio > 1) {
            return new Float(1.0);
        } else if (shiftRatio < 0) {
            return new Float(0.0);
        } else if (Double.isNaN(shiftRatio)) {
            return new Float(0.0);
        } else {
            return shiftRatio;
        }
    }

    /**
     * Calculates the current effective time zone offset in hours and minutes
     *
     * @return
     * @throws Exception
     */
    public float getEffectiveOffsetMinutes() {
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

        // Add our shift to origin UTC offset
        float offsetMins = shiftMinutes + mOriginOffset;
        Log.i("offsetCalc", "effective offset minutes: " + offsetMins);

        return offsetMins;
    }

    /**
     * Get the offset as a text time zone, for consumption by the TextClock widget
     *
     * @return
     * @throws AirClockException
     */
    public String getEffectiveOffsetText() {
        if (mDestTime.isBefore(mOriginTime)) {
            // Return origin time zone
            long offset = mOriginTime.getZone().getOffset(mOriginTime.toInstant());
            long minutes = msToMinutes((int) offset);
            Log.e("offsetString", "Cannot get offset, destination time is before origin time");
            return getTzString((int) minutes);
        }

        else if (DateTime.now().isAfter(mDestTime)) {
            // Return destination time zone
            long offset = mDestTime.getZone().getOffset(mDestTime.toInstant());
            long minutes = msToMinutes((int) offset);
            return getTzString((int) minutes);
        }

        else if (DateTime.now().isBefore(mOriginTime)){
            // Return origin time zone
            long offset = mOriginTime.getZone().getOffset(mOriginTime.toInstant());
            long minutes = msToMinutes((int) offset);
            return getTzString((int) minutes);
        }

        else {
            // Return calculated offset

            float offsetMins = getEffectiveOffsetMinutes();
            offsetMins = invertOffsetIfDateLineCrossed(offsetMins);  // Invert if over the date line

            return getTzString((int) offsetMins);
        }
    }

    /**
     * Convert offset in minutes to a TimeZone string
     *
     * @param offsetMins
     * @return
     */
    public String getTzString(int offsetMins) {
        // Convert minutes to hours+minutes
        Integer[] offsetHoursMinutes = minutesToHoursMinutes(offsetMins);
        Integer offsetHours = offsetHoursMinutes[0];
        Integer offsetMinutes = offsetHoursMinutes[1];
        Log.d("getTzString", "hours: " + offsetHours + " mins: " + offsetMinutes);

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
     * Whether we have crossed the date line at the present point in time
     *
     * @return
     */
    public boolean crossedDateLine() {
        float offsetMinutes = getEffectiveOffsetMinutes();

        if (offsetMinutes < LOWER_TZ_LIMIT * 60) {
            Log.d("TimeCalc", "Inverting timezone positively");
            return true;
        }

        else if (offsetMinutes > UPPER_TZ_LIMIT * 60) {
            Log.d("TimeCalc", "Inverting timezone negatively");
            return true;
        }

        else {
            return false;

        }
    }

    /**
     * Whether our time shift will have us crossing the international date line
     *
     * @return
     */
    public boolean crossesDateLine() {
        int ts = getTotalTimeShift();

        if (mDirection == "forward" && mOriginOffset + ts > UPPER_TZ_LIMIT * 60 ) {
            return true;
        }
        else if (mDirection == "reverse" && mOriginOffset + ts < LOWER_TZ_LIMIT) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Figure out whether we are going forwards or backwards
     *
     * @return
     */
    public final String shiftDirection() {
        float relativeShift = getTotalTimeShift();

        if (relativeShift >= 0) {
            try {
                setmDirection("forward");
            } catch (AirClockException e) {
                e.printStackTrace();
            }
        } else {
            try {
                setmDirection("reverse");
            } catch (AirClockException e) {
                e.printStackTrace();
            }
        }
        return mDirection;
    }

    /**
     * Where are we in the flight?
     *
     * @return
     */
    public final String getFlightStatus() {
        if (mDestTime.isBefore(mOriginTime)) {
            return "Error, destination before origin";
        }

        else if (DateTime.now().isBefore(mOriginTime)){
            return "at origin";
        }

        else if (DateTime.now().isAfter(mDestTime)) {
            return "at destination";
        }

        else if (DateTime.now().isAfter(mOriginTime)) {
            return "in flight";
        }

        else if (DateTime.now() == mOriginTime) {
            return "take off!";
        }

        else if (DateTime.now() == mDestTime) {
            return "arrived";
        }

        return "Error";
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
    public Integer[] minutesToHoursMinutes(int nMinutes) {
        int minutes = nMinutes % 60;
        int hours = (nMinutes - minutes) / 60;

        return new Integer[] {hours, minutes};
    }

    /**
     * Calculate the current offset from a given time zone
     *
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
