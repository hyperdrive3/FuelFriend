package com.example.earth.fuelfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.earth.fuelfriend.Constants.PREFS_NAME;
import static com.example.earth.fuelfriend.Constants.RATE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;


/**
 * Created by EARTH on 5/08/2017.
 */

final class GeneralHelper {

    /**
     * A method to download json data from url
     */
    static String downloadUrl(String strUrl) throws IOException {
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
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    static int getTransportIcon(String transport) {

        switch (transport) {
            case TRANSPORT_BIKE:
                return R.drawable.ic_bike_24dp;
            case TRANSPORT_CAR:
                return R.drawable.ic_car_24dp;
            default:
                return R.drawable.ic_walk_24dp;
        }
    }

    static int getTransportColor(String transport, Context context) {

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
    public static double getFuelConsumption(double distance, double litrePerKm) {
        return litrePerKm * distance;
    }

    // Converts values generated from Google Distance API such as Strings like: 10 m, 133 km, 9,999 km etc. and turns it into usable data
    static double convertStringDistanceToDouble(String distance) {
        String units = distance.substring(distance.indexOf(" ") + 1, distance.length());
        double number = Double.parseDouble(distance.substring(0, distance.indexOf(" ")));

        if(units.equals("km")) {
            return number;
        } else return number / 1000;

    }

    static String createTitleText(CustomMarker origin, String dest) {
        return dest + "|" + origin.getTransportMode();
    }

    static String createSnippetText(Double distance, String vehicle) {
        double fuelRate;
        String[] vehicleArray = vehicle.split(",");
        fuelRate = Double.valueOf(vehicleArray[RATE]) / 100;

        return String.format("%.1f", distance) + " km\n" +
                String.format("%.2f", fuelRate * distance) + " L|" + vehicle;
    }

    static Bitmap getBitmap(int drawableRes, Context context) {

        Drawable drawable = context.getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    static double gallonsToLitres(double gallons) {
        return gallons * 3.78541178;
    }

    static double milesToKm(double miles) {
        return miles * 1.60934;
    }

    static double litrePerHundredKm(double milesPerGallon) {

        double first = milesPerGallon * 1.60934;
        double second = first / 3.78541178;
        double reciprocal = (1 / second) * 100;

        BigDecimal bd = new BigDecimal(reciprocal);
        return bd.setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    static void displayAboutMessage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Fuel Friend is a UoW COMP477 project designed with the purpose of promoting energy conservation in our every day commutes. " +
                "At this point in time, the objective is to achieve a relatively complete distance/fuel consumption" +
                " application utilizing the Android Google Maps API." + "\n\n" + "Created by James Wong(1228302) \n\nSupervised by Mark Apperley")
                .setTitle("About Fuel Friend");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static String getDesignatedVehicle(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("currentCar", "");
    }

}
