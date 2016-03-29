package wawa.skripsi.dekost.kost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import wawa.skripsi.dekost.AppController;
import wawa.skripsi.dekost.R;


/**
 * Created by Admin on 05/01/2016.
 */
public class RoomAdd extends AppCompatActivity {

    private static final String TAG = RoomAdd.class.getSimpleName();

    private EditText name, x,y, price, description;
    private AppCompatButton upload;
    private ImageView uploadImage;

    private ProgressDialog mProgressDialog;
    private String filepath;
    private String kostid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_add_layout);

        Bundle extras = getIntent().getExtras();
        kostid = extras.getString("kostid");
        Log.e("extras on add", extras.toString());

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        name = (EditText) findViewById(R.id.name);

        x = (EditText) findViewById(R.id.length);
        y = (EditText) findViewById(R.id.width);
        price = (EditText) findViewById(R.id.price);
        description = (EditText) findViewById(R.id.description);
        upload = (AppCompatButton) findViewById(R.id.selectImageButton);
        uploadImage = (ImageView) findViewById(R.id.uploadImage);

        mProgressDialog = new ProgressDialog(RoomAdd.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        });



        AppCompatButton button = (AppCompatButton) findViewById(R.id.room_add);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.setMaxRetriesAndTimeout(5, 10000);
                String uri = "?";
                uri += "name="+name.getText().toString();
                uri += "&size_x="+x.getText().toString();
                uri += "&size_y="+y.getText().toString();
                uri += "&price="+price.getText().toString();
                uri += "&kost="+kostid;
                uri += "&member=0";
                uri += "&description="+description.getText().toString();
                Log.e("uri register", AppController.KOST_ROOM_ADD+uri);
                client.get(AppController.KOST_ROOM_ADD+uri, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                        mProgressDialog.setTitle("Registering new Room...");
                        mProgressDialog.setMessage("Please Wait...");
                        mProgressDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // called when response HTTP status is "200 OK"
                        Log.i(TAG, "Room Successfully Added");
                        mProgressDialog.hide();
                        Toast.makeText(RoomAdd.this, "Room Successfully Added", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                        Log.i(TAG, "Room Add Failed");
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

    /**
     * Retrives the result returned from selecting image, by invoking the method
     * <code>selectImageFromGallery()</code>
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            decodeFile(picturePath);

        }
    }

    /** The method decodes the image file to avoid out of memory issues. Sets the
     * selected image in to the ImageView.
     *
     * @param filePath
     */
    public void decodeFile(String filePath) {

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

        uploadImage.setImageBitmap(bitmap);
        uploadImage.setTag(filePath);

        filepath = filePath;
    }


    public void upload_image(String filepath){
        RequestParams data = new RequestParams();
        data.put("key","some_key");

        File image = new File(filepath);
        try {
            data.put("file_upload", image);
        }catch (FileNotFoundException e){
            Log.e("File not found", e.toString());
        }

        //Create a new AsyncHttpClient request.
        //here we're sending a POST request, the first parameter is the request URL
        // then our RequestParams, in our case data and then a Json response handler.
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://techulus/mprocessupload.php", data, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray success) {
                try {
                    JSONObject data = success.getJSONObject(0);
                    String m = data.getString("message");
                    Toast.makeText(RoomAdd.this, m, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }




}
