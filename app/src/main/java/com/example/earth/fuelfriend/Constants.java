package com.example.earth.fuelfriend;

/**
 * Created by EARTH on 5/08/2017.
 */

final class Constants {

    public static final String PREFS_NAME = "preferences";
    static final int DATABASE_VERSION = 1;
    static final String FF_DATABASE_NAME = "fuelfriend.db";
    // Marker Table
    static final String MKR_TABLE_NAME = "marker_checkpoints";
    static final String MKR_ID = "id"; // PRIMARY KEY
    static final String MKR_LAT = "latitude";
    static final String MKR_LNG = "longitude";
    static final String MKR_DATE = "date_time";
    static final String MKR_DISTANCE = "distance";
    static final String MKR_GEOLOCATION = "geolocation";
    static final String MKR_TRANSPORT = "transport_mode";
    // Transport Table
    static final String TRANS_TABLE_NAME = "transport_options";
    static final String TRANS_ID = "id";
    static final String TRANS_MODEL = "car_model";
    static final String TRANS_MAKE = "car_make";
    static final String TRANS_YEAR = "year";
    static final String TRANS_FUEL_PER_KM = "approx_use";
    static final String TRANS_CLASS = "vehicle_class";
    static final String TRANS_TRANSMISSION = "transmission";
    static final String TRANS_DRIVETRAIN = "drive_train";
    static final String TRANS_FUEL_TYPE = "fuel_type";
    static final String TRANS_ANNUAL_COST = "annual_cost";
    static final String TRANS_ANNUAL_SAVING = "annual_saving";
    static final String TRANSPORT_CAR = "CAR";
    static final String TRANSPORT_WALK = "WALK";
    static final String TRANSPORT_BIKE = "BIKE";
    static final int NOTIFICATION_ID = 1;
    static final String ACTION_WALK = "ACTION_WALK";
    static final String ACTION_CAR = "ACTION_CAR";
    static final String ACTION_BIKE = "ACTION_BIKE";
    static final int MAKE = 4;
    static final int MODEL = 5;
    static final int YEAR = 8;
    static final int CLASS = 7;
    static final int TRANSMISSION = 6;
    static final int TRAIN = 1;
    static final int RATE = 0;
    static final int TYPE = 3;
    static final int COSTS = 2;
    static final int SAVINGS = 9;
}
