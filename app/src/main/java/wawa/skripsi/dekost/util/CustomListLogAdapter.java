package wawa.skripsi.dekost.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.model.Log;

/**
 * Created by Admin on 10/01/2016.
 */
public class CustomListLogAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Log> logitem;
    private Integer type = 1;


    public CustomListLogAdapter(Activity activity, List<Log> movieItems) {
        this.activity = activity;
        this.logitem = movieItems;
    }

    @Override
    public int getCount() {
        return logitem.size();
    }

    @Override
    public Object getItem(int position) {
        return logitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // getting movie data for the row
        Log m = logitem.get(position);


            if(m.getType()== 1) {
                convertView = inflater.inflate(R.layout.template_list_log_row, null);
            }else {
                convertView = inflater.inflate(R.layout.template_list_log_row_red, null);
            }


        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView value = (TextView) convertView.findViewById(R.id.value);

        // title
        description.setText(m.getDescription());

        // rating
        date.setText("Date : " + String.valueOf(m.getDate()));

        value.setText(m.getValue());

        return convertView;
    }

    public void clear(){
        logitem.clear();
    }
}
