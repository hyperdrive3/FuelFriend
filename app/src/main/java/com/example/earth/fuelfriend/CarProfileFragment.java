package com.example.earth.fuelfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by EARTH on 20/08/2017.
 */

public class CarProfileFragment extends Fragment {


    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String data[] = getArguments().getString("data").split(",");

        v = inflater.inflate(R.layout.vehicle_profile, container, false);
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

        tv_make.setText(data[4]);
        tv_model.setText(data[5]);
        tv_year.setText(data[8]);
        tv_vclass.setText(data[7]);
        tv_trans.setText(data[6]);
        tv_dtrain.setText(data[1]);
        tv_fuelrate.setText(Double.toString(GeneralHelper.litrePerHundredKm(Double.valueOf(data[0]))) + " Litres/100km");
        tv_fueltype.setText(data[3]);
        tv_costs.setText(data[2]);
        tv_savings.setText(data[9]);

        Button profileBack = (Button) v.findViewById(R.id.profile_back);
        Button addRemove = (Button) v.findViewById(R.id.add_remove);

        final FragmentManager fm = getFragmentManager();

        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fm.getFragments().get(1)).commit(); // i dont know why this works
            }
        });

        addRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Add new DB entry and check if already in DB");
            }
        });

        return v;
    }

    public void remove() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
