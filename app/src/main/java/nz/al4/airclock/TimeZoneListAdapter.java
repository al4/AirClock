package nz.al4.airclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alex on 23/10/2016.
 */

public class TimeZoneListAdapter extends ArrayAdapter {
    private final Context context;
    private final List values;

    public TimeZoneListAdapter(Context context, List values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_time_zone_list, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.time_zone_content);
        textView.setText(values.get(0).toString());

        return rowView;
    }

}
