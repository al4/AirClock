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

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by alex on 17/10/2016.
 *
 * Stolen from http://stackoverflow.com/questions/5533078/
 */


public class DatePreference extends DialogPreference {
    private DatePicker picker=null;

    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    public DatePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    public static int getYear(String date) {
        String[] pieces=date.split("/");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMonth(String date) {
        String[] pieces=date.split("/");

        return(Integer.parseInt(pieces[1]));
    }

    public static int getDay(String date) {
        String[] pieces=date.split("/");

        return(Integer.parseInt(pieces[2]));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());

        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            year = picker.getYear();
            month = picker.getMonth();
            day = picker.getDayOfMonth();

            String date = String.valueOf(year) + "/"
                    + String.valueOf(month + 1) + "/"
                    + String.valueOf(day)
                ;

            if (callChangeListener(date)) {
                persistString(date);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String date;

        if (restoreValue) {
            if (defaultValue == null) {
                date = getPersistedString("1970/1/1");
            }
            else {
                date = getPersistedString(defaultValue.toString());
            }
        }
        else {
            date = defaultValue.toString();
        }

        year = getYear(date);
        month = getMonth(date);
        day = getDay(date);
    }
}
