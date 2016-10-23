package nz.al4.airclock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class TimeZonePickerFragment extends DialogFragment
    implements AdapterView.OnItemSelectedListener {

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
        mCallback = (OnZonePickedListener) getActivity();
        View view = inflater.inflate(R.layout.fragment_timezone, container, false);
        getDialog().setTitle("Select Time Zone offset");

        Button button_ok = (Button) view.findViewById(R.id.timezone_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        setSpinnerContent(view);

        // Set the adapter
        return view;
    }

    private void setSpinnerContent (View view) {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_timezone);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, new TimeZoneList().getTimeZoneOffsets());
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        int offset = Integer.valueOf(obj.toString());

        Bundle bundle = this.getArguments();
        String event = bundle.getString("event");
        mCallback.onZonePicked(event, offset);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
