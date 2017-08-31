package com.example.earth.fuelfriend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by EARTH on 30/08/2017.
 */

public class StatisticsAdapter extends FragmentStatePagerAdapter {

    private final static int STATS_SIZE = 4;

    public StatisticsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StatGraphFragment();
            case 1:
                return new StatGraphFragment();
            case 2:
                return new StatGraphFragment();
            case 3:
                return new StatTransportFragment();
            default:
                return new StatGraphFragment();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return STATS_SIZE;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Dashboard";
            case 1:
                return "Bike Stats";
            case 2:
                return "Walk Stats";
            case 3:
                return "Drive Stats";
            default:
                return "Error";
        }
    }
}
