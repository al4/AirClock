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
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.StrictChronology;

import java.util.Arrays;
import java.util.HashMap;
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

    String mStatus; // atOrigin, atDestination or inFlight
    private String direction = "auto";

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

    public void setDirection(String direction) {
        String[] directions = {"forward", "reverse", "auto"};
        Log.d("setDirection", "value " + direction);
        if (Arrays.asList(directions).contains(direction)) {
            this.direction = direction;
        } else {
            Log.w("setDirection", "invalid value " + direction + ", setting auto");
            this.direction = "auto";
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
     * Get elapsed time at a particular instant
     *
     * @return
     */
    public float getElapsed(DateTime dateTime) {
        long nowMillis = dateTime.getMillis();
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
     * Get elapsed time now
     * @return
     */
    public float getElapsed() {
        return getElapsed(new DateTime().now());
    }


    /**
     * Get the total amount of time we need to shift in minutes and the direction
     *
     * @return
     */
    public HashMap getTimeShiftHash() {
        // Amount we're shifting time by in minutes
        int timeShift;
        String ldirection;
        HashMap rv = new HashMap();

        int simpleDiff = mDestOffset - mOriginOffset;

        if (this.direction.equals("auto")) {
            if (simpleDiff <= 12*60 && simpleDiff >= -12*60) {
                // Use simple difference. If it's less than 12 hours going the other way is not going
                // to be faster.
                Log.d("getTimeShiftHash",
                        "Using simple difference for timeshift: " + String.valueOf(simpleDiff));
                rv.put("timeShift", simpleDiff);
                ldirection = (simpleDiff >= 0) ? "forward" : "reverse";
                rv.put("direction", ldirection);
            }
            else {
                Log.d("getTimeShiftHash", "using opposite diff");
                // Opposite way must be faster, subtract from 24h
                if (simpleDiff > 0) {
                    rv.put("timeShift", -24 * 60 + simpleDiff);  // should be negative
                    rv.put("direction", "reverse");
                }
                else if (simpleDiff < 0) {
                    rv.put("timeShift", 24 * 60 + simpleDiff);  // should be positive
                    rv.put("direction", "forward");
                }
                else {
                    // must be zero...
                    rv.put("timeShift", 0);
                    rv.put("direction", "forward");
                }
            }
        }
        else if (this.direction.equals("forward")) {
            rv.put("direction", "forward");
            rv.put("timeShift", (simpleDiff >=0) ? simpleDiff : 24 - simpleDiff);
        }
        else if (this.direction.equals("reverse")) {
            rv.put("direction", "reverse");
            rv.put("timeShift", (simpleDiff <= 0) ? simpleDiff : -24 + simpleDiff);
        }

        Log.d("getTimeShiftHash", "final shift: " + rv.get("timeShift"));
        return rv;
    }

    public int getTimeShiftMins() {
        HashMap h = getTimeShiftHash();
        return (int) h.get("timeShift");
    }

    /**
     * Get how far we are through the flight at a particular point in time
     *
     * @return
     */
    public float getFlightProgress(DateTime dateTime) {
        float flightLength = getFlightLength();

        // Time elapsed since departure
        float elapsed = getElapsed(dateTime);

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
     * Get how far through the flight we are now
     */
    public float getFlightProgress() {
        return getFlightProgress(DateTime.now());
    }

    /**
     * Calculates the current effective time zone offset in hours and minutes
     *
     * @return
     * @throws Exception
     */
    public float getEffectiveOffsetMinutes() {
        return getEffectiveOffsetMinutes(DateTime.now());
    }

    /**
     * Get effective offset at a given time
     *
     * @param dateTime
     * @return
     */
    public float getEffectiveOffsetMinutes(DateTime dateTime) {
        // Length of flight
        float flightLength = getFlightLength();

        // Time elapsed since departure
        float elapsed = getElapsed(dateTime);

        // Shift ratio
        Float shiftRatio = elapsed / flightLength;

        // Total time shift across the journey
        HashMap ts = getTimeShiftHash();
        int totalTimeShift = (int) ts.get("timeShift");

        // Time shift at current time
        float shiftMinutes = totalTimeShift * shiftRatio;

        // Add our shift to origin UTC offset
        float offsetMins = shiftMinutes + mOriginOffset;

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
            // Impossible flight, return origin time zone
            long offset = mOriginTime.getZone().getOffset(mOriginTime.toInstant());
            long minutes = msToMinutes((int) offset);
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
     * Get date time zone object for current point of flight
     * @return
     */
    public DateTimeZone getTimeZone() {
        int offsetMins = (int) getEffectiveOffsetMinutes();
        Integer[] offsetHoursMinutes = minutesToHoursMinutes(offsetMins);
        Integer offsetHours = offsetHoursMinutes[0];
        Integer offsetMinutes = offsetHoursMinutes[1];

        return DateTimeZone.forOffsetHoursMinutes(offsetHours, offsetMinutes);
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
        HashMap h = getTimeShiftHash();
        int ts = (int) h.get("timeShift");

        if (shiftDirection().equals("forward") && mOriginOffset + ts > UPPER_TZ_LIMIT * 60 ) {
            return true;
        }
        else if (shiftDirection().equals("reverse") && mOriginOffset + ts < LOWER_TZ_LIMIT) {
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
        HashMap h = getTimeShiftHash();
        int relativeShift = (int) h.get("timeShift");

        if (this.direction.equals("auto") || this.direction == null) {
            if (relativeShift >= 0) {
                return "forward";
            } else {
                return "reverse";
            }
        }
        return this.direction;
    }

    /**
     * Where are we in the flight?
     *
     * @return
     */
    public final String getFlightStatusText() {
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

        else if (DateTime.now().isEqual(mOriginTime)) {
            return "take off!";
        }

        else if (DateTime.now().isEqual(mDestTime)) {
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
        if (offsetMinutes < LOWER_TZ_LIMIT * 60) {
            Log.d("TimeCalc", "Inverting timezone positively");
            return offsetMinutes + 1440;
        }

        if (offsetMinutes > UPPER_TZ_LIMIT * 60) {
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

    /** * Return absolute value of a number
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

    /**
     * Calculate when to set an alarm given a LocalDateTime (no time zone information)
     *
     * To do this, we need to figure out what time zone will be applied when we hit this time...
     * far from trivial!
     *
     * So we use linear algebra to create simple formulas for the absolute time and tz offet of our
     * alarm.
     *
     * @param localAlarmTime
     * @return
     */
    public DateTime timeForAlarm(LocalDateTime localAlarmTime) {
        Log.d("timeForAlarm", "Calculating alarm for time " + localAlarmTime.toString());
        // x axis, Time in ms since 1970
        long To = mOriginTime.getMillis(); // x1
        long Td = mDestTime.getMillis(); // x3
        // y axis, Offset in milliseconds
        long Oo = mOriginTime.getZone().getOffset(mOriginTime); // y1
        long Od = mDestTime.getZone().getOffset(mDestTime); // y3

        System.out.println(String.valueOf(To) + ',' + String.valueOf(Oo));
        System.out.println(String.valueOf(Td) + ',' + String.valueOf(Od));

        // slope = Δx/Δy
        float slope = (Td - To) / (Od - Oo);
        Log.v("debug", String.valueOf(slope));
        System.out.println(String.valueOf(slope));

        /*
            now that we have the slope, we can use algebra to rearrange what we know and come up
            with formulas for what we don't.

            * (x1, y1) is the point at takeoff, i.e (To, Oo)
            * our unknown point, the alarm point is (x2, y2) => (Ta, Oa) (Time of alarm,
              offset of alarm)
            * the localtime of the alarm we want to calculate, T, equals x2 (absolute time at
              alarm) plus y2 (offset at time of alarm), i.e T=x2+y2

            by rearranging the slope formula, y2 = (x2 - x1)/S + y1
            therefore T - x2  = (x2 - x1)/S + y1 etc, until we get the formulas below
        */

        // UTC is zero offset,
        long T = localAlarmTime.toDateTime(DateTimeZone.UTC).getMillis();
        System.out.println("T " + String.valueOf(T));
//        double Ta = ((slope * To) - Oo + T) / (slope + 1);
        // y2 = T - x2
//        double Oa = T - Ta;

//        double Oa = (slope + 1) / ((slope * T) - (slope * To) + Oo);
//        double Ta = T - Oa;

        double Ta = (T + (slope * To) - Oo) / (slope + 1);
        double Oa = T - Ta;

        System.out.println("Ta " + String.valueOf(Ta));
        System.out.println("Oa " + String.valueOf(Oa));

        // construct a datetime
        DateTimeZone alarmTz = DateTimeZone.forOffsetMillis((int) Oa);
        DateTime alarmTime = new DateTime((long) Ta, alarmTz);
        Log.d("timeForAlarm", "as origin: " + alarmTime.toDateTime(mOriginTime.getZone()).toString());
        Log.d("timeForAlarm", "as dest: " + alarmTime.toDateTime(mDestTime.getZone()).toString());

        return alarmTime;
    }
}
