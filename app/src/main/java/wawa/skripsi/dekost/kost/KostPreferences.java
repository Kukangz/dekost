package wawa.skripsi.dekost.kost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;
import wawa.skripsi.dekost.util.LocationPicker;

public class KostPreferences extends AppCompatActivity {
    private static final String TAG = KostPreferences.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_preference);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        Bundle extras = getIntent().getExtras();



        MyPreferenceFragment mFragment_A = new MyPreferenceFragment();
        mFragment_A.setArguments(extras);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, mFragment_A).commit();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private ProgressDialog mProgressDialog;
        private String id;

        Bundle bundle;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.kost_preferences);


            bundle = this.getArguments();
            MultiSelectListPreference facility = (MultiSelectListPreference) findPreference("facility");
            facility.setEntries(AppController.getInstance().getFacilityPreference().second);
            facility.setEntryValues(AppController.getInstance().getFacilityPreference().first);

            MultiSelectListPreference rules = (MultiSelectListPreference) findPreference("rules");
            rules.setEntries(AppController.getInstance().getRulesPreference().second);
            rules.setEntryValues(AppController.getInstance().getRulesPreference().first);

            CheckBoxPreference look_member, searchable;
            EditTextPreference name, address, address_other, price;
            Preference location, display_image;
            ListPreference type;

            look_member = (CheckBoxPreference) findPreference("status_looking");
            searchable = (CheckBoxPreference) findPreference("status_searchable");
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(bundle.getString("result"));
                id = json.getString("id");

                name = (EditTextPreference) findPreference("name");
                name.setSummary(json.getString("name"));
                name.setText(json.getString("name"));


                address = (EditTextPreference) findPreference("address");
                address.setSummary(json.getString("address"));
                address.setText(json.getString("address"));


                address_other = (EditTextPreference) findPreference("address_other");
                address_other.setSummary(json.getString("address_other"));
                address_other.setText(json.getString("address_other"));

                price = (EditTextPreference) findPreference("price");
                price.setSummary(json.getString("price"));
                price.setText(json.getString("price"));


                location = findPreference("location");
                location.setSummary(json.getString("latitude") + "," + json.getString("longitude"));

                final String lat = json.getString("latitude");
                final String lng = json.getString("longitude");
                location.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {


                        Intent intent = new Intent(getActivity(), LocationPicker.class);
                        intent.replaceExtras(bundle);
                        intent.putExtra("latitude", lat);
                        intent.putExtra("longitude", lng);
                        MyPreferenceFragment.this.startActivityForResult(intent, 2);
                        return true;
                    }
                });

                /*display_image = findPreference("display_image");
                display_image.setSummary(json.getString("image"));
                display_image.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        intent.setType("image*//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        MyPreferenceFragment.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                        return true;
                    }
                });*/

                type = (ListPreference) findPreference("resident_type");
                type.setSummary(getResources().getStringArray(R.array.resident_array)[json.getInt("resident_type")]);
                type.setValueIndex(json.getInt("resident_type"));


                look_member.setChecked(false);
                if (json.getString("status_looking").equals("1")) {
                    look_member.setChecked(true);
                }
                searchable.setChecked(false);
                if (json.getString("status_searchable").equals("1")) {
                    searchable.setChecked(true);
                }

            } catch (JSONException e) {
                Log.e("Json Error", bundle.getString("result").toString());
                e.printStackTrace();
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.e("Log Preference", "Settings key changed: " + key);

            if (key.equalsIgnoreCase("status_looking") || key.equalsIgnoreCase("status_searchable")) {
                updateClickerPreference(key, sharedPreferences.getBoolean(key, false), id);
                Preference pref = findPreference(key);
                SharedPreferences.Editor edit = pref.getEditor();
                edit.putBoolean(key, sharedPreferences.getBoolean(key, false));
                edit.commit();
            } else if (key.equalsIgnoreCase("facility") || key.equalsIgnoreCase("rules")) {
                Set<String> selections = sharedPreferences.getStringSet(key, null);
                String uri = "";
                for (String s : selections) {
                    uri += s + ",";
                }

                uri = uri.substring(0, uri.length() - 1);
                updatePreference(key, uri, id);
            } else {
                updatePreference(key, sharedPreferences.getString(key, ""), id);
                Preference pref = findPreference(key);
                pref.setSummary(sharedPreferences.getString(key, ""));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("Request code", String.valueOf(requestCode));
            Log.e("Result code", String.valueOf(resultCode));

            if (requestCode == 1) {
                if (resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};



                }
            }
            if (requestCode == 2) {

                if (resultCode == RESULT_OK && data != null) {
                    Log.e("last check", "aa");
                    Preference location;
                    location = findPreference("location");
                    location.setSummary(data.getStringExtra("latitude") +","+ data.getStringExtra("longitude"));
                    updateLocation(data.getStringExtra("latitude"), data.getStringExtra("longitude"), id);
                }
            }


        }

        private void decodeFile(String filePath) {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        }

        private void updatePreference(String key, String value, String id) {

            AsyncHttpClient client = new AsyncHttpClient();
            String uri = id + "?" + key + "=" + value;
            uri += "";
            Log.e("url update", AppController.KOST_UPDATE + uri);
            client.get(AppController.KOST_UPDATE + uri, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setTitle("Updating Preference..");
                    mProgressDialog.setMessage("Please Wait..");
                    mProgressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // called when response HTTP status is "200 OK"
                    Log.i(TAG, "Update Success");
                    mProgressDialog.hide();
                    Toast.makeText(getActivity(), "Kost Preference Updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i(TAG, "Failed to save");
                    mProgressDialog.hide();
                }

                @Override
                public void onRetry(int retryNo) {
                    Log.i(TAG, "Retrying");
                    // called when request is retried
                }
            });
        }

        private void updateClickerPreference(String key, Boolean value, String id) {

            AsyncHttpClient client = new AsyncHttpClient();
            String nilai = "0";
            if (value == true) {
                nilai = "1";
            }

            String uri = id + "?" + key + "=" + nilai;
            uri += "";
            Log.e("url update", AppController.KOST_UPDATE + uri);
            client.get(AppController.KOST_UPDATE + uri, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setTitle("Updating Preference..");
                    mProgressDialog.setMessage("Please Wait..");
                    mProgressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // called when response HTTP status is "200 OK"
                    Log.i(TAG, "Update Success");
                    mProgressDialog.hide();
                    Toast.makeText(getActivity(), "Kost Preference Updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i(TAG, "Failed to save");
                    mProgressDialog.hide();
                }

                @Override
                public void onRetry(int retryNo) {
                    Log.i(TAG, "Retrying");
                    // called when request is retried
                }
            });
        }

        private void updateLocation(String latitude, String longitude, String id) {

            AsyncHttpClient client = new AsyncHttpClient();
            String uri = id + "?latitude=" + latitude + "&longitude=" + longitude;
            uri += "";

            Log.e("url update", AppController.KOST_UPDATE + uri);
            client.get(AppController.KOST_UPDATE + uri, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setTitle("Updating Preference..");
                    mProgressDialog.setMessage("Please Wait..");
                    mProgressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // called when response HTTP status is "200 OK"
                    Log.i(TAG, "Update Success");
                    mProgressDialog.hide();
                    Toast.makeText(getActivity(), "Kost Preference Updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i(TAG, "Failed to save");
                    mProgressDialog.hide();
                }

                @Override
                public void onRetry(int retryNo) {
                    Log.i(TAG, "Retrying");
                    // called when request is retried
                }
            });
        }

        private void uploadImage(String image, String id) {

            AsyncHttpClient client = new AsyncHttpClient();
            String uri = id + "?image=" + image;
            uri += "";

            InputStream fis;

//            fis = new FileInputStream("");

     /*       RequestParams params = new RequestParams();
            params.put("secret_passwords", myInputStream, "passwords.txt");

            File myFile = new File("/path/to/file.png");
            try {
                params.put("profile_picture", myFile);
            } catch(FileNotFoundException e) {

            }

            byte[] myByteArray = blah;
            params.put("soundtrack", new ByteArrayInputStream(myByteArray), "she-wolf.mp3");


            client.post(AppController.KOST_UPDATE + uri, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setTitle("Updating Preference..");
                    mProgressDialog.setMessage("Please Wait..");
                    mProgressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // called when response HTTP status is "200 OK"
                    Log.i(TAG, "Update Success");
                    mProgressDialog.hide();
                    Toast.makeText(getActivity(), "Kost Preference Updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i(TAG, "Failed to save");
                    mProgressDialog.hide();
                }

                @Override
                public void onRetry(int retryNo) {
                    Log.i(TAG, "Retrying");
                    // called when request is retried
                }
            });
        }*/


        }
    }

}