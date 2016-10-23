package nz.al4.airclock;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Time Picker to set the take off and landing times
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public TimePickerFragment() {
        // Required empty public constructor
    }

    OnTimePickedListener mCallback;

    public interface OnTimePickedListener {
        void onTimePicked(String event, int hour, int minute);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnTimePickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnTimePickedListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCallback = (OnTimePickedListener)getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, 0, 0,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Bundle bundle = this.getArguments();
        String event = bundle.getString("event");

        if(mCallback != null) {
            mCallback.onTimePicked(event, hour, minute);
        }
    }

}
