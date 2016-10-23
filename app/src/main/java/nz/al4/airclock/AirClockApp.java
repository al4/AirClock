package nz.al4.airclock;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by alex on 23/10/2016.
 */

public class AirClockApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
