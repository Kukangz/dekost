package wawa.skripsi.dekost.member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
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
public class MemberRegister extends Activity {

    private static final String TAG = MemberRegister.class.getSimpleName();

    private EditText android_id;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_suggest_layout);

        android_id = (EditText) findViewById(R.id.android_id);

        mProgressDialog = new ProgressDialog(MemberRegister.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        AppCompatButton button = (AppCompatButton) findViewById(R.id.member_suggest);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(AppController.SUGGESTION_URL, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                        mProgressDialog.setTitle("Sending Suggestion..");
                        mProgressDialog.setMessage("Please Wait..");
                        mProgressDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // called when response HTTP status is "200 OK"
                        Log.i(TAG, "Member Suggestion Success");
                        mProgressDialog.hide();
                        Toast.makeText(MemberRegister.this, "Member Suggestion Successful", Toast.LENGTH_LONG).show();
                        finish();
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
