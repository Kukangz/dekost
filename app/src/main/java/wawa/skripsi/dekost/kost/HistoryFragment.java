package wawa.skripsi.dekost.kost;

/**
 * Created by Admin on 04/01/2016.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
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
import wawa.skripsi.dekost.model.History;
import wawa.skripsi.dekost.util.CustomListHistoryAdapter;


public class HistoryFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener{

    private static final String TAG = ManagePaymentFragment.class.getSimpleName();
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<History> history = new ArrayList<History>();
    private ListView listView;
    private CustomListHistoryAdapter adapter;
    private FloatingActionButton refresh;
    private CircularProgressView progressview;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    private TextView emptyresult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manage_history_layout,null);

        mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);
        progressview = (CircularProgressView) view.findViewById(R.id.progress_view);
        emptyresult = (TextView) view.findViewById(R.id.empty_result);


        listView = (ListView) view.findViewById(R.id.list);


        adapter = new CustomListHistoryAdapter(this.getActivity(), history);
        listView.setAdapter(adapter);

        adapter.clear();

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
                                    History log = new History();
                                    log.setDescription(obj.getString("title"));
                                    log.setDate(obj.getString("releaseYear"));
                                    log.setValue(obj.getString("rating"));
                                    // adding movie to movies array
                                    history.add(log);

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
                progressview.setVisibility(View.GONE);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
            }
        });



        AppController.getInstance().addToRequestQueue(memberReq);



        return view;
    }

    @Override
    public void onFabAnimationEnd() {

    }

    private void getList(){
        // Creating volley request obj
        adapter.clear();
        refresh.setVisibility(View.INVISIBLE);
        progressview.setVisibility(View.VISIBLE);
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
                                    History member = new History();
                                    member.setDescription(obj.getString("title"));
                                    member.setDate(obj.getString("releaseYear"));
                                    member.setValue(obj.getString("rating"));


                                    // adding movie to movies array
                                    history.add(member);

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
                progressview.setVisibility(View.GONE);
            }
        });
        AppController.getInstance().addToRequestQueue(memberReq);
    }
}
