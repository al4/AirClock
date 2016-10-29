package nz.al4.airclock;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.MutableDateTime;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnClockInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AirClockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AirClockFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ORIGIN = "originTime";
    private static final String ARG_DEST = "destTime";

    private Long mOriginTime;
    private Long mDestTime;

    public MutableDateTime ORIGIN_TIME = new MutableDateTime();
    public MutableDateTime DEST_TIME = new MutableDateTime();

    private OnClockInteractionListener mListener;

    public AirClockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param originTime Origin departure time.
     * @param destTime Destination arrival time.
     * @return A new instance of fragment AirClockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AirClockFragment newInstance(MutableDateTime originTime, MutableDateTime destTime) {
        AirClockFragment fragment = new AirClockFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ORIGIN, originTime.getMillis());
        args.putLong(ARG_DEST, destTime.getMillis());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOriginTime = getArguments().getLong(ARG_ORIGIN);
            mDestTime = getArguments().getLong(ARG_DEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_air_clock, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onClockInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClockInteractionListener) {
            mListener = (OnClockInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClockInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnClockInteractionListener {
        // TODO: Update argument type and name
        void onClockInteraction(Uri uri);
    }
}
