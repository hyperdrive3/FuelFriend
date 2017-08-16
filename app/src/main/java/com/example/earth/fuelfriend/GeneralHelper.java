package com.example.earth.fuelfriend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;


/**
 * Created by EARTH on 5/08/2017.
 */

public final class GeneralHelper {

    /**
     * A method to download json data from url
     */
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static int getIcon(String transport) {

        switch (transport) {
            case TRANSPORT_BIKE:
                return R.drawable.ic_bike_24dp;
            case TRANSPORT_CAR:
                return R.drawable.ic_car_24dp;
            default:
                return R.drawable.ic_walk_24dp;
        }
    }

    public static int getTransportColor(String transport, Context context) {

        int polyline_color;
        switch (transport) {
            case TRANSPORT_CAR:
                polyline_color = context.getResources().getColor(R.color.colorCarLine);
                break;
            case TRANSPORT_BIKE:
                polyline_color = context.getResources().getColor(R.color.colorBikeLine);
                break;
            default:
                polyline_color = context.getResources().getColor(R.color.colorWalkLine);
        }

        return polyline_color;
    }

    // Expand on this to make it customizible via UI
    public static double getFuelConsumption(double distance) {
        return 0.0500 * distance;
    }

    // Converts values generated from Google Distance API such as Strings like: 10 m, 133 km, 9,999 km etc. and turns it into usable data
    public static double convertStringDistanceToDouble(String distance) {
        String units = distance.substring(distance.indexOf(" ") + 1, distance.length());
        double number = Double.parseDouble(distance.substring(0, distance.indexOf(" ")));

        if(units.equals("km")) {
            return number;
        } else return number / 1000;

    }

    public static String createTitleText(CustomMarker origin, String dest) {
        return dest + "|" + origin.getTransportMode();
    }

    public static String createSnippetText(Double distance) {
        return String.format("%.1f", distance) + " km\n" +
                String.format("%.2f", 0.05 * distance) + " Litres";
    }

    public static Bitmap getBitmap(int drawableRes, Context context) {

        Drawable drawable = context.getDrawable(drawableRes);

        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }



}
