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


/**
 * Created by alex on 17/10/2016.
 */

public class TimeCalculator {

    public TimeCalculator(DateTime originTime, DateTime destTime) throws SpaceTimeException {
        mOriginTime = originTime;
        mDestTime = destTime;

        if (mDestTime.isBefore(mOriginTime)) {
            throw new SpaceTimeException("You can not land before you take off");
        }
    }

    DateTime mOriginTime;
    DateTime mDestTime;

    public float getFlightLength(Boolean as_hours) {
        // Length of flight
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
        // Amount we're shifting time by in millis
        DateTimeZone originTz = mOriginTime.getZone();
        Log.d("origin tz", String.valueOf(originTz));
        DateTimeZone destTz = mDestTime.getZone();
        Log.d("dest tz", String.valueOf(destTz));

        long originOffset = originTz.getOffset(mOriginTime.toInstant());
        Log.d("origin offset", String.valueOf(originOffset));
        long destOffset = destTz.getOffset(mDestTime.toInstant());
        Log.d("dest offset", String.valueOf(destOffset));

        // calculate which way is better to shift
        Long timeShift;
        // destination as ahead of origin
        long forwardTimeShift = destOffset - originOffset;
        Log.d("forward shift", String.valueOf(forwardTimeShift));

        // destination as behind origin
        long reverseTimeShift = originOffset - destOffset;
        Log.d("reverse shift", String.valueOf(reverseTimeShift));

        if (forwardTimeShift < reverseTimeShift) {
            // better to go the other way around the globe!
            timeShift = forwardTimeShift;
            Log.i("timeCalc", "using forward time shift");
        } else {
            timeShift = reverseTimeShift;
            Log.i("timeCalc", "using reverse time shift");
        }

        float timeShiftHours = (timeShift / (1000*60*60));
        Log.i("timeCalc", "shift: " + timeShiftHours + " hours");

        if (as_hours) {
            return timeShiftHours;
        } else {
            return timeShift.intValue();
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
    public String getEffectiveOffset() throws Exception {
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
        long shiftMillis = shiftMillisFloat.longValue();
        long shiftMinutes = shiftMillis / (1000*60);

        // Convert minutes to hours+minutes
        int offsetMinutes = (int) (shiftMinutes % 60);
        int offsetHours = (int) ((shiftMinutes - offsetMinutes) / 60);
        Log.i("offsetCalc", "hours " + offsetHours + ", minutes " + offsetMinutes);

        if (offsetHours > 99 || offsetMinutes > 99) {
            String msg = "offset value is greater than 99! hard to express this as a time zone...";
            Log.e("offsetCalc", msg);
            throw new Exception(msg);
        }

        // Get existing hours+minutes offset

        // Add to existing time zone


        // Output time zone string
        // Forwards or backwards?
        if (shiftMinutes >= 0) {
            String hours = String.format("%02d", offsetHours);
            String minutes = String.format("%02d", offsetMinutes);

            Log.i("offsetCalc", "GMT+" + hours + minutes);
            return("GMT+" + hours + minutes);
        } else {
            String hours = String.format("%02d", offsetHours * -1);
            String minutes = String.format("%02d", offsetMinutes * -1);
            Log.i("offsetCalc", "GMT-" + hours + minutes);
            return("GMT+" + hours + minutes);
        }
    }


}
