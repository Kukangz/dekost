package wawa.skripsi.dekost.search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.ViewResult;
import wawa.skripsi.dekost.model.KostLocation;
import wawa.skripsi.dekost.util.MultiDrawable;

/**
 * Demonstrates heavy customisation of the look of rendered clusters.
 */
public class MapResult extends MapsBase implements ClusterManager.OnClusterClickListener<KostLocation>, ClusterManager.OnClusterInfoWindowClickListener<KostLocation>, ClusterManager.OnClusterItemClickListener<KostLocation>, ClusterManager.OnClusterItemInfoWindowClickListener<KostLocation> {
    private ClusterManager<KostLocation> mClusterManager;
    private Random mRandom = new Random(1984);
    private Bundle extras;
    private static final int REQUEST_CODE = 1;

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class KostRenderer extends DefaultClusterRenderer<KostLocation> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public KostRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(KostLocation kostlocation, MarkerOptions markerOptions) {
            // Draw a single kost.
            // Set the info window to show their name.
            mImageView.setImageResource(kostlocation.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(kostlocation.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<KostLocation> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (KostLocation p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<KostLocation> cluster) {
        // Show a toast with some info when the cluster is clicked.
        /*String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();*/
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 18f));
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<KostLocation> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(KostLocation item) {
        // Does nothing, but you could go into the user's profile page, for example.

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(KostLocation item) {
        Log.e("item id", String.valueOf(item.id));
        if(item.id!= 0) {
            Intent intent = new Intent(this, ViewResult.class);
            intent.putExtra("kost_id", item.id);
            startActivityForResult(intent, REQUEST_CODE);

        }
    }

    @Override
    protected void startDemo() {

        extras = getIntent().getExtras();
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(extras.getString("latitude")), Double.valueOf(extras.getString("longitude"))), 18f));

        mClusterManager = new ClusterManager<KostLocation>(this, getMap());
        mClusterManager.setRenderer(new KostRenderer());
        mClusterManager.addItem(new KostLocation(0,new LatLng(Double.valueOf(extras.getString("latitude")), Double.valueOf(extras.getString("longitude"))), "Current Location", R.drawable.store_locator_current_position_icon));

        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private void addItems() {
        ArrayList<String> list = extras.getStringArrayList("location");
        JSONObject json;
        for (String obj: list) {
            try {
                json = new JSONObject(obj);
                mClusterManager.addItem(new KostLocation(json.getInt("id"),new LatLng(Double.valueOf(json.getString("latitude")),Double.valueOf(json.getString("longitude"))), json.getString("name"), R.drawable.home_icon));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
      /*  // http://www.flickr.com/photos/sdasmarchives/5036248203/
        mClusterManager.addItem(new KostLocation(1,position(), "Walter", R.drawable.home_icon));

        // http://www.flickr.com/photos/usnationalarchives/4726917149/
        mClusterManager.addItem(new KostLocation(2,position(), "Gran", R.drawable.home_icon));

        // http://www.flickr.com/photos/nypl/3111525394/
        mClusterManager.addItem(new KostLocation(3,position(), "Ruth", R.drawable.home_icon));

        // http://www.flickr.com/photos/smithsonian/2887433330/
        mClusterManager.addItem(new KostLocation(4,position(), "Stefan", R.drawable.home_icon));

        // http://www.flickr.com/photos/library_of_congress/2179915182/
        mClusterManager.addItem(new KostLocation(5,position(), "Mechanic", R.drawable.home_icon));

        // http://www.flickr.com/photos/nationalmediamuseum/7893552556/
        mClusterManager.addItem(new KostLocation(6,position(), "Yeats", R.drawable.home_icon));

        // http://www.flickr.com/photos/sdasmarchives/5036231225/
        mClusterManager.addItem(new KostLocation(7,position(), "John", R.drawable.home_icon));

        // http://www.flickr.com/photos/anmm_thecommons/7694202096/
        mClusterManager.addItem(new KostLocation(8,position(), "Trevor the Turtle", R.drawable.home_icon));

        // http://www.flickr.com/photos/usnationalarchives/4726892651/
        mClusterManager.addItem(new KostLocation(9,position(), "Teach", R.drawable.home_icon));*/
    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}