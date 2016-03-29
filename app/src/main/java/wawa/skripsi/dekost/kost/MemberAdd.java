package wawa.skripsi.dekost.kost;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;


/**
 * Created by Admin on 05/01/2016.
 */
public class MemberAdd extends AppCompatActivity {

    private static final String TAG = MemberAdd.class.getSimpleName();

    private EditText android_id;

    private ProgressDialog mProgressDialog;
    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_add_layout);

        extras = getIntent().getExtras();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        android_id = (EditText) findViewById(R.id.android_id);

        mProgressDialog = new ProgressDialog(MemberAdd.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppCompatButton button = (AppCompatButton) findViewById(R.id.member_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                String uri = "?id="+android_id.getText()+"&kost="+extras.getString("kost_id");
                Log.e("add member", uri);
                client.get(AppController.KOST_MEMBER_ADD+uri, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                        mProgressDialog.setMessage("Please Wait..");
                        mProgressDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // called when response HTTP status is "200 OK"
                        Log.i(TAG, "Member Successfully Added");
                        mProgressDialog.hide();
                        Toast.makeText(MemberAdd.this, "Member Successfully Added", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.i(TAG, "Member Add Fail");
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

    }
}
