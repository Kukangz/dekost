package wawa.skripsi.dekost.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.List;

import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.model.Kost;

public class CustomListSearchAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Kost> movieItems;
    public JSONObject result;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListSearchAdapter(Activity activity, List<Kost> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
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
        if (convertView == null)
            convertView = inflater.inflate(R.layout.template_list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);
        TextView id = (TextView) convertView.findViewById(R.id.target_id);
        TextView allresult = (TextView) convertView.findViewById(R.id.allresult);

        // getting movie data for the row
        Kost m = movieItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);


        result = m.getAllresult();
        allresult.setText(result.toString());

        // title
        title.setText(m.getTitle());

        // rating
        rating.setText(String.valueOf(m.getRoom()));

        // genre
        genre.setText(m.getDescription());

        // release year
        year.setText(String.valueOf(m.getLast_update()));

        id.setText(String.valueOf(m.getItem()));



        return convertView;
    }

    public void clear(){
        movieItems.clear();
    }

}