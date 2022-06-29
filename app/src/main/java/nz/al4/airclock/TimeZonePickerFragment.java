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
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class TimeZonePickerFragment extends AppCompatDialogFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimeZonePickerFragment() {
    }

    OnZonePickedListener mCallback;
    int selected_zone_id = 13;  // default selection
    CharSequence selected_zone;
    String event;

    public interface OnZonePickedListener {
        void onZonePicked(String event, int offset);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnZonePickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnZonePickedListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        this.event = bundle.getString("event");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] timeZones = new TimeZoneList().getTimeZoneOffsetSeq();

        builder.setTitle("Select Time Zone Offset")
            .setSingleChoiceItems(timeZones, selected_zone_id, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    selected_zone_id = id;
                }

            })
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    selected_zone = timeZones[selected_zone_id];
                    int offset = Integer.valueOf(selected_zone.toString());

                    mCallback.onZonePicked(event, offset);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

        return builder.create();
    }

}
