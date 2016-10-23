package nz.al4.airclock;

import android.util.Log;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by alex on 17/10/2016.
 */

public class TimeZoneList {
    private String[] all_timezones = TimeZone.getAvailableIDs();

    public String[] getTimeZones() {
        ArrayList<String> tzOffsets = new ArrayList<>();
        ArrayList<String> tzNames = new ArrayList<>();

        for (String tzId : all_timezones) {
            TimeZone tz = TimeZone.getTimeZone(tzId);
            String name = tz.getDisplayName();
            String offset = String.valueOf(tz.getRawOffset());

            if (!tzOffsets.contains(offset)) {
                tzOffsets.add(offset);
                tzNames.add(name);
            }
        }

        String[] tzOffsetArray = new String[tzOffsets.size()];
        tzOffsetArray = tzOffsets.toArray(tzOffsetArray);

        return tzOffsetArray;
    }

    public String[] getTimeZoneOffsets() {
        ArrayList<String> tzOffsets = new ArrayList<>();

        for (int i = -13; i <= 13; i++) {
            tzOffsets.add(String.valueOf(i));
        }

        String[] tzOffsetArray = new String[tzOffsets.size()];
        tzOffsetArray = tzOffsets.toArray(tzOffsetArray);

        return tzOffsetArray;
    }
}
