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
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;


/**
 * Created by alex on 17/10/2016.
 */

public class TimeCalculator {

    public TimeCalculator() {
        // empty constructor
    }

    public DateTime getEffectiveTime(
            DateTime OriginTime, DateTime DestTime
    ) {
        Long nowMillis = new DateTime().getMillis();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

        // testing values
//        MutableDateTime testDate;
//        testDate = OriginTime.toMutableDateTime();
//        testDate.addHours(14);
//        Long nowMillis = testDate.getMillis();

        // Length of flight
        Long flightLength = DestTime.getMillis() - OriginTime.getMillis();
        int flightLengthHours = (int) (flightLength / (1000 * 60 * 60));
        Log.i("timeCalc", "flightLength: " + flightLengthHours + " hours");

        // Amount we're shifting time by
        DateTimeZone originTz = OriginTime.getZone();
        DateTimeZone destTz = DestTime.getZone();
        long originOffset = originTz.getOffset(OriginTime.toInstant());
        long destOffset = destTz.getOffset(DestTime.toInstant());
        long timeShift = destOffset - originOffset;
        Log.i("timeCalc", "shift: " + (float) (timeShift / (1000*60*60)) + " hours");

        // Time elapsed since departure
        Long elapsed = nowMillis - OriginTime.getMillis();
        Log.i("timeCalc", "elapsed: " + (float) (elapsed / (1000*60*60)) + " hours");

        // Shift ratio
        Float shiftRatio = elapsed.floatValue() / flightLength.floatValue();
        Log.i("timeCalc", "shift ratio: " + shiftRatio.toString());

        // Shift amount
        Float shiftMillisFloat = timeShift * shiftRatio;
        long shiftMillis = shiftMillisFloat.longValue();
        Log.i("timeCalc", "shift amount: " + (float) (shiftMillis / (1000*60*60)) + "hours");

        // Calculated time
        long calculatedTimeMillis = nowMillis + shiftMillis;
        MutableDateTime calculatedTime = new MutableDateTime(calculatedTimeMillis);
        // As the calculation is an offset of the origin time, the zone is the origin's
        calculatedTime.setZone(OriginTime.getZone());

        // Nice output format
        Log.i("timeCalc", "calculated time: " + fmt.print(calculatedTime));

        return calculatedTime.toDateTime();
    }

    public String getEffectiveOffset(DateTime originTime, DateTime destTime) {
        Long nowMillis = new DateTime().getMillis();

        // Length of flight
        Long flightLength = destTime.getMillis() - originTime.getMillis();
        int flightLengthHours = (int) (flightLength / (1000 * 60 * 60));
        Log.i("offsetCalc", "flightLength: " + flightLengthHours + " hours");

        // Amount we're shifting time by
        DateTimeZone originTz = originTime.getZone();
        DateTimeZone destTz = destTime.getZone();
        long originOffset = originTz.getOffset(originTime.toInstant());
        long destOffset = destTz.getOffset(destTime.toInstant());
        long timeShift = destOffset - originOffset;
        Log.i("offsetCalc", "shift: " + (float) (timeShift / (1000*60*60)) + " hours");

        // Time elapsed since departure
        Long elapsed = nowMillis - originTime.getMillis();
        Log.i("offsetCalc", "elapsed: " + (float) (elapsed / (1000*60*60)) + " hours");

        // Shift ratio
        Float shiftRatio = elapsed.floatValue() / flightLength.floatValue();
        Log.i("offsetCalc", "shift ratio: " + shiftRatio.toString());

        // Shift amount
        Float shiftMillisFloat = timeShift * shiftRatio;
        long shiftMillis = shiftMillisFloat.longValue();
        long shiftMinutes = shiftMillis / (1000*60);
        int offsetMinutes = (int) (shiftMinutes % 60);
        int offsetHours = (int) ((shiftMinutes - offsetMinutes) / 60);

        Log.i("offsetCalc", "hours " + offsetHours);
        Log.i("offsetCalc", "minutes " + offsetMinutes);

        String hours = String.format("%02d", offsetHours);
        String minutes = String.format("%02d", offsetMinutes);
        Log.i("offsetCalc", "GMT+" + hours + minutes);

//        return("GMT+" + offsetHours + offsetMinutes);

        return("GMT+" + hours + minutes);
    }


}
