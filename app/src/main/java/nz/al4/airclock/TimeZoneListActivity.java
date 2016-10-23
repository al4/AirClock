package nz.al4.airclock;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class TimeZoneListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_time_zone_list);
        List values = new TimeZoneList().getTimeZoneList();

        Context context = getApplicationContext();
        String s = values.toString();

        setListAdapter(new TimeZoneListAdapter(this, values));
    }
}
