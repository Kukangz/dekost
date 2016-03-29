package wawa.skripsi.dekost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Admin on 05/01/2016.
 */
public class Signup extends AppCompatActivity {

    private static final String TAG = "Signup";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_register_layout);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setTitle("Creating Account...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        String uri = "?name="+name+"&email=" + email + "&password=" + password;
        Log.e("uri", AppController.LOGIN_URL + uri);
        JsonArrayRequest jsonarray = new JsonArrayRequest(AppController.REGISTER_URL + uri, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = new JSONObject(response.getJSONObject(0).toString());
                    String status = object.getString("status");
                    switch (status) {
                        case "200":
                            Toast.makeText(Signup.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Signup.this, Login.class);
                            startActivity(i);
                            finish();
                            break;
                        case "201":
                            Toast.makeText(Signup.this, "This user already registered.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON ERROR", e.toString());
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error volley", error.toString());
                Toast.makeText(Signup.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });

        RetryPolicy rp = new DefaultRetryPolicy(15000,3,0);
        jsonarray.setRetryPolicy(rp);

        AppController.getInstance().addToRequestQueue(jsonarray);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration Failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 6) {
            _nameText.setError("at least 6 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            _passwordText.setError("between 4 and 12 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
