package wawa.skripsi.dekost;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wawa.skripsi.dekost.library.KeyPairBoolData;
import wawa.skripsi.dekost.util.AuthLibrary;
import wawa.skripsi.dekost.util.JSONParser;

/**
 * Created by Admin on 24/01/2016.
 */
public class SplashScreen extends Activity {

    private static final String TAG = SplashScreen.class.getSimpleName();
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new GetParameter().execute();
    }

    private class GetParameter extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONArray json_country;

            json_country = jParser.getJSONFromUrl(AppController.COUNTRY_URL);

            AppController.getInstance().setCountry(json_country);


            JSONArray json = jParser.getJSONFromUrl(AppController.FACILITY_URL);
            List<KeyPairBoolData> rules_adapter = new ArrayList<KeyPairBoolData>(), facility_adapter = new ArrayList<KeyPairBoolData>();

            try {

                // Getting JSON Array
                JSONObject jsonobject;
                List<String> entries = new ArrayList<String>();
                List<String> entriesvalue = new ArrayList<String>();

                for (int i = 0; i < json.length(); i++) {
                    jsonobject = json.getJSONObject(i);
                    Log.e(TAG, jsonobject.toString());

                    entries.add(jsonobject.getString("id"));
                    entriesvalue.add(jsonobject.getString("name"));

                    KeyPairBoolData h = new KeyPairBoolData();
                    h.setId(jsonobject.getInt("id"));
                    h.setName(jsonobject.getString("name"));
                    h.setSelected(false);
                    facility_adapter.add(h);
                    Log.e("add facility",jsonobject.getString("name") );
                }
                Pair<CharSequence[], CharSequence[]> j = Pair.create(entries.toArray(new CharSequence[entries.size()]), entriesvalue.toArray(new CharSequence[entriesvalue.size()]));
                AppController.getInstance().setFacility(facility_adapter, j);
            } catch (JSONException e) {
                Log.e("facility ex", e.toString());
            }

            json = jParser.getJSONFromUrl(AppController.RULES_URL);
            try {

                // Getting JSON Array
                JSONObject jsonobject;
                List<String> entries = new ArrayList<String>();
                List<String> entriesvalue = new ArrayList<String>();

                for (int i = 0; i < json.length(); i++) {
                    jsonobject = json.getJSONObject(i);
                    Log.e(TAG, jsonobject.toString());

                    entries.add(jsonobject.getString("id"));
                    entriesvalue.add(jsonobject.getString("name"));

                    KeyPairBoolData h = new KeyPairBoolData();
                    h.setId(jsonobject.getInt("id"));
                    h.setName(jsonobject.getString("name"));
                    h.setSelected(false);
                    rules_adapter.add(h);
                }

                Pair<CharSequence[], CharSequence[]> j = Pair.create(entries.toArray(new CharSequence[entries.size()]), entriesvalue.toArray(new CharSequence[entriesvalue.size()]));
                AppController.getInstance().setRules(rules_adapter, j);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {
            super.onPostExecute(json);
            AuthLibrary auth = new AuthLibrary(SplashScreen.this);
            if (!auth.checkLogin()) {
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashScreen.this, MainMenu.class);
                startActivity(intent);
            }
        }
    }
}
