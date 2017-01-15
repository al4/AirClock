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

    public String[] getTimeZoneOffsetNames() {
        ArrayList<String> tzOffsets = new ArrayList<>();

        for (int i = -12; i <= 14; i++) {
            if (i > 0) {
                tzOffsets.add("GMT+" + String.valueOf(i));
            }
            else if (i < 0 ) {
                tzOffsets.add("GMT" + String.valueOf(i));
            }
            else {
                tzOffsets.add("GMT");
            }
        }

        String[] tzOffsetArray = new String[tzOffsets.size()];
        tzOffsetArray = tzOffsets.toArray(tzOffsetArray);

        return tzOffsetArray;
    }

    public String[] getTimeZoneOffsets() {
        ArrayList<String> tzOffsets = new ArrayList<>();

        for (int i = -12; i <= 14; i++) {
            tzOffsets.add(String.valueOf(i));
        }

        String[] tzOffsetArray = new String[tzOffsets.size()];
        tzOffsetArray = tzOffsets.toArray(tzOffsetArray);

        return tzOffsetArray;
    }

    public CharSequence[] getTimeZoneOffsetSeq() {
        ArrayList<String> tzOffsets = new ArrayList<>();

        for (int i = -12; i <= 14; i++) {
            tzOffsets.add(String.valueOf(i));
        }

        CharSequence[] tzOffsetArray = new CharSequence[tzOffsets.size()];
        tzOffsetArray = tzOffsets.toArray(tzOffsetArray);

        return tzOffsetArray;
    }

}
