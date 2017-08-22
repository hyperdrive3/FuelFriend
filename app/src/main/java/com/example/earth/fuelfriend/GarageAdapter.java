package com.example.earth.fuelfriend;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by EARTH on 22/08/2017.
 */

public class GarageAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public GarageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new GarageCarProfile();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new GarageCarProfile();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new GarageCarProfile();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}
