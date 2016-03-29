package wawa.skripsi.dekost.kost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import wawa.skripsi.dekost.R;

/**
 * Created by Admin on 08/01/2016.
 */
public class ManageRoomDetail extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_room_detail_layout);

        Bundle extras = getIntent().getExtras();
        String message = extras.getString("title");
        String detail = extras.getString("detail");
        Log.e("detail", detail);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(ManageRoomDetail.this, ManageRoomEdit.class);
                e.putExtra("roomid", "11232131");
                startActivity(e);
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(ManageRoomDetail.this).load("http://lorempixel.com/400/400/").memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE).into(imageView);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setTitle(message);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
