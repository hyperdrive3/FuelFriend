package com.example.earth.fuelfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by EARTH on 22/08/2017.
 */

public class GarageFragment extends Fragment {

    GarageAdapter mGarageAdapter;
    
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.garage_swipe_view, container, false);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorWalkLine));

        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        mGarageAdapter = new GarageAdapter(getChildFragmentManager(), getContext());
        vpPager.setAdapter(mGarageAdapter);

        return view;
    }



}
