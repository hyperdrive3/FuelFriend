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

    public double getFuelUsage(CustomMarker cm) {

        String[] vehicle = cm.getVehicle().split(",");
        double distance = cm.getDistance();

        return distance * Double.valueOf(vehicle[0]) / 100;
    }

    public double getTotalTransportFuelUsage(String transport) {

        double totalFuel = 0;

        for (CustomMarker cm : markers) {
            if (cm.getTransportMode().equals(transport)) {
                totalFuel += getFuelUsage(cm);
            }
        }

        return totalFuel;
    }

    public double getTotalTransportDistance(String transport) {

        double totalDistance = 0;

        for (CustomMarker cm : markers) {
            if (cm.getTransportMode().equals(transport)) {
                totalDistance += cm.getDistance();
            }
        }

        return totalDistance;
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
