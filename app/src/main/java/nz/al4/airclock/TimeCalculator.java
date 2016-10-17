package nz.al4.airclock;

import android.content.Context;

import java.util.Date;
import java.util.Calendar;


/**
 * Created by alex on 17/10/2016.
 */

public class TimeCalculator {
    public TimeCalculator(
            String destOffsetPref,
            String takeOffTimePref,
            String landingTimePref
    ) {
        takeOffTimePref = takeOffTimePref;
        landingTimePref = landingTimePref;
        destOffsetPref = destOffsetPref;

        // get take off time in UTC
        Date takeOffDate = new Date();
    }

    public Date getEffectiveTime() {
        return new Date();
    }
}
