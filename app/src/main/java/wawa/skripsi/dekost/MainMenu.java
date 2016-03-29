package wawa.skripsi.dekost;

/**
 * Created by Admin on 04/01/2016.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import wawa.skripsi.dekost.util.AuthLibrary;


public class MainMenu extends AppCompatActivity{

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);



        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;


        View header = mNavigationView.getHeaderView(0);

        final AuthLibrary auth = new AuthLibrary(this);
        HashMap<String,String> data = auth.getUserDetails();
        AppController.getInstance().setKOSTID(data.get(AuthLibrary.KOST_ID));
        AppController.getInstance().setId(data.get(AuthLibrary.KEY_ID));


        CircleImageView image = (CircleImageView) header.findViewById(R.id.profile_picture);
        TextView username = (TextView) header.findViewById(R.id.name);

        StringBuilder rackingSystemSb = new StringBuilder(data.get(AuthLibrary.KEY_NAME).toLowerCase());
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        username.setText(rackingSystemSb.toString());


        TextView memberid = (TextView) header.findViewById(R.id.memberid);
        memberid.setText("User ID : "+AppController.getInstance().getIMEI());

        Picasso.with(MainMenu.this).load("http://lorempixel.com/200/200/").memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE).into(image);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                Log.e("item pressed", String.valueOf(item.getItemId()));
                switch (item.getItemId()){
                    case R.id.nav_item_logout:
                        Log.e("item logout pressed", "ohai");
                        auth.logoutUser();
                        finish();
                        break;
                }
                return false;
            }
        });

        //image.setImageURI();
        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

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
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        if (keyCode == KeyEvent.KEYCODE_HOME){
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
