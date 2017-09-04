package com.example.earth.fuelfriend;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by EARTH on 4/09/2017.
 */

public class StatisticsHelper {

    DBHelper dbHelper;
    ArrayList<String> transport;
    ArrayList<CustomMarker> markers;

    public StatisticsHelper(Context context) {
        dbHelper = new DBHelper(context);
        transport = dbHelper.getAllTransport();
        markers = dbHelper.getAllMarkers();
    }

    public double getCarTotalFuelUsage() {

        return 0;
    }

    public double getWalkTotalFuelUsage() {

        return 0;
    }

    public double getBikeTotalFuelUsage() {

        return 0;
    }

    public double getWalkTotalDistance() {

        return 0;
    }

    public double getBikeTotalDistance() {

        return 0;
    }

    public double getCarTotalDistance() {

        return 0;
    }

    public double getCarLongestDistance() {

        return 0;
    }

    public double getBikeLongestDistance() {

        return 0;
    }

    public double getWalkLongestDistance() {

        return 0;
    }

    public double getMostFuelConsumingCar() {

        return 0;
    }

}
