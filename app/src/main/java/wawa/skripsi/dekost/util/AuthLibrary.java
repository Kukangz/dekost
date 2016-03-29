package wawa.skripsi.dekost.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import wawa.skripsi.dekost.Login;

/**
 * Created by Admin on 28/01/2016.
 */
public class AuthLibrary {

    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "UserSession";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";


    // User name (make variable public to access from outside)
    public static final String KEY_ID = "id";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String IMEI = "imei";
    public static final String KOST_ID = "kostid";
    public static final String ALLRESULT = "allresult";

    // Constructor
    public AuthLibrary(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String userdata) {

        try {
            Log.e("json create user", userdata);
            JSONObject json = new JSONObject(userdata);
            // Storing login value as TRUE
            editor.putBoolean(IS_USER_LOGIN, true);

            // Storing name in pref
            editor.putString(KEY_ID, json.getString("id"));
            // Storing name in pref
            editor.putString(KEY_NAME, json.getString("name"));

            // Storing email in pref
            editor.putString(KEY_EMAIL, json.getString("email"));

            editor.putString(IMEI, json.getString("device_id"));

            editor.putString(KOST_ID, json.getString("kost"));

            editor.putString(ALLRESULT, userdata);

            // commit changes
            editor.commit();
        } catch (JSONException e) {
            Log.e("exceptionjson", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return false;
        }
        return true;
    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        user.put(IMEI, pref.getString(IMEI, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KOST_ID, pref.getString(KOST_ID, null));

        user.put(ALLRESULT, pref.getString(ALLRESULT, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, Login.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
