package wawa.skripsi.dekost.util;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import wawa.skripsi.dekost.OnTaskCompleted;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.search.MapsBase;

/**
 * Created by Admin on 28/01/2016.
 */
public class LocationPicker extends MapsBase {
    private LatLng latlat;

    private Bundle extras;
    private GoogleMap mMap;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == android.view.KeyEvent.KEYCODE_BACK))
        {
            new AlertDialog.Builder(this)
                    .setMessage("Location will not updated, are you sure?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void startDemo() {

        new UpdateLocation(new OnTaskCompleted() {
            @Override
            public void processFinish(LatLng lat) {
                latlat = lat;
            }
        }).execute();

        extras = getIntent().getExtras();
        if (extras != null) {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(extras.getString("latitude")), Double.valueOf(extras.getString("longitude"))), 18f));
        } else {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latlat, 18f));
        }

        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(new LatLng(Double.valueOf(extras.getString("latitude")), Double.valueOf(extras.getString("longitude"))));

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(extras.getString("latitude") + " : " + extras.getString("longitude"));

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.store_locator_current_position_icon));

        getMap().addMarker(markerOptions);

        getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                getMap().clear();
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.store_locator_current_position_icon));

                getMap().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        final Marker mark = marker;
                        final Bundle bundle = getIntent().getExtras();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LocationPicker.this);
                        builder.setTitle("Save Location");
                        builder.setMessage("Do you want to use this location?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent();
                                //i.replaceExtras(bundle);
                                i.putExtra("latitude", String.valueOf(mark.getPosition().latitude));
                                i.putExtra("longitude", String.valueOf(mark.getPosition().longitude));
                                setResult(-1, i);
                                finish();
                            }
                        });

                        builder.setNegativeButton("No", null);
                        builder.show();
                    }
                });


                // Clears the previously touched position
                getMap().clear();

                // Animating to the touched position
                getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                getMap().addMarker(markerOptions);
            }
        });
    }

    public class UpdateLocation extends AsyncTask<String, LatLng, LatLng> {

        private ProgressDialog pDialog;
        public OnTaskCompleted delegate;

        public UpdateLocation(OnTaskCompleted listener) {
            this.delegate = listener;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LocationPicker.this);
            pDialog.setMessage("Getting Location ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected LatLng doInBackground(String... params) {
            LastLocation location = new LastLocation();

            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("background check", String.valueOf(location.getLatitude()));
            return latlng;
        }

        @Override
        protected void onPostExecute(LatLng result) {
            //Log.e("POST EXECUTE RESULT", String.valueOf(result.latitude));
            delegate.processFinish(result);
            pDialog.dismiss();
        }
    }

    private class LastLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private GoogleApiClient mGoogleApiClient;
        private Location mLastLocation;


        public LastLocation() {
            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(LocationPicker.this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

            mGoogleApiClient.connect();
            while (mGoogleApiClient.isConnecting()) {

            }
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }


        @Override
        public void onConnected(Bundle bundle) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Latitude Connected", String.valueOf(mLastLocation.getLatitude()));
                Log.e("Longitude Connected", String.valueOf(mLastLocation.getLongitude()));
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        private double getLatitude() {
            Log.e("Latitude Connected", String.valueOf(mLastLocation.getLatitude()));

            return mLastLocation.getLatitude();
        }

        private double getLongitude() {
            Log.e("Longitude Connected", String.valueOf(mLastLocation.getLongitude()));
            return mLastLocation.getLongitude();
        }


    }
}
