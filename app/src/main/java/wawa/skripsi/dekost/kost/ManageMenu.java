package wawa.skripsi.dekost.kost;

/**
 * Created by Admin on 04/01/2016.
 */

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import wawa.skripsi.dekost.R;


public class ManageMenu extends AppCompatActivity{

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        Bundle extras = getIntent().getExtras();
        String message = extras.getString("title");
        String id = extras.getString("kost_id");


        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */


        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(message);
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        TabManageMenuFragment tmF = new TabManageMenuFragment();
        tmF.setArguments(extras);

        mFragmentTransaction.replace(R.id.containerView,tmF).commit();



    }
}
