package com.example.earth.fuelfriend;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import static com.example.earth.fuelfriend.Constants.MODEL;

/**
 * Created by EARTH on 22/08/2017.
 */

public class GarageAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS;
    private ArrayList<String> mVehicleGarage;
    private DBHelper dbHelper;

    public GarageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        dbHelper = new DBHelper(context);
        mVehicleGarage = new DBHelper(context).getAllTransport();
        NUM_ITEMS = mVehicleGarage.size();
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("vehicle", mVehicleGarage.get(position));
        GarageCarProfile gcp = new GarageCarProfile();
        gcp.setArguments(bundle);
        return gcp;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        mVehicleGarage = dbHelper.getAllTransport();
        String[] title = mVehicleGarage.get(position).split(",");
        return title[MODEL];
    }
}
