package com.example.earth.fuelfriend;

/*
    Created by James Wong for a Waikato University New Zealand COMP477 Project
    Supervised by Mark Appereley

    1228302

*/


import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import static com.example.earth.fuelfriend.Constants.MAKE;
import static com.example.earth.fuelfriend.Constants.MODEL;
import static com.example.earth.fuelfriend.Constants.NOTIFICATION_ID;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_BIKE;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.Constants.TRANSPORT_WALK;
import static com.example.earth.fuelfriend.Constants.YEAR;
import static com.example.earth.fuelfriend.GeneralHelper.convertStringDistanceToDouble;
import static com.example.earth.fuelfriend.GeneralHelper.createSnippetText;
import static com.example.earth.fuelfriend.GeneralHelper.createTitleText;
import static com.example.earth.fuelfriend.GeneralHelper.displayAboutMessage;
import static com.example.earth.fuelfriend.GeneralHelper.downloadUrl;
import static com.example.earth.fuelfriend.GeneralHelper.getBitmap;
import static com.example.earth.fuelfriend.GeneralHelper.getDesignatedVehicle;
import static com.example.earth.fuelfriend.GeneralHelper.getTransportColor;
import static com.example.earth.fuelfriend.GeneralHelper.getTransportIcon;
import static com.example.earth.fuelfriend.GeneralHelper.isDesignated;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener,
        LocationListener {

    FragmentManager fragmentManager = getSupportFragmentManager();
    private GoogleMap mMap;
    private SupportMapFragment mSupportMapFragment;
    private SearchFragment mSearchFragment;
    private GarageFragment mGarageFragment;
    private LocationManager mLocationManager;
    private DBHelper mDatabaseHelper;
    private ArrayList<? extends CustomMarker> mMarkerInformation;
    private HashMap<LatLng, CustomPolyline> mPolylines;
    private ArrayList<Marker> googleMapMarkers;
    volatile private boolean UNLOCK_ON_POLYLINE_ADDED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        BroadcastReceiverNotificationActions mActionListner = new BroadcastReceiverNotificationActions();
        // dynamically register an instance of this class with Context.registerReceiver() in the manifest
        // as you cannot use the manifest for non-static inner class BroadcastReceiver classes.
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_CAR");
        filter.addAction("ACTION_WALK");
        filter.addAction("ACTION_BIKE");
        this.registerReceiver(new BroadcastReceiverNotificationActions(), filter, null, null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        googleMapMarkers = new ArrayList<>();
        mDatabaseHelper = new DBHelper(this);
        mSupportMapFragment = SupportMapFragment.newInstance();
        mSupportMapFragment.getMapAsync(this);
        mSearchFragment = new SearchFragment();
        mGarageFragment = new GarageFragment();

        mMarkerInformation = mDatabaseHelper.getAllMarkers();
        mPolylines = new HashMap<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Notification method
        mActionListner.createNotificationIntents(R.drawable.ic_gas_petrol_24dp_white, R.drawable.blank_icon_small, getBaseContext());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // latLngArrayList = savedInstanceState.getParcelableArrayList("mMarkerInformation");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
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

        GeneralHelper.hideKeyboard(this);
        switch (item.getItemId()) {
            case R.id.nav_map:
                setTitle("Travel History Map");
                fragmentManager.beginTransaction().replace(R.id.content_frame, mSupportMapFragment).addToBackStack("map").commit();
                break;

            case R.id.statistics:
                setTitle("My Statistics");
                break;

            case R.id.nav_manage:
                setTitle("Personal Garage");
                fragmentManager.beginTransaction().replace(R.id.content_frame, mGarageFragment).addToBackStack("garage").commit();
                break;

            case R.id.nav_add:
                setTitle("Vehicle Database");
                if (mSearchFragment.getArguments() == null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("searchFragment", "searchFragment");
                    mSearchFragment.setArguments(bundle);
                }

                fragmentManager.beginTransaction().replace(R.id.content_frame, mSearchFragment).addToBackStack("search").commit();
                break;

            case R.id.nav_about:
                displayAboutMessage(this);
                break;

            case R.id.nav_walk_marker:
                setNewTransportMarker(TRANSPORT_WALK);
                break;

            case R.id.nav_car_marker:
                setNewTransportMarker(TRANSPORT_CAR);
                break;

            case R.id.nav_bike_marker:
                setNewTransportMarker(TRANSPORT_BIKE);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 0) {
            boolean ples = fragmentManager.popBackStackImmediate("map", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //fragmentManager.beginTransaction().replace(R.id.map, getSupportFragmentManager().findFragmentByTag("map")).commit();
            System.out.println("Got last fragment = " + " " + getSupportFragmentManager().getBackStackEntryCount());

        } else {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
        super.onBackPressed();
    }

    protected void buildGoogleApiClient() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void setNewTransportMarker(String transport) {

        if (!isDesignated(getBaseContext())) {
/*            AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
            builder.setMessage("Cannot find designated vehicle information, please designate a vehicle you're going to be driving today.")
                    .setTitle("Designate a Driven Vehicle");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.getMenu().getItem(1).setChecked(true);
                    onNavigationItemSelected(navigationView.getMenu().getItem(1));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();*/

            Toast.makeText(this, "Please designate a vehicle from your garage first.", Toast.LENGTH_LONG).show(); //Placeholder message until dialog works.
            return;
        }

        String currentVehicle = getDesignatedVehicle(getBaseContext());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Location location = getLastKnownLocation();
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
        mDatabaseHelper.insertMarker(dest, transport, currentDateandTime, addresses.get(0).getLocality(), currentVehicle);

        LatLng origin = mMarkerInformation.get(mMarkerInformation.size() - 1).getCoordinates();

        final AddNewMarkerThread r = new AddNewMarkerThread(transport, origin, dest, addresses.get(0).getLocality(), currentVehicle);
        new Thread(r).start();
        drawPolyline(mMarkerInformation.get(mMarkerInformation.size() - 1).getTransportMode(), origin, dest);

        mMarkerInformation = mDatabaseHelper.getAllMarkers();

    }

    public void drawPolyline(String t, LatLng a, LatLng b) {

        String url = getDirectionsUrl(a, b);
        DownloadTask downloadTask = new DownloadTask(getTransportColor(t, getBaseContext()), a);
        downloadTask.execute(url);
    }

    public void asyncDrawPolylines(final int i) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    handlePolyline(i);
                } catch (Exception ex) {
                    System.err.print("WTF HAPPENED?? ERRORED OUT drawing polylines");
                }
            }
        };
        new Thread(task, "ServiceThread").start();
    }

    public void handlePolyline(int i) {

        CustomMarker cm = mMarkerInformation.get(i);
        LatLng cmOrigin = cm.getCoordinates();

        if (mPolylines.containsKey(cmOrigin)) {
            mMap.addPolyline(mPolylines.get(cmOrigin).getPolyLine());
        } else if (i > 0)
            drawPolyline(mMarkerInformation.get(i - 1).getTransportMode(), mMarkerInformation.get(i - 1).getCoordinates(), cm.getCoordinates());
    }

    public void refreshMap() throws InterruptedException {

        mMap.clear();
        for (int i = 0; i < mMarkerInformation.size(); i++) {
            asyncDrawPolylines(i); // Draw poly lines

            String title_text = mMarkerInformation.get(mMarkerInformation.size() - 1).getGeoLocation() + "|" + mMarkerInformation.get(mMarkerInformation.size() - 1).getTransportMode(), snippet_text = "Getting\ndata...";
            CustomMarker cm = mMarkerInformation.get(i);
            if (i < mMarkerInformation.size() - 1) {
                title_text = createTitleText(cm, mMarkerInformation.get(i + 1).getGeoLocation()); // i + 1 representing the destination of the origin marker
                snippet_text = createSnippetText(cm.getDistance(), cm.getVehicle());
            }

            Marker marker = mMap.addMarker(new MarkerOptions() // Draw markers
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getTransportIcon(cm.getTransportMode()), getBaseContext())))
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

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            //System.out.println("last known location, provider: %s, location: %s" + provider + l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //System.out.println("found best last known location: %s" + l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarkerInformation.get(1).getCoordinates(), 13));
        enableGPS();

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MainActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                try {
                    System.out.println(marker.getSnippet());
                    String[] vehicle = marker.getSnippet().substring(marker.getSnippet().indexOf("|"), marker.getSnippet().length()).split(",");
                    String vehicleTitle = vehicle[YEAR] + " " + vehicle[MAKE] + " " + vehicle[MODEL];
                    Toast.makeText(getApplicationContext(), vehicleTitle, Toast.LENGTH_LONG).show();
                } catch (Exception ignore) {
                }

            }
        });

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

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

        mMarkerInformation = mDatabaseHelper.getAllMarkers();

        try {
            refreshMap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

            // Drawing polyline in the Google Map for the i-th route
            mPolylines.put(origin, new CustomPolyline(lineOptions, distance));
            mMap.addPolyline(lineOptions);
            UNLOCK_ON_POLYLINE_ADDED = true;

        }
    }

    public class BroadcastReceiverNotificationActions extends BroadcastReceiver {

        public void createNotificationIntents(int smallIcon, int actionIcon, Context context) {
            Intent bike_intent = new Intent();
            bike_intent.setAction(ACTION_BIKE);
            PendingIntent pendingIntentBike = PendingIntent.getBroadcast(context, 0, bike_intent, 0);

            Intent car_intent = new Intent();
            car_intent.setAction(ACTION_CAR);
            PendingIntent pendingIntentCar = PendingIntent.getBroadcast(context, 0, car_intent, 0);

            Intent walk_intent = new Intent();
            walk_intent.setAction(ACTION_WALK);
            PendingIntent pendingIntentWalk = PendingIntent.getBroadcast(context, 0, walk_intent, 0);

            NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setOngoing(true)
                            .setAutoCancel(true)
                            .setSmallIcon(smallIcon)
                            .setContentTitle("FuelFriend tracking...")
                            .setContentText("Tap to create new transport path.")
                            .addAction(new NotificationCompat.Action(actionIcon,
                                    "Walk", pendingIntentWalk))
                            .addAction(new NotificationCompat.Action(actionIcon,
                                    "Car", pendingIntentCar))
                            .addAction(new NotificationCompat.Action(actionIcon,
                                    "Bike", pendingIntentBike));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_BIKE.equals(action)) {
                setNewTransportMarker(TRANSPORT_BIKE);
            } else if (ACTION_CAR.equals(action)) {
                setNewTransportMarker(TRANSPORT_CAR);
            } else if (ACTION_WALK.equals(action)) {
                setNewTransportMarker(TRANSPORT_WALK);
            }
        }
    }

    public class AddNewMarkerThread implements Runnable {
        String transport, dest_geolocation, vehicle;
        LatLng dest, origin;


        public AddNewMarkerThread(String t, LatLng a, LatLng b, String geo, String v) {
            transport = t;
            origin = a;
            dest = b;
            dest_geolocation = geo;
            vehicle = v;
        }

        public void run() {
            while (true) {
                if (UNLOCK_ON_POLYLINE_ADDED) {
                    if (mPolylines.containsKey(origin)) {
                        mDatabaseHelper.updateEntryDistance(convertStringDistanceToDouble(mPolylines.get(origin).getPolylineDistance()));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Updating the 2nd to last marker with new information since new marker added.
                            Marker originMarker = googleMapMarkers.get(googleMapMarkers.size() - 1);
                            CustomMarker cm = mMarkerInformation.get(mMarkerInformation.size() - 2);
                            String distance = mPolylines.get(origin).getPolylineDistance();

                            Marker marker_1 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getTransportIcon(cm.getTransportMode()), getBaseContext())))
                                    .position(new LatLng(origin.latitude, origin.longitude))
                                    .title(createTitleText(cm, dest_geolocation))
                                    .snippet(createSnippetText(convertStringDistanceToDouble(distance), vehicle)));

                            originMarker.remove();
                            googleMapMarkers.remove(googleMapMarkers.size() - 1);
                            googleMapMarkers.add(marker_1);

                            // New marker with no distance info as its waiting for another marker to be placed.
                            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getTransportIcon(transport), getBaseContext())))
                                    .position(new LatLng(dest.latitude, dest.longitude))
                                    .title(createTitleText(mMarkerInformation.get(mMarkerInformation.size() - 1), dest_geolocation))
                                    .snippet("Getting\ndata..."));

                            googleMapMarkers.add(marker);
                        }
                    });
                    break;
                }
            }
        }
    }

}
