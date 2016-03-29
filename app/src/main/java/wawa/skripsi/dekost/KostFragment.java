package wawa.skripsi.dekost;

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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import wawa.skripsi.dekost.kost.KostPreferences;
import wawa.skripsi.dekost.kost.KostRegister;
import wawa.skripsi.dekost.kost.ManageMenu;
import wawa.skripsi.dekost.model.Kost;
import wawa.skripsi.dekost.util.CustomListSearchAdapter;


public class KostFragment extends Fragment  implements SheetLayout.OnFabAnimationEndListener, SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = KostFragment.class.getSimpleName();


    private ProgressDialog pDialog;
    private List<Kost> memberList = new ArrayList<Kost>();
    private ListView listView;
    private CustomListSearchAdapter adapter;
    private FloatingActionButton refresh;

    private TextView emptyresult;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    private final String[] option = new String[] { "Broadcast message", "Manage","Settings"};
    private final String[] optionlongpress = new String[] { "Toggle Looking Member", "Deactivate"};
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressView progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kost_layout,null);


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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.select_dialog_item, option);
                final View currentListItem = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TextView title = (TextView) currentListItem.findViewById(R.id.title);
                        TextView id = (TextView) currentListItem.findViewById(R.id.target_id);
                        TextView result = (TextView) currentListItem.findViewById(R.id.allresult);
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                //Broadcast Message

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                                builder.setTitle("Send Message");
                                // Set up the input
                                final EditText input = new EditText(getContext());

                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                                input.setMinLines(4);
                                builder.setView(input);
                                // Set up the buttons
                                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Send message broadcast
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.setMaxRetriesAndTimeout(5, 10000);
                                        Log.e("url test", AppController.KOST_BROADCAST_URL);
                                        String uri = "?message="+input.getText().toString()+"&kost="+view.getId();
                                        client.get(AppController.KOST_BROADCAST_URL+uri, new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                // called before request is started
                                                mProgressDialog.setTitle("Sending Broadcast Message..");
                                                mProgressDialog.setMessage("Please Wait..");
                                                mProgressDialog.show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // called when response HTTP status is "200 OK"
                                                Log.i(TAG, "Register Success");
                                                mProgressDialog.hide();
                                                Toast.makeText(getContext(), "Broadcast Message Send!", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                Log.i(TAG, "Broadcast Message Failure");
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
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                                break;
                            case 1:
                                //Manage
                                Intent intent = new Intent(getActivity(), ManageMenu.class);
                                intent.putExtra("title", title.getText());
                                intent.putExtra("kost_id", id.getText());
                                startActivityForResult(intent, REQUEST_CODE);

                                break;
                            case 2:
                                //Manage Setting
                                Intent i = new Intent(getActivity(), KostPreferences.class);
                                i.putExtra("result", result.getText().toString());
                                startActivity(i);
                                break;
                            default:
                                break;
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.select_dialog_item, optionlongpress);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        return view;
    }

    private void getList(){
 /*       final ProgressDialog pd = new ProgressDialog(getContext());

        pd.setMessage("Please Wait...");
        pd.show();*/
        // Creating volley request obj
        refresh.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        emptyresult.setVisibility(View.GONE);
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.notifyDataSetChanged();



        int time = (int) (System.currentTimeMillis());//gets the current time in milliseconds
        Log.e("url", AppController.KOST_LIST+AppController.getInstance().getId()+"?timestamp=" + String.valueOf(time));
        JsonArrayRequest memberReq = new JsonArrayRequest(AppController.KOST_LIST+AppController.getInstance().getId()+"?timestamp=" + String.valueOf(time),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
                        if(response.length() > 0){
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    Kost member = new Kost();
                                    member.setTitle(obj.getString("name"));
                                    member.setThumbnailUrl(obj.getString("image"));
                                    member.setRoom("Room count : " + obj.getString("room_total"));
                                    member.setLast_update("Last Update : " + obj.getString("last_update_at"));
                                    member.setDescription(obj.getString("description"));
                                    member.setId(obj.getString("id"));
                                    member.setAllresult(obj);

                                    // adding movie to movies array
                                    memberList.add(member);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                adapter.notifyDataSetChanged();
                            }

                            progress.setVisibility(View.GONE);

                        }
                        else
                        {
                            progress.setVisibility(View.GONE);
                            emptyresult.setVisibility(View.VISIBLE);

                        }
//                        pd.dismiss();
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
                refresh.setVisibility(View.VISIBLE);
//                pd.dismiss();
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
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onFabAnimationEnd() {

        Intent intent = new Intent(getActivity(), KostRegister.class);
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
        if(mSheetLayout.isFabExpanded()){
            mSheetLayout.contractFab();
        }
        AppController.getInstance().getRequestQueue().getCache().clear();
        getList();
    }

    @Override
    public void onRefresh() {
        getList();
    }
}
