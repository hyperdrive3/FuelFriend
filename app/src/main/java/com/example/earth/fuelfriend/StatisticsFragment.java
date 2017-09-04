package com.example.earth.fuelfriend;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_WALK;

/**
 * Created by EARTH on 30/08/2017.
 */

public class StatisticsFragment extends Fragment {

    StatisticsHelper statHelper;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        statHelper = new StatisticsHelper(getContext());

        View view = inflater.inflate(R.layout.statistics_home, container, false);

        LinearLayout summary = (LinearLayout) view.findViewById(R.id.stats_car);

        summary.setClickable(true);
        summary.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub

                return false;
            }
        });

        PieChart pieChart = (PieChart) view.findViewById(R.id.pie_chart);


        final int[] MY_COLORS = {getActivity().getResources().getColor(R.color.colorCarLine), getActivity().getResources().getColor(R.color.colorWalkLine), getActivity().getResources().getColor(R.color.colorBikeLine)};
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : MY_COLORS) colors.add(c);


        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        float totalFuelSavedCar = (float) statHelper.getTotalTransportFuelUsage(TRANSPORT_CAR);
        float totalFuelSavedBike = (float) statHelper.getTotalTransportFuelUsage(TRANSPORT_BIKE);
        float totalFuelSavedWalk = (float) statHelper.getTotalTransportFuelUsage(TRANSPORT_WALK);


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(totalFuelSavedCar, 0));
        entries.add(new Entry(totalFuelSavedWalk, 1));
        entries.add(new Entry(totalFuelSavedBike, 2));

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Car");
        labels.add("Walk");
        labels.add("Bike");
        PieDataSet dataset = new PieDataSet(entries, "Test");
        dataset.setColors(colors);
        dataset.setDrawValues(false);
        PieData data = new PieData(labels, dataset);

        pieChart.setData(data);

        float totalFuelUsage = totalFuelSavedBike + totalFuelSavedWalk + totalFuelSavedCar;
        pieChart.setCenterText((df.format(totalFuelUsage)) + "L");
        pieChart.setCenterTextSize(25);
        pieChart.setCenterTextTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        pieChart.highlightValue(1, 0);
        pieChart.setDescription("");
        pieChart.setTouchEnabled(false);
        pieChart.highlightValues(null);
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);


        TextView pieSaved = (TextView) view.findViewById(R.id.pie_total_saved);
        float fuelSaved = totalFuelSavedBike + totalFuelSavedWalk;
        pieSaved.setText(df.format(fuelSaved) + "L saved\nWalking and Biking");


        // Car
        TextView carDistance = (TextView) view.findViewById(R.id.total_car_distance);
        float totalCarDist = (float) statHelper.getTotalTransportDistance(TRANSPORT_CAR);
        carDistance.setText(df.format(totalCarDist) + "km");

        TextView carFuel = (TextView) view.findViewById(R.id.total_car_fuel);
        carFuel.setText(df.format(totalFuelSavedCar) + "L");

        // Bike
        TextView bikeDistance = (TextView) view.findViewById(R.id.total_bike_distance);
        float totalBikeDist = (float) statHelper.getTotalTransportDistance(TRANSPORT_BIKE);
        bikeDistance.setText(df.format(totalBikeDist) + "km");

        TextView bikeFuel = (TextView) view.findViewById(R.id.total_bike_fuel);
        bikeFuel.setText(df.format(totalFuelSavedBike) + "L");

        // Walk
        TextView walkDistance = (TextView) view.findViewById(R.id.total_walk_distance);
        float totalWalkDist = (float) statHelper.getTotalTransportDistance(TRANSPORT_WALK);
        walkDistance.setText(df.format(totalWalkDist) + "km");

        TextView walkFuel = (TextView) view.findViewById(R.id.total_walk_fuel);
        walkFuel.setText(df.format(totalFuelSavedWalk) + "L");
        return view;
    }


}
