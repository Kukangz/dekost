package wawa.skripsi.dekost.kost;

/**
 * Created by Admin on 04/01/2016.
 */
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
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
import fr.ganfra.materialspinner.MaterialSpinner;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.ViewResult;
import wawa.skripsi.dekost.model.Kost;
import wawa.skripsi.dekost.util.CustomListSearchAdapter;
import wawa.skripsi.dekost.util.CustomSpinnerAdapter;


public class ManageMemberFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, SwipeRefreshLayout.OnRefreshListener{


    private static final String TAG = ManageMemberFragment.class.getSimpleName();
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Kost> memberList = new ArrayList<Kost>();
    private ListView listView;
    private CustomListSearchAdapter adapter;
    private FloatingActionButton refresh;
    private CircularProgressView progress;
    private TextView emptyresult;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    private final String[] option = new String[] { "Send message","Call Member", "Add Payment","View Profile","View Payment Record","Assign to Room","Release Member"};
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle extras;
    private CustomSpinnerAdapter room_adapter;

    private List<Pair<Integer,String>> room_list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manage_member_layout,null);

        extras = getArguments();
        Log.e("extras room", extras.toString());

        FloatingActionButton fabclick = (FloatingActionButton) view.findViewById(R.id.fab);
        mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);
        mSheetLayout.setFab(fabclick);
        mSheetLayout.setFabAnimationEndListener(this);
        progress = (CircularProgressView) view.findViewById(R.id.progress_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        room_list = new ArrayList<>();
        getRoom();

        room_adapter = new CustomSpinnerAdapter(getActivity(), room_list);

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


        adapter.clear();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.select_dialog_item, option);
                final View currentListItem = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TextView username = (TextView) view.findViewById(R.id.title);
                        // TODO Auto-generated method stub
                        final TextView id = (TextView) view.findViewById(R.id.target_id);
                        Intent e;

                        final AsyncHttpClient client = new AsyncHttpClient();
                        client.setMaxRetriesAndTimeout(3, 15000);
                        switch (which) {
                            case 0:
                                //Send Message

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                                builder.setTitle("Send Message to " + username.getText());
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

                                        String uri = "?";
                                        uri += "message="+input.getText().toString();
                                        uri += "&member="+ id.getText().toString();
                                        uri += "&source="+AppController.getInstance().getId();
                                        Log.e("message", AppController.KOST_MEMBER_MESSAGE+ uri);
                                        client.get(AppController.KOST_MEMBER_MESSAGE + uri, new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                // called before request is started
                                                mProgressDialog = new ProgressDialog(getActivity());
                                                mProgressDialog.setMessage("Sending Message");
                                                mProgressDialog.show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // called when response HTTP status is "200 OK"
                                                Log.i(TAG, "Member Message Sucess");
                                                mProgressDialog.hide();
                                                Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                Log.i(TAG, "Member Message Failed");
                                                mProgressDialog.hide();
                                                Toast.makeText(getActivity(), "Message Not Sent", Toast.LENGTH_SHORT).show();
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
                                // Call member
                                final TextView phonenumber = (TextView) currentListItem.findViewById(R.id.rating);
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Doing phone call, are you sure?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                callIntent.setData(Uri.parse("tel:" + phonenumber.getText().toString()));
                                                startActivity(callIntent);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                break;
                            case 2:
                                // Payment
                                e = new Intent(getActivity(), PaymentAdd.class);
                                e.putExtra("userid", id.getText().toString());
                                e.putExtra("username", username.getText().toString());
                                e.putExtra("kost_id", extras.getString("kost_id"));
                                startActivity(e);
                                break;
                            case 3:
                                //View Profile
                                e = new Intent(getActivity(), ViewResult.class);
                                e.putExtra("title", username.getText()+" Profile");

                                startActivity(e);
                                break;
                            case 4:
                                //View Profile
                                e = new Intent(getActivity(), ViewResult.class);
                                e.putExtra("payment", username.getText()+" Payment Record");
                                startActivity(e);
                                break;

                            case 5:
                                // Assign Member to Room

                                final MaterialSpinner spinner = new MaterialSpinner(getActivity());
                                spinner.setAdapter(room_adapter);
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        room_adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                room_adapter.notifyDataSetChanged();
                                        new AlertDialog.Builder(getActivity())
                                        .setView(spinner)
                                .setMessage("Select room for " + username.getText())
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int er) {
                                                // send to server to kick this guy from this kost.
                                                String room_id = room_adapter.getItem(spinner.getSelectedItemPosition()).first.toString();
                                                final String kost = extras.getString("kost_id");
                                                String uri = "?kost="+kost+"&id="+room_id+"&member="+id.getText().toString();
                                                Log.e("onAccept",uri);
                                                client.get(AppController.KOST_MEMBER_ASSIGN+ uri, new AsyncHttpResponseHandler() {

                                                    @Override
                                                    public void onStart() {
                                                        // called before request is started
                                                        mProgressDialog = new ProgressDialog(getActivity());
                                                        mProgressDialog.setMessage("Please Wait...");
                                                        mProgressDialog.show();
                                                    }

                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                        // called when response HTTP status is "200 OK"
                                                        Log.i(TAG, "Member Message Sucess");
                                                        mProgressDialog.hide();
                                                        Toast.makeText(getActivity(), "Member Released", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                        Log.i(TAG, "Member Message Failed");
                                                        mProgressDialog.hide();
                                                        Toast.makeText(getActivity(), "Member Release Failed", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onRetry(int retryNo) {
                                                        Log.i(TAG, "Retrying");
                                                        // called when request is retried
                                                    }
                                                });

                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                break;

                            case 6:
                                // Release Member
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Are you sure you want to release " + username.getText() + " as Member from "+username.getText().toString()+" ?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int er) {
                                                // send to server to kick this guy from this kost.

                                                final String kost = extras.getString("kost_id");
                                                String uri = "?kost="+kost+"&member="+id.getText().toString();
                                                Log.e("onRelease",uri);
                                                client.get(AppController.KOST_MEMBER_RELEASE+uri, new AsyncHttpResponseHandler() {

                                                    @Override
                                                    public void onStart() {
                                                        // called before request is started
                                                        mProgressDialog = new ProgressDialog(getActivity());
                                                        mProgressDialog.setMessage("Please Wait...");
                                                        mProgressDialog.show();
                                                    }

                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                        // called when response HTTP status is "200 OK"
                                                        Log.i(TAG, "Member Message Sucess");
                                                        mProgressDialog.hide();
                                                        Toast.makeText(getActivity(), "Member Released", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                        Log.i(TAG, "Member Message Failed");
                                                        mProgressDialog.hide();
                                                        Toast.makeText(getActivity(), "Member Release Failed", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onRetry(int retryNo) {
                                                        Log.i(TAG, "Retrying");
                                                        // called when request is retried
                                                    }
                                                });

                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
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
        JsonArrayRequest memberReq = new JsonArrayRequest(AppController.KOST_MEMBER_LIST+uri+"?timestamp=" + String.valueOf(time),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if(response.length() > 0){
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    Kost member = new Kost();

                                    member.setTitle(obj.getString("name"));
                                    member.setThumbnailUrl(obj.getString("image"));
                                    member.setRoom(obj.getString("phone"));
                                    member.setLast_update("Last seen : " + obj.getString("last_login"));
                                    member.setDescription((obj.getString("gender").matches("1")) ? "Male" : "Female");
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

        Intent intent = new Intent(getActivity(), MemberAdd.class);
        intent.putExtra("kost_id", extras.getString("kost_id"));
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

    private void getRoom(){
        final String uri = extras.getString("kost_id");
        Log.e(AppController.KOST_ROOM_LIST + uri, "uri test");
        int time = (int) (System.currentTimeMillis());//gets the current time in milliseconds
        JsonArrayRequest memberReq = new JsonArrayRequest(AppController.KOST_ROOM_LIST+uri+"?timestamp=" + String.valueOf(time),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() > 0){
                            // Parsing json
                            room_list.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    room_list.add(Pair.create(Integer.valueOf(obj.getString("id")), obj.getString("name")));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }


                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        room_adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // stopping swipe refresh
        RetryPolicy rp = new DefaultRetryPolicy(15000,3,0);
        memberReq.setRetryPolicy(rp);
        AppController.getInstance().addToRequestQueue(memberReq);
    }
}
