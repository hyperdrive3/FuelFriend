package com.example.earth.fuelfriend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.earth.fuelfriend.Constants.CLASS;
import static com.example.earth.fuelfriend.Constants.COSTS;
import static com.example.earth.fuelfriend.Constants.MAKE;
import static com.example.earth.fuelfriend.Constants.MODEL;
import static com.example.earth.fuelfriend.Constants.PREFS_NAME;
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
    private DBHelper dbHelper;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        dbHelper = new DBHelper(getContext());
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

        final Button designate = (Button) v.findViewById(R.id.designate);
        Button remove = (Button) v.findViewById(R.id.remove);
        designate.setTextColor(getResources().getColor(R.color.colorInfoWindowFont));
        if (isDesignated()) {
            designate.setBackgroundColor(getResources().getColor(R.color.colorBikeLine));
            designate.setText("Designated");

        } else {
            designate.setBackgroundColor(getResources().getColor(R.color.colorWalkLine));
            designate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Designate " + data[YEAR] + " " + data[MAKE] + " " + data[MODEL] + " as currently driven car?");

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            designateCar(getArguments().getString("vehicle"));
                            Button designateButton = (Button) view;
                            designateButton.setBackgroundColor(getResources().getColor(R.color.colorBikeLine));
                            designateButton.setText("Designated");

                            refreshFragment(container);

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });


        }

        remove.setTextColor(getResources().getColor(R.color.colorInfoWindowFont));
        remove.setBackgroundColor(getResources().getColor(R.color.colorCarLine));
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Remove " + data[YEAR] + " " + data[MAKE] + " " + data[MODEL] + " from garage?");

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.removeTransport(data[MAKE], data[MODEL], data[YEAR], data[CLASS],
                                data[TRANSMISSION], data[TRAIN], data[RATE],
                                data[TYPE], data[COSTS], data[SAVINGS]);

                        if (isDesignated())
                            designateCar(""); //empties the car designation preference upon removal of vehicle

                        refreshFragment(container);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return v;
    }

    public boolean isDesignated() {
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        String vehicle = settings.getString("currentCar", "");
        return vehicle.equals(getArguments().getString("vehicle"));
    }

    public void refreshFragment(ViewGroup container) {
        ViewPager sliding = (ViewPager) container.findViewById(R.id.vpPager);
        GarageAdapter ga = (GarageAdapter) sliding.getAdapter();
        ga.refreshGarage();
        ga.notifyDataSetChanged();
    }

    public void designateCar(String car) {
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("currentCar", car);
        editor.apply();
    }
}
