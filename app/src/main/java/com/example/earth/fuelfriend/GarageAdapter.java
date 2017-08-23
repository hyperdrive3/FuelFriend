package com.example.earth.fuelfriend;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import static com.example.earth.fuelfriend.Constants.MODEL;

/**
 * Created by EARTH on 22/08/2017.
 */

public class GarageAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> mVehicleGarage;
    private DBHelper dbHelper;

    public GarageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        dbHelper = new DBHelper(context);
        mVehicleGarage = new DBHelper(context).getAllTransport();
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mVehicleGarage.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
        String[] title = mVehicleGarage.get(position).split(",");
        return title[MODEL];
    }

    public void refreshGarage() {
        ArrayList<String> currentData = dbHelper.getAllTransport();
        if (currentData.size() != getCount()) {
            mVehicleGarage = currentData;
            notifyDataSetChanged();
        }
    }
}
