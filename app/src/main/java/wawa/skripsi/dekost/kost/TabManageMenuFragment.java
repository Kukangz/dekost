package wawa.skripsi.dekost.kost;

/**
 * Created by Admin on 04/01/2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wawa.skripsi.dekost.R;

public class TabManageMenuFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4 ;
    private Bundle extras;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */

        extras = getArguments();
        View x =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */

        MyAdapter fragmentmanager = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            ManageMemberFragment memberFragment = new ManageMemberFragment();
            memberFragment.setArguments(extras);
            ManageRoomFragment roomFragment = new ManageRoomFragment();
            roomFragment.setArguments(extras);
            ManagePaymentFragment paymentFragment = new ManagePaymentFragment();
            paymentFragment.setArguments(extras);
            HistoryFragment historyFragment = new HistoryFragment();
            historyFragment.setArguments(extras);

            switch (position){
                case 0 : return memberFragment;
                case 1 : return roomFragment;
                case 2 : return paymentFragment;
                case 3 : return historyFragment;
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Member";
                case 1 :
                    return "Room";
                case 2 :
                    return "Payment";
                case 3 :
                    return "History";
            }
            return null;
        }
    }

}
