package com.example.taxiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxiapp.model.Company;
import com.example.taxiapp.model.Contact;
import com.example.taxiapp.model.Driver;
import com.example.taxiapp.model.Main;
import com.example.taxiapp.model.MyCustomAdapterForItems;
import com.example.taxiapp.model.MyItem;
import com.example.taxiapp.model.TaxiApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    FusedLocationProviderClient client;
    Context context = MainActivity.this;
    private final static int REQUEST_CODE = 101;
    private ClusterManager<MyItem> mClusterManager;
    private ArrayList<Contact> contactList = new ArrayList<>();
    public static MyItem clickedItem;

    private String TAG = "MainActivity";
    public double longtitute = 74.603691;
    public double latitute = 42.875933;
    TextView text, coord;
    Location cur_location;
    TaxiApi taxiApi;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openfreecabs.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        taxiApi = retrofit.create(TaxiApi.class);

        client = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation() {
        Log.d(TAG, "fetchLocation:  fetch started");

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    latitute = location.getLatitude();
                    longtitute = location.getLongitude();

                    cur_location = location;

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map_frag);
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpClusterManager(googleMap);

        LatLng latLng = new LatLng(latitute, longtitute);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        googleMap.addMarker(markerOptions);

    }
    private void setUpClusterManager(GoogleMap map) {
        mClusterManager = new ClusterManager<>(this, googleMap);
        map.setOnCameraIdleListener(mClusterManager);

        getData();

        MyCustomAdapterForItems adapter = new MyCustomAdapterForItems(this, mClusterManager);
        googleMap.setInfoWindowAdapter(adapter);
    }

    private void getData() {

        Call<Main>call = taxiApi.getDetails(latitute, longtitute);
        call.enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {
                Log.d(TAG, "main:  successs");

                Main main = response.body();
                String str = "";

                Toast.makeText(MainActivity.this, "size of com: "+ main.getCompanies().size(), Toast.LENGTH_SHORT).show();

                for(int c = 0;c < main.getCompanies().size();c++)
                {
                    Company company = main.getCompanies().get(c);
                    for(int d = 0;d < company.getDrivers().size();d++)
                    {
                        Driver driver = main.getCompanies().get(c).getDrivers().get(d);
                        MyItem offsetItem = new MyItem(new LatLng(driver.getLat(),driver.getLon()), company.getName(), company.getContacts().get(0).getContact(),company.getContacts().get(1).getContact());
                        mClusterManager.addItem(offsetItem);
                        googleMap.setOnMarkerClickListener(mClusterManager);
                    }
                }
            }
            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                Log.d(TAG, "onResponse: fails: " + t.getMessage());
            }
        });

    }
}
