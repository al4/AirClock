package nz.al4.airclock;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import nz.al4.airclock.TimeZoneList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class TimeZonePickerFragment extends DialogFragment {

    private Spinner spinner;
    private String[] timeZones = new TimeZoneList().getTimeZoneOffsets();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimeZonePickerFragment() {
    }

    OnZonePickedListener mCallback;

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

    @SuppressWarnings("unused")
    public static TimeZonePickerFragment newInstance(String event) {
        TimeZonePickerFragment fragment = new TimeZonePickerFragment();
        Bundle args = new Bundle();
        args.putString("event", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mEvent;

        if (getArguments() != null) {
            mEvent = getArguments().getString("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spinner_timezone, container, false);
        setSpinnerContent(view);

        // Set the adapter
        return view;
    }

    private void setSpinnerContent (View view) {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_timezone);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                this.timeZones);
    }

}