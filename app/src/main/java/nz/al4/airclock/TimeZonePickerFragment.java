package nz.al4.airclock;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;



/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class TimeZonePickerFragment extends ListFragment
    {

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
        View view = inflater.inflate(R.layout.fragment_timezone, container, false);

        ListAdapter TimeZonesAdapter = new SimpleAdapter(
                getContext(),
                new TimeZoneList().getTimeZoneList(),
                R.layout.fragment_timezone,
                new String[] {"timezone"},
                new int[] {R.id.fragment_timezone});

        // Set the adapter
        return view;
    }



}
