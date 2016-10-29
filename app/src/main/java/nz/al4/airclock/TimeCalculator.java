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
        Long timeShift = destOffset - originOffset;
        Log.i("timeCalc", "shift: " + (float) (timeShift / (1000*60*60)) + " hours");

        // Time elapsed since departure
        Long elapsed = nowMillis - OriginTime.getMillis();
        Log.i("timeCalc", "elapsed: " + (float) (elapsed / (1000*60*60)) + " hours");

        // Shift ratio
        Float shiftRatio = elapsed.floatValue() / flightLength.floatValue();
        Log.i("timeCalc", "shift ratio: " + shiftRatio.toString());

        // Shift amount
        Float shiftMillisFloat = timeShift * shiftRatio;
        Long shiftMillis = shiftMillisFloat.longValue();
        Log.i("timeCalc", "shift amount: " + (float) (shiftMillis / (1000*60*60)) + "hours");

        // Calculated time
        Long calculatedTimeMillis = nowMillis + shiftMillis;
        MutableDateTime calculatedTime = new MutableDateTime(calculatedTimeMillis);
        // As the calculation is an offset of the origin time, the zone is the origin's
        calculatedTime.setZone(OriginTime.getZone());

        // Nice output format
        Log.i("timeCalc", "calculated time: " + fmt.print(calculatedTime));

        return calculatedTime.toDateTime();
    }


    public String testTime(
            DateTime OriginTime, DateTime DestTime
    ) {
        DateTime testTime = getEffectiveTime(OriginTime, DestTime);
        return "";

    }
}
