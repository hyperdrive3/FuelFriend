package com.example.earth.fuelfriend;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by EARTH on 6/08/2017.
 */

public class CustomPolyline {

    private PolylineOptions mPolyline;
    private String mDistance;

    public CustomPolyline(PolylineOptions pl, String d) {
        mPolyline = pl;
        mDistance = d;
    }

    public void setPolylineColor(int color) {
        mPolyline.color(color);
    }

    public void setPolylineThickness(float size) {
        mPolyline.width(size);
    }

    public String getPolylineDistance() {
        return mDistance;
    }

    public PolylineOptions getPolyLine() {
        return mPolyline;
    }


}
