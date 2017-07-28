package com.example.a585552.deliveryapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.a585552.deliveryapp.DirectionDataModel.DirectionModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.android.gms.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Connection connection = new Connection();
    private String log_tag = "MapsActivity";
    private float distanceToDest;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LatLng MyPosition;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {

        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null){

            /* Debug logs*/

            Log.i("Result lat: ", String.valueOf(mCurrentLocation.getLatitude()));
            Log.i("Result lon: ", String.valueOf(mCurrentLocation.getLongitude()));


            //Test destination coordinates -  53.121771, 18.000568

            Location dest = new Location(LocationManager.GPS_PROVIDER);
            dest.setLatitude(53.121771);
            dest.setLongitude(18.000568);
            distanceToDest = mCurrentLocation.distanceTo(dest);

            MyPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(MyPosition).title("My position, Distance to dest: " + distanceToDest));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(MyPosition));



            Polyline line;

            final PolylineOptions polyOptions = new PolylineOptions();




            /*new PolylineOptions().
                    add(new LatLng(53.1333616,17.9678931), new LatLng(53.1336349, 17.9684481))
                    .add(new LatLng(53.1336349,17.9684481), new LatLng(53.1329103, 17.9704364))
                    .add(new LatLng(53.1329103,17.9704364), new LatLng(53.132731,17.9702151))
                    .width(15)
                    .color(Color.BLUE)*/


            API api = connection.getApi();
            api.directions("53.1333616,17.9678931", "53.132731,17.9702151", "AIzaSyAtUp6uonJ_3fHPXssi7VeNngCeVqqC0qo").enqueue(new Callback<DirectionModel>() {
                @Override
                public void onResponse(Call<DirectionModel> call, Response<DirectionModel> response) {

                    for (int i = 0; i < response.body().routes[0].legs[0].steps.length; i++) {

                        polyOptions.add(
                                new LatLng(response.body().routes[0].legs[0].steps[i].start_location.getLat(),
                                        response.body().routes[0].legs[0].steps[i].start_location.getLng()),
                                new LatLng(response.body().routes[0].legs[0].steps[i].end_location.getLat(),
                                        response.body().routes[0].legs[0].steps[i].end_location.getLng())

                                        );

                    }
                    polyOptions.width(15).color(Color.CYAN);
                    mMap.addPolyline(polyOptions);

                    Log.v("Retrofit: ", response.body().routes[0].legs[0].steps[0].duration.getValue());

                }

                @Override
                public void onFailure(Call<DirectionModel> call, Throwable t) {

                    t.printStackTrace();
                }
            });


        }
        else{
            Log.v("No current location", "No current location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("OnCOnnectionFailed ", "con failed");
    }


}
