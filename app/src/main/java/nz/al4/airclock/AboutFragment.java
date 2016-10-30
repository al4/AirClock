package nz.al4.airclock;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends AppCompatDialogFragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Material_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("About");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        View tv_version = v.findViewById(R.id.version_text);
        ((TextView)tv_version).setText(Html.fromHtml(
                "<p>AirClock Version " + BuildConfig.VERSION_NAME + "</p>"));

        View tv_about_text = v.findViewById(R.id.fragment_about_text);
        ((TextView)tv_about_text).setText(Html.fromHtml(getString(R.string.html_about_content)));

        return v;
    }

}
