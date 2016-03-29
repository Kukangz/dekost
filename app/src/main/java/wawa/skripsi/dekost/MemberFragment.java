package wawa.skripsi.dekost;

/**
 * Created by Admin on 04/01/2016.
 */

import android.app.Dialog;
import android.app.DialogFragment;
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
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import wawa.skripsi.dekost.member.MemberRegister;
import wawa.skripsi.dekost.model.Kost;
import wawa.skripsi.dekost.util.CustomListSearchAdapter;

public class MemberFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = MemberFragment.class.getSimpleName();

    private static final String url = AppController.MEMBER_LIST_URL;
    private ProgressDialog pDialog;
    private List<Kost> memberList = new ArrayList<Kost>();
    private ListView listView;
    private CustomListSearchAdapter adapter;
    private CircularProgressView progress;
    private FloatingActionButton refresh;
    private TextView emptyresult;
    private FloatingActionButton fabclick;
    private SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;
    private ActionBarDialog abd;
    private final String[] option = new String[]{"View Member", "Call Member", "Send Message", "Report Member"};
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.member_layout, null);

        fabclick = (FloatingActionButton) view.findViewById(R.id.fab);
        mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);
        mSheetLayout.setFab(fabclick);
        mSheetLayout.setFabAnimationEndListener(this);
        progress = (CircularProgressView) view.findViewById(R.id.progress_view);

        fabclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSheetLayout.expandFab();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        emptyresult = (TextView) view.findViewById(R.id.empty_result);
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListSearchAdapter(this.getActivity(), memberList);
        listView.setAdapter(adapter);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        adapter.clear();



        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.select_dialog_item, option);
                final View v = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                        final EditText input = new EditText(getContext());
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                // View member
                                TextView member_id = (TextView) v.findViewById(R.id.releaseYear);
                                Intent intent = new Intent(getActivity(), ViewResult.class);
                                intent.putExtra("title", "Member Detail");
                                intent.putExtra("member_id", member_id.getText().toString());
                                startActivityForResult(intent, REQUEST_CODE);
                                break;
                            case 1:
                                // Call member
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Doing phone call, are you sure?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                callIntent.setData(Uri.parse("tel:" + v.findViewById(R.id.rating)));
                                                startActivity(callIntent);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();

                                break;
                            case 2:
                                // Send Message

                                builder.setTitle("Send Message");
                                // Set up the input
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
                                        client.get(AppController.MEMBER_MESSAGE_URL, new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                // called before request is started
                                                mProgressDialog.setTitle("Sending Message..");
                                                mProgressDialog.setMessage("Please Wait..");
                                                mProgressDialog.show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // called when response HTTP status is "200 OK"
                                                Log.i(TAG, "Send Message Success");
                                                mProgressDialog.hide();
                                                Toast.makeText(getContext(), "Send Message Success", Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                Log.i(TAG, "Send Message Failure");
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
                            case 3:
                                // Report Member

                                builder.setTitle("Report Member");
                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                                input.setMinLines(4);
                                builder.setView(input);
                                // Set up the buttons
                                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Send Report
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.get(AppController.MEMBER_REPORT_URL, new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                // called before request is started
                                                mProgressDialog.setTitle("Sending Message..");
                                                mProgressDialog.setMessage("Please Wait..");
                                                mProgressDialog.show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // called when response HTTP status is "200 OK"
                                                Log.i(TAG, "Send Message Success");
                                                mProgressDialog.hide();
                                                Toast.makeText(getContext(), "Send Message Success", Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                Log.i(TAG, "Send Message Failure");
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
                            default:
                                break;
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getList();


        return view;
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

    private void getList() {
        // Creating volley request obj
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.notifyDataSetChanged();
        if (!AppController.getInstance().getKOSTID().matches("0")) {
            // Creating volley request obj
            Log.e("current url", url+AppController.getInstance().getKOSTID());
            JsonArrayRequest memberReq = new JsonArrayRequest(url+AppController.getInstance().getKOSTID(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();
                            if(response.length() > 0) {
                                // Parsing json
                                // Parsing json
                                for (int i = 0; i < response.length(); i++) {
                                    try {

                                        JSONObject obj = response.getJSONObject(i);
                                        Kost member = new Kost();
                                        member.setTitle(obj.getString("name"));
                                        member.setThumbnailUrl(obj.getString("image"));
                                        member.setId(obj.getString("id"));
                                        member.setLast_update(obj.getString("last_login"));
                                        member.setDescription(obj.getString("address"));

                                        // adding movie to movies array
                                        memberList.add(member);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                progress.setVisibility(View.GONE);
                            }else{
                                progress.setVisibility(View.GONE);
                                emptyresult.setText("No Member Found");
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
                    hidePDialog();
                    refresh.setVisibility(View.VISIBLE);
                }
            });

            // stopping swipe refresh
            RetryPolicy rp = new DefaultRetryPolicy(15000,3,0);
            memberReq.setRetryPolicy(rp);
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(memberReq);
        }else{
            emptyresult.setText("Not registered as a Member yet");
            emptyresult.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            fabclick.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onFabAnimationEnd() {

        Intent intent = new Intent(getActivity(), MemberRegister.class);
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
    public void onRefresh() {
        getList();
    }

    public static class ActionBarDialog extends DialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            String title = args.getString("title");
            View v = inflater.inflate(R.layout.dialog, container, false);
            TextView tv = (TextView) v.findViewById(R.id.text);
            tv.setText("This is an instance of ActionBarDialog");
            Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // Handle the menu item
                    return true;
                }
            });
            toolbar.setTitle(title);
            return v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            // request a window without the title
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }
    }
}


