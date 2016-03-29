package wawa.skripsi.dekost;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wawa.skripsi.dekost.util.AuthLibrary;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity{

    public static final String KEY_USERNAME="email";
    public static final String KEY_PASSWORD="password";

    private EditText editTextUsername;
    private EditText editTextPassword;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editTextUsername = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);


        Button mRegisterButton = (Button) findViewById(R.id.register_button);

        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
                /*Intent intent = new Intent(Login.this, MainMenu.class);
                startActivity(intent);*/
            }
        });


    }

    private void userLogin() {
        if(validate()) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Please Wait..");
            pd.setTitle("Sign in..");
            pd.show();
            email = editTextUsername.getText().toString().trim();
            password = editTextPassword.getText().toString().trim();
            String uri = "?email=" + email + "&password=" + password;
            Log.e("uri", AppController.LOGIN_URL + uri);
            JsonArrayRequest jsonarray = new JsonArrayRequest(AppController.LOGIN_URL + uri, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject object = new JSONObject(response.getJSONObject(0).toString());
                        Log.e("wawa", object.toString());
                        String status = object.getString("status");
                        switch (status) {
                            case "200":
                                String data = object.getString("data");
                                Log.e("data", data);
                                JSONObject userdata = new JSONObject(data);
                                AuthLibrary auth = new AuthLibrary(Login.this);
                                auth.createUserLoginSession(userdata.toString());
                                if (auth.checkLogin()) {
                                    openProfile();
                                } else {
                                    Toast.makeText(Login.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case "201":
                                Toast.makeText(Login.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        pd.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON ERROR", e.toString());
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error volley", error.toString());
                    Toast.makeText(Login.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            });

            RetryPolicy rp = new DefaultRetryPolicy(15000,3,0);
            jsonarray.setRetryPolicy(rp);

            AppController.getInstance().addToRequestQueue(jsonarray);
        }
    }

    private void openProfile(){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean validate() {
        boolean valid = true;


        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() ||  !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextUsername.setError("at least 6 characters");
            valid = false;
        } else {
            editTextUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            editTextPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }

}

