package wawa.skripsi.dekost.search;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.fabtransitionactivity.SheetLayout;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.OnTaskCompleted;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.ViewResult;
import wawa.skripsi.dekost.model.Kost;
import wawa.skripsi.dekost.util.CustomListSearchAdapter;
import wawa.skripsi.dekost.util.JSONParser;


/**
 * Created by Admin on 12/01/2016.
 */
public class SearchResult extends Activity implements SheetLayout.OnFabAnimationEndListener, OnTaskCompleted, Serializable, SwipeRefreshLayout.OnRefreshListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = SearchResult.class.getSimpleName();
    private static final String url = "http://kukang.bahasbuku.com/v1/kost/search";
    private ProgressDialog pDialog;
    private List<Kost> memberList = new ArrayList<Kost>();
    private ArrayList<String> passresult = new ArrayList<String>();
    private ListView listView;
    private CustomListSearchAdapter adapter;
    private FloatingActionButton refresh;
    private CircularProgressView progressview;
    private TextView emptyresult;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    public Bundle extras;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_layout);

        extras = getIntent().getExtras();
        if (extras == null) {
            extras = new Bundle();
        }

        getLocation();




        FloatingActionButton fabclick = (FloatingActionButton) findViewById(R.id.fab);
        mSheetLayout = (SheetLayout) findViewById(R.id.bottom_sheet);
        refresh = (FloatingActionButton) findViewById(R.id.refresh);
        mSheetLayout.setFab(fabclick);
        mSheetLayout.setFabAnimationEndListener(this);
        progressview = (CircularProgressView) findViewById(R.id.progress_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getList();
                                    }
                                }
        );

        fabclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSheetLayout.expandFab();
            }
        });


        emptyresult = (TextView) findViewById(R.id.empty_result);
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListSearchAdapter(this, memberList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String[] option = new String[]{"View Detail", "Call Owner", "View Maps"};

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResult.this,
                        android.R.layout.select_dialog_item, option);
                final View v = view;

                AlertDialog.Builder builder = new AlertDialog.Builder(SearchResult.this);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(SearchResult.this, ViewResult.class);
                                TextView targetid = (TextView) v.findViewById(R.id.target_id);
                                intent.putExtra("kost_id", targetid.getText().toString());
                                startActivityForResult(intent, REQUEST_CODE);
                                break;
                            case 1:
                                // Call member
                                new AlertDialog.Builder(SearchResult.this)
                                        .setMessage("Doing phone call, are you sure?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                callIntent.setData(Uri.parse("tel:" + v.findViewById(R.id.rating)));
                                                if (ActivityCompat.checkSelfPermission(SearchResult.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                    // TODO: Consider calling
                                                    //    ActivityCompat#requestPermissions
                                                    // here to request the missing permissions, and then overriding
                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    //                                          int[] grantResults)
                                                    // to handle the case where the user grants the permission. See the documentation
                                                    // for ActivityCompat#requestPermissions for more details.
                                                    return;
                                                }
                                                startActivity(callIntent);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                break;
                            case 2:
                                Intent i = new Intent(SearchResult.this, MapResult.class);
                                ArrayList<String> pass = new ArrayList<String>();
                                pass.add(passresult.get(position));
                                i.putExtra("latitude", extras.getString("latitude"));
                                i.putExtra("longitude", extras.getString("longitude"));
                                i.putStringArrayListExtra("location", pass);
                                startActivity(i);
                                break;
                        }
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        adapter.clear();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pDialog = new ProgressDialog(SearchResult.this);
        pDialog.setMessage("Getting Location");
        pDialog.setCancelable(true);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        pDialog.show();

    }


    private void getList() {

        JSONParser jParser = new JSONParser();

        Bundle bundle = extras;
        String uri = "?";
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (!value.toString().equalsIgnoreCase("") || value.toString() != null) {
                uri += key + "=" + value.toString() + "&";
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        int time = (int) (System.currentTimeMillis());//gets the current time in milliseconds
        uri += "timestamp="+ String.valueOf(time);

        passresult.clear();
        refresh.setVisibility(View.GONE);
        Log.e("url result", uri);

        JsonArrayRequest memberReq = new JsonArrayRequest(url + uri,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    Kost member = new Kost();
                                    member.setTitle(obj.getString("name"));
                                    member.setThumbnailUrl(obj.getString("image"));
                                    member.setRoom(obj.getString("address"));
                                    member.setLast_update(obj.getString("price"));
                                    member.setDescription(obj.getString("description"));
                                    member.setId(obj.getString("id"));
                                    member.setAllresult(obj);
                                    passresult.add(obj.toString());


                                    // adding movie to movies array
                                    memberList.add(member);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            progressview.setVisibility(View.GONE);
                            emptyresult.setVisibility(View.VISIBLE);
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //refresh.setVisibility(View.VISIBLE);
            }
        });

        AppController.getInstance().addToRequestQueue(memberReq);
    }

    private void addtoRequest(JsonArrayRequest memberReq){
        AppController.getInstance().addToRequestQueue(memberReq);
    }


    @Override
    public void onFabAnimationEnd() {

        Intent intent = new Intent(this, MapResult.class);
        intent.putExtra("latitude", extras.getString("latitude"));
        intent.putExtra("longitude", extras.getString("longitude"));
        intent.putStringArrayListExtra("location", passresult);

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        if (mSheetLayout.isFabExpanded()) {
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.disconnect();
    }

    @Override
    public void processFinish(LatLng lat) {
        Log.e("lat Last", String.valueOf(lat.latitude));
        extras.putString("latitude", String.valueOf(lat.latitude));
        extras.putString("longitude", String.valueOf(lat.longitude));

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.notifyDataSetChanged();
        getList();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(memberList.size() <= 1) {
            pDialog.dismiss();
            extras.putString("latitude", String.valueOf(location.getLatitude()));
            extras.putString("longitude", String.valueOf(location.getLongitude()));
            new KostParse().execute(extras);
        }
    }

    public void getLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(10);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class KostParse extends AsyncTask<Bundle, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter.clear();
            adapter.notifyDataSetChanged();
            emptyresult.setVisibility(View.GONE);

            pDialog = new ProgressDialog(SearchResult.this);
            pDialog.setMessage("Getting Result ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            progressview.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);


        }

        @Override
        protected String doInBackground(Bundle... args) {

            JSONParser jParser = new JSONParser();

            Bundle bundle = args[0];
            String uri = "?";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                uri += key+"="+value.toString()+"&";
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }

            Log.e("url result", uri);

            getList();

            return uri;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            swipeRefreshLayout.setRefreshing(false);
            pDialog.dismiss();
            adapter.notifyDataSetChanged();
            progressview.setVisibility(View.GONE);
            emptyresult.setVisibility(View.VISIBLE);
        }
    }


}










