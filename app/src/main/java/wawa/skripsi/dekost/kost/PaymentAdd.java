package wawa.skripsi.dekost.kost;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import fr.ganfra.materialspinner.MaterialSpinner;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.util.CustomSpinnerAdapter;

/**
 * Created by Admin on 10/01/2016.
 */
public class PaymentAdd extends AppCompatActivity {

    private static final String TAG = PaymentAdd.class.getSimpleName();
    private MaterialSpinner spinner_type, spinner_member;

    private EditText android_id, payment_date, name, amount;
    private Bundle extras;

    private ProgressDialog mProgressDialog;
    private CustomSpinnerAdapter adapter_member;


    private List<Pair<Integer,String>> ITEMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_add_layout);

        extras = getIntent().getExtras();

        name = (EditText) findViewById(R.id.name);
        amount = (EditText) findViewById(R.id.amount);

        android_id = (EditText) findViewById(R.id.android_id);
        String[]  TYPE = {"Payment","Expenses"};
        ITEMS = new ArrayList<>();
        if(extras.getBoolean("no_member")) {
            /*mProgressDialog = new ProgressDialog(PaymentAdd.this);
            mProgressDialog.setMessage("Loading Member");
            mProgressDialog.show();*/

            JsonArrayRequest result = getList();
            AppController.getInstance().addToRequestQueue(result);
            ITEMS.add(Pair.create(1, "dummmy"));

        }else{
            ITEMS.add(Pair.create(Integer.valueOf(extras.getString("userid")), extras.getString("username")));
        }




        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TYPE);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // toolbar bos

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        // get all member here

        adapter_member = new CustomSpinnerAdapter(this, ITEMS);

        /*
        register and set adapter
         */
        spinner_type = (MaterialSpinner) findViewById(R.id.spinner);
        spinner_member = (MaterialSpinner) findViewById(R.id.spinner_member);
        spinner_type.setAdapter(adapter);
        spinner_member.setAdapter(adapter_member);

        if(!extras.getBoolean("no_member")) {
            spinner_member.setSelection(1);
            spinner_member.setClickable(false);
        }

        payment_date = (EditText) findViewById(R.id.payment_date);

        payment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PaymentAdd.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        mProgressDialog = new ProgressDialog(PaymentAdd.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppCompatButton button = (AppCompatButton) findViewById(R.id.payment_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                String member;
                if(extras.getBoolean("no_member")) {
                    member = adapter_member.getItem(spinner_member.getSelectedItemPosition()).second.toString();
                }else{
                    member = adapter_member.getItem(spinner_member.getSelectedItemPosition()-1).second.toString();
                }
                String uri = "?description="+name.getText()+"&type="+String.valueOf(spinner_type.getSelectedItemPosition())+"&member_name="+member+"&amount="+amount.getText()+"&payment_date="+payment_date.getText().toString().replace("/","-");
                Log.e("uri add", AppController.KOST_PAYMENT_ADD+extras.getString("kost_id")+uri);
                client.get(AppController.KOST_PAYMENT_ADD+extras.getString("kost_id")+"/"+uri, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                        mProgressDialog.setMessage("Saving Payment...");
                        mProgressDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // called when response HTTP status is "200 OK"
                        Log.i(TAG, "Payment Success");
                        mProgressDialog.hide();
                        Toast.makeText(PaymentAdd.this, "Payment Add Successful.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.i(TAG, "Payment Failure");
                        mProgressDialog.hide();
                        Toast.makeText(PaymentAdd.this, "Payment add failed, please retry.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        Log.i(TAG, "Retrying");
                        // called when request is retried
                    }
                });
            }
        });
    }
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        payment_date.setText(sdf.format(myCalendar.getTime()));
    }

    private JsonArrayRequest getList(){
        final String uri = extras.getString("kost_id");
        int time = (int) (System.currentTimeMillis());//gets the current time in milliseconds
        Log.e("get list payment",AppController.KOST_MEMBER_LIST+uri+"?timestamp=" + String.valueOf(time));
        JsonArrayRequest memberReq = new JsonArrayRequest(AppController.KOST_MEMBER_LIST+uri+"?timestamp=" + String.valueOf(time),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if(response.length() > 0){
                            // Parsing json
                            ITEMS.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);

                                    // adding movie to movies array
                                    ITEMS.add(Pair.create(Integer.valueOf(obj.getString("id")),obj.getString("name")));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }


                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter_member.notifyDataSetChanged();
                        mProgressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        RetryPolicy rp = new DefaultRetryPolicy(15000,3,0);
        memberReq.setRetryPolicy(rp);

        return memberReq;

    }

}
