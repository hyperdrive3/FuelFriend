package com.example.earth.fuelfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by EARTH on 30/08/2017.
 */

public class StatTransportFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.statistics_graph, container, false); // create layouts for this

        return v;

    }
}
