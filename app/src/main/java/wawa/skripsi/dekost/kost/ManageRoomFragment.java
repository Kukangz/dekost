package wawa.skripsi.dekost.kost;

/**
 * Created by Admin on 04/01/2016.
 */
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.fabtransitionactivity.SheetLayout;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.model.Kost;
import wawa.skripsi.dekost.util.CustomListSearchAdapter;


public class ManageRoomFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ManageRoomFragment.class.getSimpleName();
    private List<Kost> memberList = new ArrayList<Kost>();
    private ListView listView;
    private CustomListSearchAdapter adapter;
    private FloatingActionButton refresh;
    private CircularProgressView progress;
    private TextView emptyresult;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    private String[] optionlongpress = new String[] { "Deactivate", "Delete"};
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle extras;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manage_room_layout, null);

        extras = getArguments();
        Log.e("extras room", extras.toString());

        FloatingActionButton fabclick = (FloatingActionButton) view.findViewById(R.id.fab);
        mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);
        mSheetLayout.setFab(fabclick);
        mSheetLayout.setFabAnimationEndListener(this);
        progress = (CircularProgressView) view.findViewById(R.id.progress_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        fabclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSheetLayout.expandFab();
            }
        });

        emptyresult = (TextView) view.findViewById(R.id.empty_result);
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListSearchAdapter(this.getActivity(), memberList);
        listView.setAdapter(adapter);

        mProgressDialog = new ProgressDialog(this.getContext());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.select_dialog_item, optionlongpress);

                final View currentListItem = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        TextView title = (TextView) currentListItem.findViewById(R.id.title);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                        switch (which) {
                            case 0:


                                builder.setTitle("Deactivate Room");
                                builder.setMessage("Are you sure to deactivate " + title);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Send message broadcast
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        mProgressDialog = new ProgressDialog(getContext());
                                        mProgressDialog.setIndeterminate(false);
                                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        client.get("https://www.google.com", new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                // called before request is started
                                                mProgressDialog.show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // called when response HTTP status is "200 OK"
                                                Log.i(TAG, "Member Suggestion Success");
                                                mProgressDialog.hide();
                                                Toast.makeText(getContext(), "Member Suggestion Successful", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                Log.i(TAG, "Member Suggestion Failure");
                                                mProgressDialog.hide();
                                            }

                                            @Override
                                            public void onRetry(int retryNo) {
                                                Log.i(TAG, "Retrying");
                                                // called when request is retried
                                            }
                                        });

                                    }
                                });
                                builder.setNegativeButton("No", null);
                                builder.show();
                                break;
                            case 1:

                                builder.setTitle("Delete Room");
                                builder.setMessage("Are you sure to delete " + title);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Send message broadcast
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        mProgressDialog = new ProgressDialog(getContext());
                                        mProgressDialog.setIndeterminate(false);
                                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        client.get("https://www.google.com", new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                // called before request is started
                                                mProgressDialog.show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // called when response HTTP status is "200 OK"
                                                Log.i(TAG, "Member Suggestion Success");
                                                mProgressDialog.hide();
                                                Toast.makeText(getContext(), "Member Suggestion Successful", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                Log.i(TAG, "Member Suggestion Failure");
                                                mProgressDialog.hide();
                                            }

                                            @Override
                                            public void onRetry(int retryNo) {
                                                Log.i(TAG, "Retrying");
                                                // called when request is retried
                                            }
                                        });

                                    }
                                });
                                builder.setNegativeButton("No", null);
                                builder.show();
                                break;
                            default:
                                break;
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                        final View currentListItem = view;
                        TextView title = (TextView) currentListItem.findViewById(R.id.title);
                        Intent intent = new Intent(getActivity(), ManageRoomDetail.class);
                        intent.putExtra("title", title.getText());
                        TextView result = (TextView) currentListItem.findViewById(R.id.allresult);
                        intent.putExtra("detail", result.getText().toString());
                        startActivity(intent);
                    }
                });

        return view;
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
        Log.e(AppController.KOST_ROOM_LIST + uri, "uri test");
        int time = (int) (System.currentTimeMillis());//gets the current time in milliseconds
        JsonArrayRequest memberReq = new JsonArrayRequest(AppController.KOST_ROOM_LIST+uri+"?timestamp=" + String.valueOf(time),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if(response.length() > 0){
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    Kost member = new Kost();
                                    member.setTitle(obj.getString("name"));
                                    member.setThumbnailUrl("http://lorempixel.com/400/400/");
                                    member.setRoom(obj.getString("description"));
                                    member.setLast_update("Last Update : " + obj.getString("last_update_at"));
                                    member.setDescription((obj.getString("member_name").matches("null")) ? "Room is empty" : obj.getString("member_name"));
                                    member.setId(obj.getString("id"));
                                    member.setAllresult(obj);

                                    // adding movie to movies array
                                    memberList.add(member);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            progress.setVisibility(View.GONE);
                        }
                        else
                        {
                            progress.setVisibility(View.GONE);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFabAnimationEnd() {

        Intent intent = new Intent(getActivity(), RoomAdd.class);
        final String uri = extras.getString("kost_id");
        intent.putExtra("kostid", uri);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        //Refresh your stuff here
        getList();
        if(mSheetLayout.isFabExpanded()){
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        adapter.notifyDataSetChanged();

        getList();
    }
}
