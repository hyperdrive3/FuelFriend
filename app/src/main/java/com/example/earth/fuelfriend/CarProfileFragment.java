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

    private String data[];
    private DBHelper dbHelper;
    private boolean inDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbHelper = new DBHelper(getContext());
        data = getArguments().getString("data").split(",");
        inDatabase = dbHelper.checkIfTransportInDb(data[4], data[5]);

        View v = inflater.inflate(R.layout.vehicle_profile, container, false);
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


        profileBack.setTextColor(getResources().getColor(R.color.colorInfoWindowFont));
        profileBack.setBackgroundColor(getResources().getColor(R.color.colorWalkLine));
        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroyThisFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fm.getFragments().get(1)).commit(); // Go back to the previous fragment
            }
        });

        removeOrAdd(addRemove);
        addRemove.setTextColor(getResources().getColor(R.color.colorInfoWindowFont)); // Remove when I make a custom button layout
        addRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inDatabase) {
                    dbHelper.insertTransport(data[4], data[5], data[8], data[7], data[6], data[1],
                            Double.toString(GeneralHelper.litrePerHundredKm(Double.valueOf(data[0]))),
                            data[3], data[2], data[9]);
                } else {
                    dbHelper.removeTransport(data[4], data[5]);
                }
                inDatabase = !inDatabase;
                removeOrAdd((Button) view.findViewById(R.id.add_remove));
            }
        });

        return v;
    }

    // Changes button text to remove or add if the viewed car is in the database or not
    public void removeOrAdd(Button addRemove) {
        if (inDatabase) {
            addRemove.setText(R.string.remove_vehicle);
            addRemove.setBackgroundColor(getResources().getColor(R.color.colorCarLine));
        } else {
            addRemove.setBackgroundColor(getResources().getColor(R.color.colorBikeLine));
            addRemove.setText(R.string.add_vehicle);
        }
    }

    public void destroyThisFragment() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
