package com.example.earth.fuelfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.earth.fuelfriend.Constants.CLASS;
import static com.example.earth.fuelfriend.Constants.COSTS;
import static com.example.earth.fuelfriend.Constants.MAKE;
import static com.example.earth.fuelfriend.Constants.MODEL;
import static com.example.earth.fuelfriend.Constants.RATE;
import static com.example.earth.fuelfriend.Constants.SAVINGS;
import static com.example.earth.fuelfriend.Constants.TRAIN;
import static com.example.earth.fuelfriend.Constants.TRANSMISSION;
import static com.example.earth.fuelfriend.Constants.TYPE;
import static com.example.earth.fuelfriend.Constants.YEAR;

/**
 * Created by EARTH on 22/08/2017.
 */

public class GarageCarProfile extends Fragment {

    private String[] data;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        data = getArguments().getString("vehicle").split(",");

        final View v = inflater.inflate(R.layout.vehicle_tab, container, false);
        TextView tv_year = (TextView) v.findViewById(R.id.year);
        TextView tv_make = (TextView) v.findViewById(R.id.make);
        TextView tv_model = (TextView) v.findViewById(R.id.model);
        TextView tv_vclass = (TextView) v.findViewById(R.id.vclass);
        TextView tv_trans = (TextView) v.findViewById(R.id.transmission);
        TextView tv_dtrain = (TextView) v.findViewById(R.id.drivetrain);
        TextView tv_fuelrate = (TextView) v.findViewById(R.id.fuelrate);
        TextView tv_fueltype = (TextView) v.findViewById(R.id.fuel_type);
        TextView tv_costs = (TextView) v.findViewById(R.id.annual_cost);
        TextView tv_savings = (TextView) v.findViewById(R.id.annual_savings);

        tv_make.setText(data[MAKE]);
        tv_model.setText(data[MODEL]);
        tv_year.setText(data[YEAR]);
        tv_vclass.setText(data[CLASS]);
        tv_trans.setText(data[TRANSMISSION]);
        tv_dtrain.setText(data[TRAIN]);
        tv_fuelrate.setText(data[RATE] + " Litres/100km");
        tv_fueltype.setText(data[TYPE]);
        tv_costs.setText(data[COSTS]);
        tv_savings.setText(data[SAVINGS]);

        Button designate = (Button) v.findViewById(R.id.designate);
        Button remove = (Button) v.findViewById(R.id.remove);

        designate.setTextColor(getResources().getColor(R.color.colorInfoWindowFont));
        designate.setBackgroundColor(getResources().getColor(R.color.colorWalkLine));
        remove.setTextColor(getResources().getColor(R.color.colorInfoWindowFont));
        remove.setBackgroundColor(getResources().getColor(R.color.colorCarLine));
        return v;
    }
}
