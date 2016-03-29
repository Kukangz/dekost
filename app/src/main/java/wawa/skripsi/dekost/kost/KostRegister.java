package wawa.skripsi.dekost.kost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import fr.ganfra.materialspinner.MaterialSpinner;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;


/**
 * Created by Admin on 05/01/2016.
 */
public class KostRegister extends Activity {

    private static final String TAG = KostRegister.class.getSimpleName();
    private static final String url = "http://kukang.bahasbuku.com/v1/kost/register/";
    private MaterialSpinner spinner;

    private EditText name, address, other, room;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kost_register_layout);

        name = (EditText) findViewById(R.id.kost_name);
        address = (EditText) findViewById(R.id.kost_address);
        other = (EditText) findViewById(R.id.kost_address_other);
        room = (EditText) findViewById(R.id.kost_room);

        String[] ITEMS = {"Man", "Women", "Both", "Married Couple"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        mProgressDialog = new ProgressDialog(KostRegister.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppCompatButton button = (AppCompatButton) findViewById(R.id.kost_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                String uri = "";
                uri += room.getText().toString()+"?name="+name.getText().toString()+"&address="+address.getText().toString()+"&address_other="+other.getText().toString()+"&resident_type="+spinner.getSelectedItemId()+"&member="+ AppController.getInstance().getId();
                Log.e("url test",url+uri);


                client.get(url+uri, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                        mProgressDialog.setTitle("Register Kost..");
                        mProgressDialog.setMessage("Please Wait..");
                        mProgressDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // called when response HTTP status is "200 OK"
                        Log.i(TAG, "Register Success");
                        mProgressDialog.hide();
                        Toast.makeText(KostRegister.this, "Registration Successful", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.i(TAG, "Register Failure");
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
        //Activate
        //spinner.setError("Error");
        //Desactivate
        //spinner.setError(null);
    }


    private void register(){
        name.setEnabled(false);
        address.setEnabled(false);
        other.setEnabled(false);
        room.setEnabled(false);
        spinner.setEnabled(false);
    }




}
