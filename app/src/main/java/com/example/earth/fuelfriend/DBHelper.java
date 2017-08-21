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
import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_WALK;
import static com.example.earth.fuelfriend.Constants.TRANS_CAPACITY;
import static com.example.earth.fuelfriend.Constants.TRANS_FUEL_PER_KM;
import static com.example.earth.fuelfriend.Constants.TRANS_ID;
import static com.example.earth.fuelfriend.Constants.TRANS_MAKE;
import static com.example.earth.fuelfriend.Constants.TRANS_MODEL;
import static com.example.earth.fuelfriend.Constants.TRANS_TABLE_NAME;
import static com.example.earth.fuelfriend.Constants.TRANS_YEAR;

/**
 * Created by EARTH on 2/08/2017.
 */

class DBHelper extends SQLiteOpenHelper {

    private static ArrayList<LatLng> latLngArrayList;


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
                    MKR_TRANSPORT + " TEXT)"
                 );

        db.execSQL("CREATE TABLE " + TRANS_TABLE_NAME + "(" +
                TRANS_ID + " INTEGER PRIMARY KEY, " +
                TRANS_MODEL + " TEXT, " +
                TRANS_MAKE + " TEXT, " +
                TRANS_CAPACITY + " REAL, " +
                TRANS_FUEL_PER_KM + " REAL, " +
                TRANS_YEAR + " REAL)"
        );

        setDefaultLabel(db);
    }


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

    ArrayList<String> getAllTransport() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TRANS_TABLE_NAME, null);

        ArrayList<String> transport = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                transport.add(c.getString(c.getColumnIndex(TRANS_YEAR)) + " " + c.getString(c.getColumnIndex(TRANS_MAKE)) + " " + c.getString(c.getColumnIndex(TRANS_MODEL)));
            } while (c.moveToNext());
        }

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
                markers.add(new CustomMarker(id, coordinates, date_time, transport, distance, geocode));
            } while (c.moveToNext());
        }

        return markers;
    }

    // Putting default values in
    private ContentValues createMarkerDbEntry(LatLng l, String transport, String date) {

        Random r = new Random();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MKR_LAT, l.latitude);
        contentValues.put(MKR_LNG, l.longitude);
        contentValues.put(MKR_DATE, date);
        contentValues.put(MKR_DISTANCE, 1 + (22 - 1) * r.nextDouble());
        contentValues.put(MKR_GEOLOCATION, "Hamilton City");
        contentValues.put(MKR_TRANSPORT, transport);

        return contentValues;
    }

    // Putting default transport values in
    private ContentValues createTransportDbEntry(String make, String model, double capacity, double rate, double year) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TRANS_MAKE, make);
        contentValues.put(TRANS_MODEL, model);
        contentValues.put(TRANS_CAPACITY, capacity);
        contentValues.put(TRANS_FUEL_PER_KM, rate);
        contentValues.put(TRANS_YEAR, year);

        return contentValues;

    }

    private void setDefaultLabel(SQLiteDatabase db) {
        // create default label
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        for(int i = 0; i < latLngArrayList.size(); i++) {

            LatLng l = latLngArrayList.get(i);
            if(i == 2 || i == 5)
                db.insert(MKR_TABLE_NAME, null, createMarkerDbEntry(l, TRANSPORT_WALK, currentDateandTime));
            else if(i == 4 || i == 6){
                db.insert(MKR_TABLE_NAME, null, createMarkerDbEntry(l, TRANSPORT_BIKE, currentDateandTime));
            }else
                db.insert(MKR_TABLE_NAME, null, createMarkerDbEntry(l, TRANSPORT_CAR, currentDateandTime));
        }

        // LITERS NOT GALLONS
        db.insert(TRANS_TABLE_NAME, null, createTransportDbEntry("Ferrari", "California T", 78, 0.13175, 2016));
        db.insert(TRANS_TABLE_NAME, null, createTransportDbEntry("Kia", "Niro FE", 45.05, 0.04732, 2017));
        db.insert(TRANS_TABLE_NAME, null, createTransportDbEntry("Lamborghini", "Aventador Roadster", 87.1, 0.18112, 2017));
        db.insert(TRANS_TABLE_NAME, null, createTransportDbEntry("Aston Martin", "V12 Vantage S", 79.87, 0.19523, 2017));

    }

    private long id;

    void insertMarker(LatLng location, String transport, String date_time, String geo_location) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MKR_LAT, location.latitude);
        contentValues.put(MKR_LNG, location.longitude);
        contentValues.put(MKR_TRANSPORT, transport);
        contentValues.put(MKR_DATE, date_time);
        contentValues.put(MKR_GEOLOCATION, geo_location);
        contentValues.put(MKR_DISTANCE, 0);

        id = db.insert(MKR_TABLE_NAME, null, contentValues);
    }

    void insertTransport(String vehicle) {
        String[] vehicleData = vehicle.split(",");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // TODO: Finish DB query to input data

    }

    void updateEntryDistance(double distance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MKR_DISTANCE, distance);

        db.update(MKR_TABLE_NAME, cv, MKR_ID + "=?", new String[] {String.valueOf(id)});
    }

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MKR_TABLE_NAME;

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
