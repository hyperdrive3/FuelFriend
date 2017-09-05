package com.example.earth.fuelfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

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
        inDatabase = dbHelper.checkIfTransportInDb(data[MAKE], data[MODEL], data[YEAR], data[CLASS], data[TRANSMISSION], data[TRAIN],
                Double.toString(GeneralHelper.litrePerHundredKm(Double.valueOf(data[RATE]))),
                data[TYPE], data[COSTS], data[SAVINGS]);

        View v = inflater.inflate(R.layout.vehicle_profile, container, false);

        TextView tv_vclass = (TextView) v.findViewById(R.id.vclass);
        TextView tv_trans = (TextView) v.findViewById(R.id.transmission);
        TextView tv_dtrain = (TextView) v.findViewById(R.id.drivetrain);
        TextView tv_fuelrate = (TextView) v.findViewById(R.id.fuelrate);
        TextView tv_fueltype = (TextView) v.findViewById(R.id.fuel_type);
        TextView tv_costs = (TextView) v.findViewById(R.id.annual_cost);
        TextView tv_savings = (TextView) v.findViewById(R.id.annual_savings);
        TextView tv_header_year = (TextView) v.findViewById(R.id.header_year);
        TextView tv_header_makemodel = (TextView) v.findViewById(R.id.header_carmakemodel);

        tv_header_year.setText(data[YEAR]);
        tv_header_makemodel.setText(data[MAKE] + " " + data[MODEL]);

        tv_vclass.setText(data[CLASS]);
        tv_trans.setText(data[TRANSMISSION]);
        tv_dtrain.setText(data[TRAIN]);
        tv_fuelrate.setText(Double.toString(GeneralHelper.litrePerHundredKm(Double.valueOf(data[RATE]))) + " Litres/100km");
        tv_fueltype.setText(data[TYPE]);
        tv_costs.setText("$" + data[COSTS]);
        tv_savings.setText("$" + data[SAVINGS]);

        Button profileBack = (Button) v.findViewById(R.id.profile_back);
        Button addRemove = (Button) v.findViewById(R.id.add_remove);

        final FragmentManager fm = getFragmentManager();

        profileBack.setTextColor(getResources().getColor(R.color.colorInfoWindowFont));
        profileBack.setBackgroundColor(getResources().getColor(R.color.colorWalkLine));
        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroyThisFragment();
                List<Fragment> test = fm.getFragments();
                for (Fragment f : test) {
                    try {
                        String s = f.getArguments().getString("searchFragment");
                        if (s.equals("searchFragment")) {
                            getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, f).commit(); // Go back to the previous fragment
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }

            }
        });

        removeOrAdd(addRemove);
        addRemove.setTextColor(getResources().getColor(R.color.colorInfoWindowFont)); // Remove when I make a custom button layout
        addRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inDatabase) {
                    dbHelper.insertTransport(data[MAKE], data[MODEL], data[YEAR], data[CLASS], data[TRANSMISSION], data[TRAIN],
                            Double.toString(GeneralHelper.litrePerHundredKm(Double.valueOf(data[RATE]))),
                            data[TYPE], data[COSTS], data[SAVINGS]);
                } else {
                    dbHelper.removeTransport(data[MAKE], data[MODEL], data[YEAR], data[CLASS], data[TRANSMISSION], data[TRAIN],
                            Double.toString(GeneralHelper.litrePerHundredKm(Double.valueOf(data[RATE]))),
                            data[TYPE], data[COSTS], data[SAVINGS]);
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
