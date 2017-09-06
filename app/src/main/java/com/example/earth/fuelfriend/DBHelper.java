package com.example.earth.fuelfriend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static com.example.earth.fuelfriend.Constants.DATABASE_VERSION;
import static com.example.earth.fuelfriend.Constants.FF_DATABASE_NAME;
import static com.example.earth.fuelfriend.Constants.MKR_DATE;
import static com.example.earth.fuelfriend.Constants.MKR_DISTANCE;
import static com.example.earth.fuelfriend.Constants.MKR_GEOLOCATION;
import static com.example.earth.fuelfriend.Constants.MKR_ID;
import static com.example.earth.fuelfriend.Constants.MKR_LAT;
import static com.example.earth.fuelfriend.Constants.MKR_LNG;
import static com.example.earth.fuelfriend.Constants.MKR_TABLE_NAME;
import static com.example.earth.fuelfriend.Constants.MKR_TRANSPORT;
import static com.example.earth.fuelfriend.Constants.MKR_VEHICLE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_WALK;
import static com.example.earth.fuelfriend.Constants.TRANS_ANNUAL_COST;
import static com.example.earth.fuelfriend.Constants.TRANS_ANNUAL_SAVING;
import static com.example.earth.fuelfriend.Constants.TRANS_CLASS;
import static com.example.earth.fuelfriend.Constants.TRANS_DRIVETRAIN;
import static com.example.earth.fuelfriend.Constants.TRANS_FUEL_PER_KM;
import static com.example.earth.fuelfriend.Constants.TRANS_FUEL_TYPE;
import static com.example.earth.fuelfriend.Constants.TRANS_ID;
import static com.example.earth.fuelfriend.Constants.TRANS_MAKE;
import static com.example.earth.fuelfriend.Constants.TRANS_MODEL;
import static com.example.earth.fuelfriend.Constants.TRANS_TABLE_NAME;
import static com.example.earth.fuelfriend.Constants.TRANS_TRANSMISSION;
import static com.example.earth.fuelfriend.Constants.TRANS_YEAR;

/**
 * Created by EARTH on 2/08/2017.
 * <p>
 * NOTE: Value must be enclosed with ''
 */

class DBHelper extends SQLiteOpenHelper {

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MKR_TABLE_NAME;
    private static ArrayList<LatLng> latLngArrayList;

    static {

        latLngArrayList = new ArrayList<>();
        latLngArrayList.add(new LatLng(-37.768478, 175.335827));
        latLngArrayList.add(new LatLng(-37.788364, 175.311322));
        latLngArrayList.add(new LatLng(-37.796246, 175.294351));
        latLngArrayList.add(new LatLng(-37.802238, 175.304919));
        latLngArrayList.add(new LatLng(-37.798283, 175.295127));
        latLngArrayList.add(new LatLng(-37.748911, 175.232617));
        latLngArrayList.add(new LatLng(-37.727168, 175.252313));
        latLngArrayList.add(new LatLng(-37.866721, 175.339425));

    }

    private long id;

    DBHelper(Context context) {
        super(context, FF_DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MKR_TABLE_NAME + "(" +
                MKR_ID + " INTEGER PRIMARY KEY, " +
                MKR_LAT + " REAL, " +
                MKR_LNG + " REAL, " +
                MKR_DATE + " TEXT, " +
                MKR_DISTANCE + " REAL, " +
                MKR_GEOLOCATION + " TEXT, " +
                MKR_VEHICLE + " TEXT, " +
                MKR_TRANSPORT + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + TRANS_TABLE_NAME + "(" +
                TRANS_ID + " INTEGER PRIMARY KEY, " +
                TRANS_YEAR + " REAL, " +
                TRANS_MAKE + " TEXT, " +
                TRANS_MODEL + " TEXT, " +
                TRANS_CLASS + " TEXT, " +
                TRANS_TRANSMISSION + " TEXT, " +
                TRANS_DRIVETRAIN + " TEXT, " +
                TRANS_FUEL_PER_KM + " REAL, " +
                TRANS_FUEL_TYPE + " TEXT, " +
                TRANS_ANNUAL_COST + " REAL, " +
                TRANS_ANNUAL_SAVING + " REAL)"
        );

        setDefaultLabel(db);
    }

    public ArrayList<String> getAllTransport() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TRANS_TABLE_NAME, null);

        ArrayList<String> transport = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                transport.add(c.getString(c.getColumnIndex(TRANS_FUEL_PER_KM)) + "," +
                        c.getString(c.getColumnIndex(TRANS_DRIVETRAIN)) + "," +
                        c.getString(c.getColumnIndex(TRANS_ANNUAL_COST)) + "," +
                        c.getString(c.getColumnIndex(TRANS_FUEL_TYPE)) + "," +
                        c.getString(c.getColumnIndex(TRANS_MAKE)) + "," +
                        c.getString(c.getColumnIndex(TRANS_MODEL)) + "," +
                        c.getString(c.getColumnIndex(TRANS_TRANSMISSION)) + "," +
                        c.getString(c.getColumnIndex(TRANS_CLASS)) + "," +
                        c.getString(c.getColumnIndex(TRANS_YEAR)) + "," +
                        c.getString(c.getColumnIndex(TRANS_ANNUAL_SAVING)));
            } while (c.moveToNext());

        }

        db.close();
        return transport;
    }

    ArrayList<CustomMarker> getAllMarkers() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MKR_TABLE_NAME, null);

        ArrayList<CustomMarker> markers = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(MKR_ID));
                LatLng coordinates = new LatLng(c.getFloat(c.getColumnIndex(MKR_LAT)), c.getFloat(c.getColumnIndex(MKR_LNG)));
                String date_time = c.getString(c.getColumnIndex(MKR_DATE));
                double distance = Double.parseDouble(c.getString(c.getColumnIndex(MKR_DISTANCE)));
                String geocode = c.getString(c.getColumnIndex(MKR_GEOLOCATION));
                String transport = c.getString(c.getColumnIndex(MKR_TRANSPORT));
                String vehicle = c.getString(c.getColumnIndex(MKR_VEHICLE));
                markers.add(new CustomMarker(id, coordinates, date_time, transport, distance, geocode, vehicle));
            } while (c.moveToNext());
        }

        db.close();
        return markers;
    }

    // Putting default values in
    private ContentValues createMarkerDbEntry(LatLng l, String transport, String date) {

        Random r = new Random();
        ContentValues cv = new ContentValues();

        String test = "19,x,x,x,Toyota,Katana,x,x,2019,x";

        cv.put(MKR_LAT, l.latitude);
        cv.put(MKR_LNG, l.longitude);
        cv.put(MKR_DATE, date);
        cv.put(MKR_DISTANCE, 1 + (22 - 1) * r.nextDouble());
        cv.put(MKR_GEOLOCATION, "Hamilton City");
        cv.put(MKR_VEHICLE, test);
        cv.put(MKR_TRANSPORT, transport);

        return cv;
    }

    private void setDefaultLabel(SQLiteDatabase db) {
        // create default label
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        for (int i = 0; i < latLngArrayList.size(); i++) {

            LatLng l = latLngArrayList.get(i);
            if (i == 2 || i == 5)
                db.insert(MKR_TABLE_NAME, null, createMarkerDbEntry(l, TRANSPORT_WALK, currentDateandTime));
            else if (i == 4 || i == 6) {
                db.insert(MKR_TABLE_NAME, null, createMarkerDbEntry(l, TRANSPORT_BIKE, currentDateandTime));
            } else
                db.insert(MKR_TABLE_NAME, null, createMarkerDbEntry(l, TRANSPORT_CAR, currentDateandTime));
        }

    }

    void insertMarker(LatLng location, String transport, String date_time, String geo_location, String vehicle) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MKR_LAT, location.latitude);
        cv.put(MKR_LNG, location.longitude);
        cv.put(MKR_TRANSPORT, transport);
        cv.put(MKR_DATE, date_time);
        cv.put(MKR_GEOLOCATION, geo_location);
        cv.put(MKR_VEHICLE, vehicle);
        cv.put(MKR_DISTANCE, 0);

        id = db.insert(MKR_TABLE_NAME, null, cv);
        db.close();
    }

    int insertTransport(String make, String model, String year, String vclass, String transmission,
                        String dtrain, String fuelrate, String fueltype, String costs, String savings) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TRANS_MAKE, make);
        cv.put(TRANS_MODEL, model);
        cv.put(TRANS_YEAR, year);
        cv.put(TRANS_CLASS, vclass);
        cv.put(TRANS_TRANSMISSION, transmission);
        cv.put(TRANS_DRIVETRAIN, dtrain);
        cv.put(TRANS_FUEL_PER_KM, fuelrate);
        cv.put(TRANS_FUEL_TYPE, fueltype);
        cv.put(TRANS_ANNUAL_COST, costs);
        cv.put(TRANS_ANNUAL_SAVING, savings);

        int insert = (int) db.insert(TRANS_TABLE_NAME, null, cv);
        db.close();
        return insert;
    }

    // Update to remove with more information, copy checkifintransportDb
    int removeTransport(String make, String model, String year,
                        String vclass, String transmission,
                        String dtrain, String fuelrate, String fueltype,
                        String costs, String savings) {
        getAllTransport();
        SQLiteDatabase db = getWritableDatabase();
        int status = db.delete(TRANS_TABLE_NAME,
                TRANS_MAKE + "=? AND " +
                        TRANS_MODEL + "=? AND " +
                        TRANS_YEAR + "=? AND " +
                        TRANS_CLASS + "=? AND " +
                        TRANS_TRANSMISSION + "=? AND " +
                        TRANS_DRIVETRAIN + "=? AND " +
                        TRANS_FUEL_PER_KM + "=? AND " +
                        TRANS_FUEL_TYPE + "=? AND " +
                        TRANS_ANNUAL_COST + "=? AND " +
                        TRANS_ANNUAL_SAVING + "=?",
                new String[]{make, model, year, vclass, transmission, dtrain, fuelrate, fueltype, costs, savings});

        System.out.println(make + " " + model + " " + year + " " + vclass + " " + transmission + " " + dtrain + " " + fuelrate + " " + fueltype + " " + costs + " " + savings);
        getAllTransport();
        db.close();
        return status;
    }

    void updateEntryDistance(double distance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MKR_DISTANCE, distance);

        db.update(MKR_TABLE_NAME, cv, MKR_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    boolean checkIfTransportInDb(String make, String model, String year,
                                 String vclass, String transmission,
                                 String dtrain, String fuelrate, String fueltype,
                                 String costs, String savings) {

        SQLiteDatabase db = getReadableDatabase();
        String Query = "SELECT * FROM " + TRANS_TABLE_NAME + " WHERE "
                + TRANS_MAKE + " = '" + make + "' AND "
                + TRANS_MODEL + " = '" + model + "' AND "
                + TRANS_YEAR + " = '" + year + "' AND "
                + TRANS_CLASS + " = '" + vclass + "' AND "
                + TRANS_TRANSMISSION + " = '" + transmission + "' AND "
                + TRANS_DRIVETRAIN + " = '" + dtrain + "' AND "
                + TRANS_FUEL_PER_KM + " = '" + fuelrate + "' AND "
                + TRANS_FUEL_TYPE + " = '" + fueltype + "' AND "
                + TRANS_ANNUAL_COST + " = '" + costs + "' AND "
                + TRANS_ANNUAL_SAVING + " = '" + savings + "'";

        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
