package com.example.earth.fuelfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

/**
 * Created by EARTH on 30/08/2017.
 */

public class StatisticsFragment extends Fragment {

    StatisticsAdapter mStatisticsAdapter;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Car");
        labels.add("Walk");
        labels.add("Bike");
        PieDataSet dataset = new PieDataSet(entries, "Test");
        dataset.setColors(colors);
        dataset.setDrawValues(false);
        PieData data = new PieData(labels, dataset);

        pieChart.setData(data);
        pieChart.setCenterText("16L ");
        pieChart.highlightValue(1, 0);
        pieChart.setDescription("");
        pieChart.setTouchEnabled(false);
        pieChart.highlightValues(null);

        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        return view;
    }


}
