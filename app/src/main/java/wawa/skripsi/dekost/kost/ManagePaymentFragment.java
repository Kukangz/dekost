package wawa.skripsi.dekost.kost;

/**
 * Created by Admin on 04/01/2016.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.fabtransitionactivity.SheetLayout;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.model.Log;
import wawa.skripsi.dekost.util.CustomListLogAdapter;


public class ManagePaymentFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener,SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = ManagePaymentFragment.class.getSimpleName();
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Log> memberList = new ArrayList<Log>();
    private ListView listView;
    private CustomListLogAdapter adapter;
    private FloatingActionButton refresh;
    private CircularProgressView progress;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    private TextView emptyresult;
    private int type = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle extras;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manage_payment_layout,null);

        extras = getArguments();
        android.util.Log.e("extras room", extras.toString());

        final FloatingActionButton fabclick = (FloatingActionButton) view.findViewById(R.id.fab);
        final FloatingActionButton fabrules = (FloatingActionButton) view.findViewById(R.id.fab_add_type);
        mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        mSheetLayout.setFabAnimationEndListener(this);
        progress = (CircularProgressView) view.findViewById(R.id.progress_view);
        emptyresult = (TextView) view.findViewById(R.id.empty_result);

        fabclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSheetLayout.setFab(fabclick);
                mSheetLayout.expandFab();
                type = 1;
            }
        });

        fabrules.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mSheetLayout.setFab(fabrules);
                mSheetLayout.expandFab();
                type = 2;
            }
        });


        listView = (ListView) view.findViewById(R.id.list);


        adapter = new CustomListLogAdapter(this.getActivity(), memberList);
        listView.setAdapter(adapter);

        adapter.clear();

        return view;
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent;
        switch(type)
        {
            case 1:
                intent = new Intent(getActivity(), PaymentAdd.class);
                intent.putExtra("kost_id", extras.getString("kost_id"));
                intent.putExtra("no_member", true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case 2:

                intent = new Intent(getActivity(), PaymentTypeAdd.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;



        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }

    private void getList(){
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.notifyDataSetChanged();
        // Creating volley request obj
        refresh.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        emptyresult.setVisibility(View.GONE);

        final String uri = extras.getString("kost_id");
        android.util.Log.e(AppController.KOST_PAYMENT_LIST + uri, "uri test");
        int time = (int) (System.currentTimeMillis());//gets the current time in milliseconds
        // Creating volley request obj
        // Creating volley request obj
        JsonArrayRequest memberReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        android.util.Log.d(TAG, response.toString());
                        if(response.length() > 0){
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    Log log = new Log();
                                    log.setDescription(obj.getString("description"));
                                    log.setDate(obj.getString("create_at"));
                                    log.setValue(obj.getString("amount"));
                                    log.setType(Integer.valueOf(obj.getString("type")));
                                    // adding movie to movies array
                                    memberList.add(log);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        else
                        {
                            listView.setVisibility(View.GONE);
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
                refresh.setVisibility(View.VISIBLE);

            }
        });

        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
        RetryPolicy rp = new DefaultRetryPolicy(15000,3,0);
        memberReq.setRetryPolicy(rp);
        AppController.getInstance().addToRequestQueue(memberReq);

    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        if(mSheetLayout.isFabExpanded()){
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.notifyDataSetChanged();

        getList();


    }
}
