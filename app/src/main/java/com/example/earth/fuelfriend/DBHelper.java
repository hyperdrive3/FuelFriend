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

import static com.example.earth.fuelfriend.Constants.DATABASE_NAME;
import static com.example.earth.fuelfriend.Constants.DATABASE_VERSION;
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

/**
 * Created by EARTH on 2/08/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    static ArrayList<LatLng> latLngArrayList;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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


    public ArrayList<CustomMarker> getAllMarkers() {

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
    public ContentValues getCV(LatLng l, String transport, String date) {

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

    public void setDefaultLabel(SQLiteDatabase db) {
        // create default label
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        for(int i = 0; i < latLngArrayList.size(); i++) {

            LatLng l = latLngArrayList.get(i);
            if(i == 2 || i == 5)
                db.insert(MKR_TABLE_NAME, null, getCV(l, TRANSPORT_WALK, currentDateandTime));
            else if(i == 4 || i == 6){
                db.insert(MKR_TABLE_NAME, null, getCV(l, TRANSPORT_BIKE, currentDateandTime));
            }else
            db.insert(MKR_TABLE_NAME, null, getCV(l, TRANSPORT_CAR, currentDateandTime));
        }

    }

    long id;
    public void insertMarker(LatLng location, String transport, String date_time, String geo_location) {

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

    public void updateEntryDistance(double distance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MKR_DISTANCE, distance);
        //SQL INCORRECT
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
