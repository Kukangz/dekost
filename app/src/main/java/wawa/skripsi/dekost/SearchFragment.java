package wawa.skripsi.dekost;

/**
 * Created by Admin on 04/01/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wawa.skripsi.dekost.library.KeyPairBoolData;
import wawa.skripsi.dekost.library.MultiSpinnerSearch;
import wawa.skripsi.dekost.search.SearchResult;
import wawa.skripsi.dekost.util.CustomSpinnerAdapter;
import wawa.skripsi.dekost.util.JSONParser;


public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final String country_url = "http://kukang.bahasbuku.com/v1/country";
    private static final String province_url = "http://kukang.bahasbuku.com/v1/province";
    private static final String city_url = "http://kukang.bahasbuku.com/v1/city";
    private static final String district_url = "http://kukang.bahasbuku.com/v1/district";
    private static final String region_url = "http://kukang.bahasbuku.com/v1/region";
    private static final String rules_url = "http://kukang.bahasbuku.com/v1/rules";
    private static final String facility_url = "http://kukang.bahasbuku.com/v1/facility";

    private TextInputLayout area_search_form;
    private LinearLayout android_search_panel;
    private TextView advanced_search_button;
    private ScrollView mScrollViewLayout;
    private CircleImageView search_nearby;
    private AppCompatSpinner country_spinner, province_spinner, city_spinner, district_spinner, region_spinner, resident_spinner, type_spinner;
    private CustomSpinnerAdapter country_adapter, province_adapter, city_adapter, district_adapter, region_adapter, resident_adapter, type_adapter;
    private LinearLayout country_container, province_container, city_container, district_container, region_container;
    private MultiSpinnerSearch rules_spinner, facility_spinner;
    private AppCompatButton advanced_search_result;
    private List<KeyPairBoolData> rules_adapter, facility_adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_layout, null);

        advanced_search_button = (TextView) view.findViewById(R.id.advanced_search_button);
        android_search_panel = (LinearLayout) view.findViewById(R.id.android_search_panel);
        mScrollViewLayout = (ScrollView) view.findViewById(R.id.base);
        search_nearby = (CircleImageView) view.findViewById(R.id.search_nearby);
        advanced_search_result = (AppCompatButton) view.findViewById(R.id.advanced_search_start);

        /*
        Input Text
         */

        final AppCompatEditText keyword = (AppCompatEditText) view.findViewById(R.id.keyword);
        final AppCompatEditText price_start = (AppCompatEditText) view.findViewById(R.id.price_start);
        final AppCompatEditText price_end = (AppCompatEditText) view.findViewById(R.id.price_end);

        /*
        Spinner
         */

        resident_spinner = (AppCompatSpinner) view.findViewById(R.id.resident);
        type_spinner = (AppCompatSpinner) view.findViewById(R.id.type);
        country_spinner = (AppCompatSpinner) view.findViewById(R.id.country);
        province_spinner = (AppCompatSpinner) view.findViewById(R.id.province);
        city_spinner = (AppCompatSpinner) view.findViewById(R.id.city);
        district_spinner = (AppCompatSpinner) view.findViewById(R.id.district);
        region_spinner = (AppCompatSpinner) view.findViewById(R.id.region);

        /*
        Button
         */

        final AppCompatCheckBox area_search_button = (AppCompatCheckBox) view.findViewById(R.id.search_area_button);
        final AppCompatCheckBox facility_search_button = (AppCompatCheckBox) view.findViewById(R.id.filter_facility);
        final AppCompatCheckBox rules_search_button = (AppCompatCheckBox) view.findViewById(R.id.filter_rules);


        facility_search_button.setChecked(false);
        rules_search_button.setChecked(false);
        /*
        Container
         */

        country_container = (LinearLayout) view.findViewById(R.id.country_container);
        province_container = (LinearLayout) view.findViewById(R.id.province_container);
        city_container = (LinearLayout) view.findViewById(R.id.city_container);
        district_container = (LinearLayout) view.findViewById(R.id.district_container);
        region_container = (LinearLayout) view.findViewById(R.id.region_container);

        resident_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());

        rules_adapter = AppController.getInstance().getRules();
        facility_adapter = AppController.getInstance().getFacility();
        /*
         * Manually Add Resident Type and Kost Type
         */

        String[] elements = view.getResources().getStringArray(R.array.resident_array);
        Integer counter = 0;
        for (String s : elements) {
            //Do your stuff here
            resident_adapter.add(Pair.create(counter, s.toString()));
            counter++;
        }

        resident_adapter.notifyDataSetChanged();
        resident_spinner.setAdapter(resident_adapter);

        type_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());


        String[] elements_type = view.getResources().getStringArray(R.array.type_array);
        Integer counter_type = 0;
        for (String s : elements_type) {
            //Do your stuff here
            type_adapter.add(Pair.create(counter_type, s.toString()));
            counter_type++;
        }

        type_adapter.notifyDataSetChanged();
        type_spinner.setAdapter(type_adapter);

        /*
        country
         */
        country_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());
        country_spinner.setAdapter(country_adapter);


        if(!AppController.getInstance().checkCountry()){
            country_adapter.add(Pair.create(0, "Select Country"));
            new CountryParse().execute(country_url, "country");
        }else{
            country_adapter.add(Pair.create(0, "Select Country"));
            JSONArray json = AppController.getInstance().getCountry();
            JSONObject jsonobject;

            for (int i = 0; i < json.length(); i++) {

                try {
                    jsonobject = json.getJSONObject(i);
                    Log.e(TAG, jsonobject.toString());
                    country_adapter.add(Pair.create(jsonobject.getInt("id"), jsonobject.getString("name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            country_adapter.notifyDataSetChanged();
        }

        /*
        Province
         */
        province_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());
        province_adapter.add(Pair.create(0, "Select Province"));
        province_spinner.setAdapter(province_adapter);

                /*
        City
         */
        city_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());
        city_adapter.add(Pair.create(0, "Select City"));
        city_spinner.setAdapter(city_adapter);

        /*
        District
         */
        district_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());
        district_adapter.add(Pair.create(0, "Select District"));
        district_spinner.setAdapter(district_adapter);

        /*
        Region
         */
        region_adapter = new CustomSpinnerAdapter(view.getContext(), new ArrayList<Pair<Integer, String>>());
        region_adapter.add(Pair.create(0, "Select Region"));
        region_spinner.setAdapter(region_adapter);


        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    clear_province();
                } else {
                    clear_province();
                    new CountryParse().execute(province_url, "province", String.valueOf(country_adapter.getItem(position).first));
                }

                noticeadapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        province_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    clear_city();
                } else {
                    clear_city();
                    new CountryParse().execute(city_url, "city", String.valueOf(province_adapter.getItem(position).first));
                }

                noticeadapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    clear_district();
                } else {
                    clear_district();
                    new CountryParse().execute(district_url, "district", String.valueOf(city_adapter.getItem(position).first));
                }

                noticeadapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        district_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    clear_region();
                } else {
                    clear_region();
                    new CountryParse().execute(district_url, "region", String.valueOf(district_adapter.getItem(position).first));
                }


                noticeadapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        search_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LocationManager locationmanager = (LocationManager) getActivity()
                        .getSystemService(Context.LOCATION_SERVICE);
                if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // YOUR MAPS ACTIVITY CALLING or WHAT YOU NEED
                    Intent i = new Intent(getActivity(), SearchResult.class);
                    i.putExtra("nearby", true);
                    startActivity(i);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final String message = "Please Activate Your Location Settings";

                    builder.setMessage(message)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            getActivity().startActivity(new Intent(action));
                                            d.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            d.cancel();
                                        }
                                    });
                    builder.create().show();
                }


            }
        });

        advanced_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android_search_panel.getVisibility() == View.VISIBLE) {
                    android_search_panel.setVisibility(View.GONE);
                } else {
                    android_search_panel.setVisibility(View.VISIBLE);
                }
            }
        });


        area_search_form = (TextInputLayout) view.findViewById(R.id.search_area);

        area_search_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    area_search_form.setVisibility(View.VISIBLE);
                } else {
                    area_search_form.setVisibility(View.GONE);
                }
            }
        });

        /**
         * Search MultiSelection Spinner (With Search/Filter Functionality)
         *
         *  Using MultiSpinnerSearch class
         */


        final MultiSpinnerSearch rules_spinner = (MultiSpinnerSearch) view.findViewById(R.id.rules_spinner);
        final MultiSpinnerSearch facility_spinner = (MultiSpinnerSearch) view.findViewById(R.id.facility_spinner);

        rules_spinner.setItems(rules_adapter, "Select Rules", -1, new MultiSpinnerSearch.MultiSpinnerSearchListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });

        rules_search_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rules_spinner.setVisibility(View.VISIBLE);
                } else {
                    rules_spinner.setVisibility(View.GONE);
                }
            }
        });

        facility_spinner.setItems(facility_adapter, "Select Facility", -1, new MultiSpinnerSearch.MultiSpinnerSearchListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });

        facility_search_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    facility_spinner.setVisibility(View.VISIBLE);
                } else {
                    facility_spinner.setVisibility(View.GONE);
                }
            }
        });

        advanced_search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationmanager = (LocationManager) getActivity()
                        .getSystemService(Context.LOCATION_SERVICE);
                if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    boolean validation = false;
                    // YOUR MAPS ACTIVITY CALLING or WHAT YOU NEED
                    Intent i = new Intent(getActivity(), SearchResult.class);
                    if (!keyword.getText().toString().matches("")) {
                        i.putExtra("search_keyword", keyword.getText().toString());
                    }
                    if (!price_start.getText().toString().matches("")) {
                        i.putExtra("price_start", price_start.getText().toString());
                    }

                    if (!price_end.getText().toString().matches("")) {
                        i.putExtra("price_end", price_end.getText().toString());
                    }
                    if (!resident_adapter.getItem(resident_spinner.getSelectedItemPosition()).first.toString().matches("0")) {
                        i.putExtra("resident", resident_adapter.getItem(resident_spinner.getSelectedItemPosition()).first.toString());
                    }
                    if (!type_adapter.getItem(type_spinner.getSelectedItemPosition()).first.toString().matches("0")) {
                        i.putExtra("type", type_adapter.getItem(type_spinner.getSelectedItemPosition()).first.toString());
                    }
                    if (facility_search_button.isChecked()) {
                        if (!facility_spinner.getCurrentIdList().equalsIgnoreCase("Select Facility") || !facility_spinner.getCurrentIdList().matches("")) {
                            i.putExtra("facility", facility_spinner.getCurrentIdList());
                        }
                    }

                    if (rules_search_button.isChecked()) {
                        if (!rules_spinner.getCurrentIdList().equalsIgnoreCase("Select Rules") || !rules_spinner.getCurrentIdList().matches("")) {
                            i.putExtra("rules", rules_spinner.getCurrentIdList());
                        }
                    }


                    if (area_search_button.isChecked()) {

                        if (region_spinner.getSelectedItemPosition() != 0) {
                            i.putExtra("region", region_adapter.getItem(region_spinner.getSelectedItemPosition()).first.toString());
                            validation = true;
                        } else {

                            if (district_spinner.getSelectedItemPosition() != 0) {
                                i.putExtra("district", district_adapter.getItem(district_spinner.getSelectedItemPosition()).first.toString());
                            } else {
                                if (city_spinner.getSelectedItemPosition() != 0) {
                                    i.putExtra("city", city_adapter.getItem(city_spinner.getSelectedItemPosition()).first.toString());
                                } else {
                                    if (province_spinner.getSelectedItemPosition() != 0) {
                                        i.putExtra("province", province_adapter.getItem(province_spinner.getSelectedItemPosition()).first.toString());
                                    } else {
                                        if (country_spinner.getSelectedItemPosition() != 0) {
                                            i.putExtra("country", country_adapter.getItem(country_spinner.getSelectedItemPosition()).first.toString());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        validation = true;
                    }

                    if (validation) {
                        startActivity(i);
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                        final String message = "Please Choose a Region";

                        builder.setMessage(message)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                d.dismiss();
                                            }
                                        });
                        builder.create().show();
                    }


                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final String message = "Please Activate Your Location Settings";

                    builder.setMessage(message)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            getActivity().startActivity(new Intent(action));
                                            d.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            d.cancel();
                                        }
                                    });
                    builder.create().show();
                }


            }
        });


        return view;
    }

    private void noticeadapter() {
        city_adapter.notifyDataSetChanged();
        region_adapter.notifyDataSetChanged();
        province_adapter.notifyDataSetChanged();
        district_adapter.notifyDataSetChanged();
    }

    private void clear_country() {
        country_adapter.clear();
        country_adapter.add(Pair.create(0, "Select Country"));
        clear_province();
        clear_city();
        clear_district();
        clear_region();
    }

    private void clear_province() {
        province_adapter.clear();
        province_adapter.add(Pair.create(0, "Select Province"));
        clear_city();
        clear_district();
        clear_region();
    }

    private void clear_city() {
        city_adapter.clear();
        city_adapter.add(Pair.create(0, "Select City"));
        clear_district();
        clear_region();
    }

    private void clear_district() {
        district_adapter.clear();
        district_adapter.add(Pair.create(0, "Select District"));
        clear_region();
    }

    private void clear_region() {
        region_adapter.clear();
        region_adapter.add(Pair.create(0, "Select Region"));
        noticeadapter();
    }


    private class CountryParse extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONArray doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            String uri = "";
            if (args.length > 2 && args[2] != null) {
                uri = "?parent=" + args[2];
            }

            // Getting JSON from URL
            JSONArray json = jParser.getJSONFromUrl(args[0] + uri);
            Log.e(TAG, args[0] + uri);
            switch (args[1]) {
                case "country":
                    try {

                        // Getting JSON Array
                        JSONObject jsonobject;

                        for (int i = 0; i < json.length(); i++) {
                            HashMap<String, String> temp = new HashMap<String, String>();
                            jsonobject = json.getJSONObject(i);
                            Log.e(TAG, jsonobject.toString());

                            country_adapter.add(Pair.create(jsonobject.getInt("id"), jsonobject.getString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    country_adapter.notifyDataSetChanged();

                    break;
                case "province":
                    try {

                        // Getting JSON Array
                        JSONObject jsonobject;

                        for (int i = 0; i < json.length(); i++) {
                            HashMap<String, String> temp = new HashMap<String, String>();
                            jsonobject = json.getJSONObject(i);
                            Log.e(TAG, jsonobject.toString());

                            province_adapter.add(Pair.create(jsonobject.getInt("id"), jsonobject.getString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case "city":
                    try {

                        // Getting JSON Array
                        JSONObject jsonobject;

                        for (int i = 0; i < json.length(); i++) {
                            HashMap<String, String> temp = new HashMap<String, String>();
                            jsonobject = json.getJSONObject(i);
                            Log.e(TAG, jsonobject.toString());
                            city_adapter.add(Pair.create(jsonobject.getInt("id"), jsonobject.getString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case "district":
                    try {

                        // Getting JSON Array
                        JSONObject jsonobject;

                        for (int i = 0; i < json.length(); i++) {
                            HashMap<String, String> temp = new HashMap<String, String>();
                            jsonobject = json.getJSONObject(i);
                            Log.e(TAG, jsonobject.toString());

                            district_adapter.add(Pair.create(jsonobject.getInt("id"), jsonobject.getString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case "region":
                    try {

                        // Getting JSON Array
                        JSONObject jsonobject;

                        for (int i = 0; i < json.length(); i++) {
                            HashMap<String, String> temp = new HashMap<String, String>();
                            jsonobject = json.getJSONObject(i);
                            Log.e(TAG, jsonobject.toString());

                            region_adapter.add(Pair.create(jsonobject.getInt("id"), jsonobject.getString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {
            super.onPostExecute(json);

            pDialog.dismiss();


        }
    }


}
