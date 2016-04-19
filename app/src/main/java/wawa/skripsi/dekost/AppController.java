package wawa.skripsi.dekost;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.orm.SugarApp;

import org.json.JSONArray;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import wawa.skripsi.dekost.library.KeyPairBoolData;
import wawa.skripsi.dekost.util.AuthLibrary;
import wawa.skripsi.dekost.util.LruBitmapCache;

public class AppController extends SugarApp {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private AuthLibrary auth;
    private String UserID = "1";
    private String KOSTID = "0";
    private List<KeyPairBoolData> facility, rules;
    private Pair<CharSequence[],CharSequence[]> facility_preference, rules_preference;
    private HashMap<String, String> userDetail;
    private JSONArray country = new JSONArray();



    /*
     *   Url List
     */


    public static String API_URL = "http://kukang.bahasbuku.com/v1/";

    public static String IMAGE_URL = "kukang.bahasbuku.com/image/";

    public static String VERSION_URL = "http://kukang.bahasbuku.com/v1/version";
    public static String RULES_URL = "http://kukang.bahasbuku.com/v1/rules";
    public static String COUNTRY_URL = "http://kukang.bahasbuku.com/v1/country";
    public static String FACILITY_URL = "http://kukang.bahasbuku.com/v1/facility";

    public static String KOST_URL = API_URL+"kost/";
    public static String KOST_LIST = KOST_URL+"gets/";
    public static String KOST_UPDATE = KOST_URL+"update/";
    public static String KOST_BROADCAST_URL= KOST_URL+"broadcast";

    public static String KOST_MEMBER_LIST = KOST_URL+"member/";
    public static String KOST_MEMBER_MESSAGE = KOST_URL+"message/";
    public static String KOST_MEMBER_RELEASE = KOST_URL+"memberrelease/";
    public static String KOST_MEMBER_ASSIGN = KOST_URL+"memberassign/";
    public static String KOST_MEMBER_ADD = KOST_URL+"memberadd/";

    public static String KOST_ROOM_LIST = KOST_URL+"room/";
    public static String KOST_ROOM_ADD = KOST_URL+"roomadd/";
    public static String KOST_ROOM_GET = KOST_URL+"roomget/";
    public static String KOST_ROOM_DELETE = KOST_URL+"roomdelete/";

    public static String KOST_PAYMENT_LIST = KOST_URL+"payment/";
    public static String KOST_PAYMENT_ADD = KOST_URL+"paymentadd/";
    public static String KOST_PAYMENT_GET = KOST_URL+"paymentget/";
    public static String KOST_PAYMENT_DELETE = KOST_URL+"paymentdelete/";

    public static String MEMBER_LIST_URL = KOST_URL+"member/";

    public static String SUGGESTION_URL = KOST_URL+"suggest";

    public static String MEMBER_URL = API_URL+"member/";

    public static String MEMBER_MESSAGE_URL = MEMBER_URL+"message/";
    public static String MEMBER_REPORT_URL = MEMBER_URL+"report/";
    public static String LOGIN_URL = MEMBER_URL +"login";
    public static String REGISTER_URL= MEMBER_URL +"register/";

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        auth = new AuthLibrary(this);

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public String getId(){
        return UserID;
    }

    public void setId(String id){
        UserID = id;
    }


    public String getIMEI() {


    //getting unique id for device
    String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    public String getAppID() {

        return getMd5Hash(getIMEI());
    }

    public void setFacility(List<KeyPairBoolData> facility, Pair<CharSequence[],CharSequence[]> pref){
        this.facility = facility;
        this.facility_preference = pref;
    }

    public List<KeyPairBoolData> getFacility(){
        return this.facility;
    }

    public Pair<CharSequence[],CharSequence[]> getFacilityPreference(){
        return this.facility_preference;
    }

    public void setRules(List<KeyPairBoolData> rules,Pair<CharSequence[],CharSequence[]> pref){
        this.rules= rules;
        this.rules_preference = pref;
    }

    public List<KeyPairBoolData> getRules(){
        return this.rules;
    }

    public Pair<CharSequence[],CharSequence[]> getRulesPreference(){
        return this.rules_preference;
    }

    public String getKOSTID() {
        return KOSTID;
    }

    public void setKOSTID(String KOSTID) {
        this.KOSTID = KOSTID;
    }


    public String getUserID(){
        return getMd5Hash(auth.getUserDetails().get("id"));
    }

    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(8);

            while (md5.length() < 8)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }

    public JSONArray getCountry() {
        return country;
    }

    public void setCountry(JSONArray country) {
        this.country = country;
    }

    public boolean checkCountry(){
        if(this.country.length() > 0){
            return true;
        }

        return false;
    }
}