package com.example.earth.fuelfriend;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by EARTH on 4/08/2017.
 */

public class CustomMarker {

    private int id;
    private LatLng coordinates;
    private String dateTime;
    private String transportMode;
    private Double distance;
    private String geoLocation;

    CustomMarker(int i, LatLng l, String d, String t, double dist, String geo) {
        id = i;
        coordinates = l;
        dateTime = d;
        transportMode = t;
        distance = dist;
        geoLocation = geo;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public int getId() {
        return id;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public Double getDistance() {
        return distance;
    }
}
