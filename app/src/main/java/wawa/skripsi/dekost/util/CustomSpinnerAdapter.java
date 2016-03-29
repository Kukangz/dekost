package wawa.skripsi.dekost.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Admin on 14/01/2016.
 */
public class CustomSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<Pair<Integer, String>> mData;

    public CustomSpinnerAdapter(Context context, List<Pair<Integer, String>> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Pair<Integer, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
            view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

        String text = getItem(position).second;


        ((TextView) view.findViewById(android.R.id.text1)).setText(text);
        ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);;


        return view;
    }

    public void clear() {
        // TODO Auto-generated method stub
        mData.clear();

    }

    public void add(Pair<Integer, String> Ex)
    {
        mData.add(Ex);
    }

    public String getText(int position){
        return getItem(position).second;
    }
}
