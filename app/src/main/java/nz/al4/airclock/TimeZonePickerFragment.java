package nz.al4.airclock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;


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
            .setSingleChoiceItems(timeZones, 13, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    selected_zone = timeZones[id];
                    Context context = getContext();
                    Toast.makeText(context, "selected " + selected_zone, Toast.LENGTH_LONG).show();
                }

            })
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
