package com.example.runkeeper;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runkeeper.model.DatabaseHelper;
import com.example.runkeeper.model.DistanceCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private final int REQUEST_CODE = 101;
    FusedLocationProviderClient client;
    double longtitute;
    double latitute;
    ImageButton btnStart;
    TextView kilom;
    Chronometer chronometer;
    long pauseOffset;
    boolean isPlaying = false, isStart;

    LocationManager locationManager;
    Location firtsLocation, lastLocation, currentLocation;
    private double kilometer = 0;
    String for_km = "";

    DatabaseHelper myDB;

    //final results variables
    String duration = "";

    List<LatLng> coords = new ArrayList<LatLng>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnStart = findViewById(R.id.btn);
        kilom = findViewById(R.id.km_id);
        myDB = new DatabaseHelper(this);

        Log.d(TAG, "onCreate:  started");
        client = LocationServices.getFusedLocationProviderClient(this);
        chronometer = findViewById(R.id.chron);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cronChanged) {
                chronometer = cronChanged;
            }
        });

        fetchLocation();

        //button for starting this app
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chronometer(); //starts timer
                getLocation(); // starts LocationChangeEvent
            }
        });

        btnStart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Save");

                builder.setMessage("Do you want to save? ");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(kilometer != 0)
                        {
                            duration = (String) chronometer.getText();
                            Toast.makeText(getApplicationContext(), "here km: "+value.format(kilometer)+" "+duration, Toast.LENGTH_SHORT).show();
                            saveDataOnDB(value.format(kilometer), duration);
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "There wasnt passed any distance "+ kilometer, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset();
                    }
                });

                AlertDialog alert = builder.create();
                        alert.show();
                return false;
            }
        });
    }

    private void reset() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        kilometer = 0;
        coords.clear();
        pauseMethod();
    }

    // saving data to DB
    private void saveDataOnDB(String km, String dur_time) {
        myDB = new DatabaseHelper(this);
        myDB.getWritableDatabase();
        String date = getCurrentDate();

        boolean result = myDB.addData(km, dur_time, date);
        if(result)
        {
            Toast.makeText(MapsActivity.this, "Successfully Inserted", Toast.LENGTH_SHORT).show();
        }else
            {
                Toast.makeText(MapsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("MMM, dd HH:mm");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }

    ///Chronometer methods
    private void Chronometer() {

        if(isPlaying)
        {
            btnStart.setImageResource(R.drawable.ic_media_pause_light);
            pauseMethod();
        }else
            {
                btnStart.setImageResource(R.drawable.ic_media_play_light);
                startMethod();
            }
        isPlaying = !isPlaying;
    }
    private void pauseMethod() {
        if(isStart)
        {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            isStart = false;
        }
    }
    private void startMethod() {
        if(!isStart)
        {
            chronometer.start();
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            isStart = true;
        }
    }

    //method for getting locations
    private void getLocation() {

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    // for getting current location
    private void fetchLocation() {

        Log.d(TAG, "fetchLocation:  fetch started");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    latitute = location.getLatitude();
                    longtitute = location.getLongitude();

                    onLocationChanged(location);
                    coords.add(new LatLng(latitute, longtitute));

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
                Log.d(TAG, "onSuccess:  " + coords.size());
                Toast.makeText(MapsActivity.this, "here size of coord: " + coords.size(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        coords.clear();
        Log.d(TAG, "onMapReady: started");
        LatLng latLng = new LatLng(latitute,longtitute);
        MarkerOptions marker = new MarkerOptions().position(latLng).title(currentLocation.getLatitude()+" "+currentLocation.getLongitude());
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }

    // formatter of km
    DecimalFormat value = new DecimalFormat("#.##");


    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;
        latitute = location.getLatitude();
        longtitute = location.getLongitude();

        DistanceCalculator discal = new DistanceCalculator();

        if(!coords.contains(location))
        {
            coords.add(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        drawPolyLine(coords);

        for(int i = 1;i<coords.size();i++)
        {
            double oldLat = coords.get(i-1).latitude;
            double oldLon = coords.get(i-1).longitude;
            double newLat = coords.get(i).latitude;
            double newLon = coords.get(i).longitude;

            kilometer += discal.getDistanceFromLatLonInKm(oldLat, oldLon, newLat, newLon);

            coords.remove(oldLat);
            coords.remove(oldLon);

            for_km = value.format(kilometer);
            kilom.setText(for_km+" км");
        }
    }

    //draw lines
    private void drawPolyLine(List<LatLng> coords) {
        if(coords.size() > 1)
        {
            ///Toast.makeText(getApplicationContext(), "size of cord: "+coords.size(), Toast.LENGTH_SHORT).show();
            Polyline line = mMap.addPolyline(new PolylineOptions().width(3).color(Color.BLUE));
            line.setPoints(coords);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(MapsActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
}
