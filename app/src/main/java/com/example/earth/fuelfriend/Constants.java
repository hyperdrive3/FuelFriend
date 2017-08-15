package com.example.earth.fuelfriend;

import android.graphics.Color;

/**
 * Created by EARTH on 5/08/2017.
 */

final class Constants {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Marker.db";
    public static final String MKR_TABLE_NAME = "marker_checkpoints";
    public static final String MKR_ID = "id"; // PRIMARY KEY
    public static final String MKR_LAT = "latitude";
    public static final String MKR_LNG = "longitude";
    public static final String MKR_DATE = "date_time";
    public static final String MKR_DISTANCE = "distance";
    public static final String MKR_GEOLOCATION = "geolocation";
    public static final String MKR_TRANSPORT = "transport_mode";


    public static final String TRANSPORT_CAR = "CAR";
    public static final String TRANSPORT_WALK = "WALK";
    public static final String TRANSPORT_BIKE = "BIKE";

    public static final int POLYLINE_CAR = Color.MAGENTA;
    public static final int POLYLINE_WALK = Color.BLUE;
    public static final int POLYLINE_BIKE = Color.GREEN;

    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_WALK = "ACTION_WALK";
    public static final String ACTION_CAR = "ACTION_CAR";
    public static final String ACTION_BIKE = "ACTION_BIKE";
}
