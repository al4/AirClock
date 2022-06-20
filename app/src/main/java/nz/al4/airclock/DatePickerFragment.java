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


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.core.app.DialogFragment;
import androidx.core.app.Fragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * A Date Picker to pick the take off and landing times
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    public DatePickerFragment() {
        // Required empty public constructor
    }

    OnDatePickedListener mCallback;

    public interface OnDatePickedListener {
        void onDatePicked(String event, int year, int month, int day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnDatePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnDatePickedListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCallback = (OnDatePickedListener)getActivity();

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Bundle bundle = this.getArguments();
        String event = bundle.getString("event");

        if(mCallback != null) {
            mCallback.onDatePicked(event, year, month, day);
        }
    }
}
