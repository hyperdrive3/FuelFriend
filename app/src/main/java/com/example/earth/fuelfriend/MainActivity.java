package com.example.earth.fuelfriend;

/*

Created by James Wong for a Waikato University New Zealand COMP477 Project
Supervised by Mark Appereley

1228302

*/


import android.Manifest;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.earth.fuelfriend.Constants.ACTION_BIKE;
import static com.example.earth.fuelfriend.Constants.ACTION_CAR;
import static com.example.earth.fuelfriend.Constants.ACTION_WALK;
import static com.example.earth.fuelfriend.Constants.NOTIFICATION_ID;
import static com.example.earth.fuelfriend.Constants.POLYLINE_BIKE;
import static com.example.earth.fuelfriend.Constants.POLYLINE_CAR;
import static com.example.earth.fuelfriend.Constants.POLYLINE_WALK;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_WALK;
import static com.example.earth.fuelfriend.GeneralHelper.convertStringDistanceToDouble;
import static com.example.earth.fuelfriend.GeneralHelper.createSnippetText;
import static com.example.earth.fuelfriend.GeneralHelper.createTitleText;
import static com.example.earth.fuelfriend.GeneralHelper.downloadUrl;
import static com.example.earth.fuelfriend.GeneralHelper.getIcon;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mSupportMapFragment;
    private LocationManager mLocationManager;
    private DBHelper dbHelper;
    private ArrayList<CustomMarker> markerList;
    private HashMap<LatLng, CustomPolyline> polyLinesList;
    private ArrayList<Marker> googleMapMarkers;
    private NotificationUtils notifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notifications = new NotificationUtils();
        googleMapMarkers = new ArrayList<>();
        dbHelper = new DBHelper(this);
        markerList = dbHelper.getAllMarkers();
        polyLinesList = new HashMap<>();
        mSupportMapFragment = SupportMapFragment.newInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fuel Consumption: 23.5 Litres", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Notification method
        notifications.displayNotification(getBaseContext(), R.drawable.fuelfriend, R.drawable.blank_icon_small);

        mSupportMapFragment.getMapAsync(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // latLngArrayList = savedInstanceState.getParcelableArrayList("markerList");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // outState.putParcelableArrayList("markerList", latLngArrayList);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

        int id = item.getItemId();

        if (mSupportMapFragment.isAdded())
            sFm.beginTransaction().hide(mSupportMapFragment).commit();
        if (id == R.id.nav_map) {
            if (!mSupportMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, mSupportMapFragment).commit();
            else sFm.beginTransaction().show(mSupportMapFragment).commit();
        } else if (id == R.id.nav_manage) {
            sFm.beginTransaction().hide(mSupportMapFragment).commit();
        } else if (id == R.id.nav_about) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Fuel Friend is a UoW COMP477 project designed with the purpose of promoting energy conservation in our every day commutes. " +
                    "At this point in time, the objective is to achieve a relatively complete distance/fuel consumption" +
                    " application utilizing the Android Google Maps API." + "\n\n" + "Created by James Wong(1228302) \n\n Supervised by Mark Apperley")
                    .setTitle("About Fuel Friend");

            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
            if (!mSupportMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, mSupportMapFragment).commit();
            else sFm.beginTransaction().show(mSupportMapFragment).commit();
        } else if (id == R.id.nav_bike_marker) {
            setNewTransportMarker(TRANSPORT_BIKE);
            if (!mSupportMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, mSupportMapFragment).commit();
            else sFm.beginTransaction().show(mSupportMapFragment).commit();
        } else if (id == R.id.nav_walk_marker) {
            setNewTransportMarker(TRANSPORT_WALK);
            if (!mSupportMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, mSupportMapFragment).commit();
            else sFm.beginTransaction().show(mSupportMapFragment).commit();
        } else if (id == R.id.nav_car_marker) {
            setNewTransportMarker(TRANSPORT_CAR);
            if (!mSupportMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, mSupportMapFragment).commit();
            else sFm.beginTransaction().show(mSupportMapFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void setNewTransportMarker(String t) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng dest = new LatLng(location.getLatitude(), location.getLongitude());

        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
             addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Permission checks :\
            return;
        }

        //Then add marker to map
        UNLOCK_ON_POLYLINE_ADDED = false;
        dbHelper.insertMarker(dest, t, currentDateandTime, addresses.get(0).getLocality());

        LatLng origin = markerList.get(markerList.size() -1).getCoordinates();

        markerList = dbHelper.getAllMarkers();

        final AddNewMarkerThread r = new AddNewMarkerThread(t, origin, dest, addresses.get(0).getLocality());
        new Thread(r).start();
        drawPolyline(t, origin, dest);

    }

    public void drawPolyline(String t, LatLng a, LatLng b) {

        int polyline_color;
        switch (t) {
            case TRANSPORT_CAR:
                polyline_color = POLYLINE_CAR;
                break;
            case TRANSPORT_BIKE:
                polyline_color = POLYLINE_BIKE;
                break;
            default:
                polyline_color = POLYLINE_WALK;
        }

        String url = getDirectionsUrl(a, b);
        DownloadTask downloadTask = new DownloadTask(polyline_color, a);
        downloadTask.execute(url);
    }


    public void asyncDrawPolylines(final int i) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    handlePolyline(i);
                } catch (Exception ex) {
                    System.err.print("WTF HAPPENED?? ERRORED OUT");
                }
            }
        };
        new Thread(task, "ServiceThread").start();
    }


    public void handlePolyline(int i) {

        CustomMarker cm = markerList.get(i);
        LatLng cmOrigin = cm.getCoordinates();

        if (polyLinesList.containsKey(cmOrigin)) {
            mMap.addPolyline(polyLinesList.get(cmOrigin).getPolyLine());
        } else if (i > 0)
            drawPolyline(markerList.get(i - 1).getTransportMode(), markerList.get(i - 1).getCoordinates(), cm.getCoordinates());
    }


    public void refreshMap() throws InterruptedException {

        mMap.clear();
        for (int i = 0; i < markerList.size(); i++) {
            asyncDrawPolylines(i); // Draw poly lines

            String title_text = "TEST", snippet_text = "TEST SNIPPET";
            CustomMarker cm = markerList.get(i);
            if(i < markerList.size() - 1) {
                title_text = createTitleText(cm, markerList.get(i + 1).getGeoLocation()); // i + 1 representing the destination of the origin marker
                snippet_text = createSnippetText(cm.getDistance());
            }

            Marker marker = mMap.addMarker(new MarkerOptions() // Draw markers
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getIcon(cm.getTransportMode()))))
                    .position(new LatLng(cm.getCoordinates().latitude, cm.getCoordinates().longitude))
                    .title(title_text)
                    .snippet(snippet_text));

            googleMapMarkers.add(marker);
        }
    }

    //Prompt user to enable GPS
    public void enableGPS() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Toast.makeText(this, "This Application requires Location services to be enabled.", Toast.LENGTH_LONG).show();
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerList.get(1).getCoordinates(), 13));
        enableGPS();

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MainActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_json));

            if (!success) {
                Log.e(null, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(null, "Can't find style. Error: ", e);
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        markerList = dbHelper.getAllMarkers();

        try {
            refreshMap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmap(int drawableRes) {

        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location changed!", Toast.LENGTH_LONG).show();
    }

    public String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_LONG).show();
    }


    // Fetches data from url passed
    public class DownloadTask extends AsyncTask<String, Void, String> {

        int color;
        LatLng origin;

        public DownloadTask(int c, LatLng o) {
            color = c;
            origin = o;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(color, origin);

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        int poly_color;
        LatLng origin;

        public ParserTask(int c, LatLng o) {
            poly_color = c;
            origin = o;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(poly_color);

            }

            //Toast.makeText(getBaseContext(), "DISTANCE: " + distance + ", FUEL USAGE: " + 0.0500 * 1.8 + "KM/Litre", Toast.LENGTH_LONG).show();

            // Drawing polyline in the Google Map for the i-th route
            polyLinesList.put(origin, new CustomPolyline(lineOptions, distance));
            mMap.addPolyline(lineOptions);
            UNLOCK_ON_POLYLINE_ADDED = true;

        }
    }


    public static class NotificationUtils {

        public void displayNotification(Context context, int smallIcon, int actionIcon) {

            Intent action_walk = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_WALK);
            PendingIntent actionPending_walk = PendingIntent.getService(context, 0,
                    action_walk, 0);
            Intent action_car = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_CAR);
            PendingIntent actionPending_car = PendingIntent.getService(context, 0,
                    action_car, 0);
            Intent action_bike = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_BIKE);
            PendingIntent actionPending_bike = PendingIntent.getService(context, 0,
                    action_bike, 0);

            NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setOngoing(true)
                            .setAutoCancel(true)
                            .setSmallIcon(smallIcon)
                            .setContentTitle("FuelFriend tracking...")
                            .setContentText("Tap to create new transport path.")
                            .addAction(new NotificationCompat.Action(actionIcon,
                                    "Walk", actionPending_walk))
                            .addAction(new NotificationCompat.Action(actionIcon,
                                    "Car", actionPending_car))
                            .addAction(new NotificationCompat.Action(actionIcon,
                                    "Bike", actionPending_bike));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        }

        public static class NotificationActionService extends IntentService {
            public NotificationActionService() {
                super(NotificationActionService.class.getSimpleName());
            }

            @Override
            protected void onHandleIntent(Intent intent) {
                String action = intent.getAction();

                DBHelper db = new DBHelper(getBaseContext());

                switch(action) {
                    case ACTION_WALK:
                        System.out.println("I'm walking now.");
                        break;

                    case ACTION_CAR:
                        System.out.println("I'm in a car now.");
                        break;

                    case ACTION_BIKE:
                        System.out.println("I'm biking now.");

                        break;

                    default:
                        System.out.println("Error, unrecognized notification action");

                }

            }
        }
    }

    volatile private boolean UNLOCK_ON_POLYLINE_ADDED = false;

    public class AddNewMarkerThread implements Runnable {
        String transport, dest_geolocation;
        LatLng dest, origin;


        public AddNewMarkerThread(String t, LatLng a, LatLng b, String geo) {
            transport = t;
            origin = a;
            dest = b;
            dest_geolocation = geo;
        }

        public void run() {
            while (true) {
                if (UNLOCK_ON_POLYLINE_ADDED) {
                    if(polyLinesList.containsKey(origin)) {
                        dbHelper.updateEntryDistance(convertStringDistanceToDouble(polyLinesList.get(origin).getPolylineDistance()));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Marker originMarker = googleMapMarkers.get(googleMapMarkers.size() - 1);
                            CustomMarker cm = markerList.get(markerList.size() - 2);
                            Marker marker_1 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getIcon(cm.getTransportMode()))))
                                    .position(new LatLng(origin.latitude, origin.longitude))
                                    .title(createTitleText(cm, dest_geolocation))
                                    .snippet(createSnippetText(convertStringDistanceToDouble(polyLinesList.get(origin).getPolylineDistance()))));

                            originMarker.remove();
                            googleMapMarkers.remove(googleMapMarkers.size() - 1);
                            googleMapMarkers.add(marker_1);

                            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getIcon(transport))))
                                    .position(new LatLng(dest.latitude, dest.longitude))
                                    .title(dest_geolocation)
                                    .snippet("No information on this leg of travel.\nStatus: In Transit"));

                            googleMapMarkers.add(marker);
                        }
                    });
                    break;
                }
            }
        }
    }
}
