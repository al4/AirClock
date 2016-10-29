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

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTouchListener} interface
 * to handle interaction events.
 */
public class AirClockFragment extends Fragment {
    public DateTime originTime = new DateTime();
    public DateTime destTime = new DateTime();

    private OnTouchListener mListener;

    private TimeCalculator timeCalculator = new TimeCalculator();

    private View clockView;
    private TextView clockText;

    public AirClockFragment() {
        // Required empty public constructor
    }

    public void updateClock() {
        /**
         * Update the displayed time
         */
        DateTime effectiveTime = timeCalculator.getEffectiveTime(originTime, destTime);
        DateTime currentTime = new DateTime();

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        String StringEffectiveTime = fmt.print(effectiveTime);

        String text = "Current effective time is: " + StringEffectiveTime;

        clockText.setText(text);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_air_clock, container, false);
        clockView = view;

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                updateClock();
                return true;
            }
        });

        clockText = (TextView) view.findViewById(R.id.clock_text);

        // set the initial clock
        updateClock();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onClockTouch();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTouchListener) {
            mListener = (OnTouchListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTouchListener");
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
    public interface OnTouchListener {
        // TODO: Update argument type and name
        void onClockTouch();
    }
}
