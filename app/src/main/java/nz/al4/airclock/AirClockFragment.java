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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import org.joda.time.DateTime;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTouchListener} interface
 * to handle interaction events.
 */
public class AirClockFragment extends Fragment {
    public DateTime originTime = new DateTime();
    public DateTime destTime = new DateTime();
    public String effectiveTz = "Error";
    public String direction;

    private OnTouchListener mListener;

    private TimeCalculator timeCalculator;

    private TextClock textClock;
    private TextClock textDate;
    private TextView textTz;

    private int textClockId = new Integer(1);
    private int textDateId = new Integer(2);
    private int textTzId = new Integer(3);

    public AirClockFragment() {
        // Required empty public constructor
    }

    public void updateClock() {
        /**
         * Update the displayed time
         */
        Log.i("updateClock", "Updating clock with origin " + originTime.toString() + ", destination " + destTime.toString());

        // update our TextClock
        timeCalculator = new TimeCalculator(originTime, destTime);
        timeCalculator.setDirection(this.direction);

        effectiveTz = timeCalculator.getEffectiveOffsetText();
        textClock.setTimeZone(effectiveTz);
        textDate.setTimeZone(effectiveTz);
        textTz.setText(effectiveTz);
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
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_air_clock, container, false);
//        clockView = layout;

        layout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                updateClock();
                return true;
            }
        });

        View tc = setupClock();
        View td = setupDate();
        View tz = setupTz();

        layout.addView(td);
        layout.addView(tc);
        layout.addView(tz);

        updateClock();

        return layout;
    }

    private View setupClock() {
        // add digital clock
        textClock = new TextClock(getContext());
        textClock.setId(textClockId);

        textClock.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        textClock.setTypeface(textClock.getTypeface(), Typeface.BOLD);
        textClock.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
        textClock.setGravity(Gravity.CENTER_HORIZONTAL);

        return textClock;
    }

    private View setupDate() {
        // add digital date
        textDate = new TextClock(getContext());
        textDate.setId(textDateId);
        RelativeLayout.LayoutParams textDateParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textDateParams.addRule(RelativeLayout.BELOW, textClockId);
        textDate.setLayoutParams(textDateParams);
        textDate.setTypeface(textDate.getTypeface(), Typeface.BOLD);
        textDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textDate.setGravity(Gravity.CENTER_HORIZONTAL);

        SpannableString dateSpan = new SpannableString("yyyy/MM/dd");
        textDate.setFormat24Hour(dateSpan);
        textDate.setFormat12Hour(dateSpan);

        return textDate;
    }

    private View setupTz() {
        // add time zone
        textTz = new TextView(getContext());
        textTz.setId(textTzId);

        RelativeLayout.LayoutParams textTzParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textTzParams.addRule(RelativeLayout.BELOW, textDateId);
        textTz.setLayoutParams(textTzParams);
        textTz.setTypeface(textTz.getTypeface(), Typeface.BOLD);
        textTz.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textTz.setGravity(Gravity.CENTER_HORIZONTAL);

        return textTz;
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
