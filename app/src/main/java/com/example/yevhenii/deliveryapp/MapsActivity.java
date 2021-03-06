package com.example.yevhenii.deliveryapp;

import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.yevhenii.deliveryapp.DirectionDataModel.DirectionModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.android.gms.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, android.app.LoaderManager.LoaderCallbacks<Cursor>{

    private Connection connection = new Connection();
    private String log_tag = "MapsActivity";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Location dest_location = new Location("");
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private PolylineDecoder polyLineDecoder;
    private String waypoints = "";
    private static final int DELIVERY_LOADER = 0;
    private String optimize_true = "optimize:true|";
    private List<LatLng> coordinates_list = new ArrayList<LatLng>();



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

        getLoaderManager().initLoader(DELIVERY_LOADER, null, this);

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



            Log.i("Current lat: ", String.valueOf(mCurrentLocation.getLatitude()));
            Log.i("Current lon: ", String.valueOf(mCurrentLocation.getLongitude()));

            Location destination = selectDestinationAndWaypoints(mCurrentLocation,coordinates_list);
            Log.v(log_tag,"Dest coodingates: " + destination.getLatitude()+","+destination.getLongitude());

            Log.v(log_tag, "Waypoints: " + waypoints);
            //Test destination coordinates -  53.121771, 18.000568

            final PolylineOptions polyOptions = new PolylineOptions();




            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .zoom(20)
                    .tilt(30)
                    .build();

            Log.v(log_tag, waypoints.toString());

            API api = connection.getApi();
            api.directions(optimize_true+waypoints, CoordinatesToString(mCurrentLocation), CoordinatesToString(destination), "AIzaSyAtUp6uonJ_3fHPXssi7VeNngCeVqqC0qo").enqueue(new Callback<DirectionModel>() {
                @Override
                public void onResponse(Call<DirectionModel> call, Response<DirectionModel> response) {

                    polyLineDecoder = new PolylineDecoder();
                    Log.v("Legs: ", String.valueOf(response.body().routes.get(0).legs.size()));


                    for (int i = 0; i < response.body().routes.get(0).legs.size(); i++) {

                        mMap.addMarker(new MarkerOptions().position(new LatLng(response.body().routes.get(0)
                        .legs.get(i).end_location.getLat(),response.body().routes.get(0)
                                .legs.get(i).end_location.getLng())));

                        for (int j = 0; j < response.body().routes.get(0).legs.get(i).steps.size(); j++) {

                            polyLineDecoder.decodePolyLine(response.body().routes.get(0).legs.get(i).
                                    steps.get(j).getPolyline().getPoints());
                        }

                    }

                    for (LatLng  point : polyLineDecoder.decoded){

                        polyOptions.add(point);


                    }

                    polyOptions.width(15).color(Color.BLUE);
                    mMap.addPolyline(polyOptions);

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                @Override
                public void onFailure(Call<DirectionModel> call, Throwable t) {


                    t.printStackTrace();
                }
            });


        }
        else{
            Log.v(log_tag, "No current location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("OnCOnnectionFailed ", "con failed");
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                DeliveryDBContract.DeliveryItemEntry._ID,
                DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LAT,
                DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LON };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                DeliveryDBContract.DeliveryItemEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

        while (data.moveToNext()){

            double lat = data.getDouble(data.getColumnIndex(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LAT));
            double lon = data.getDouble(data.getColumnIndex(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LON));



            dest_location.setLatitude(lat);
            dest_location.setLongitude(lon);

            Log.v(log_tag,"dest lat" + lat);
            Log.v(log_tag,"dest lon" + lon);

            this.coordinates_list.add(new LatLng(lat,lon));

        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    private String CoordinatesToString (Location location){

        String coordinatesString = String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());

        return coordinatesString;
    }

    private String CoordinatestoString (LatLng location){

        String coordinateString = String.valueOf(location.latitude)+","+String.valueOf(location.longitude);
        return coordinateString;
    }

    /*
        Method for selecting the destination, which is the most remote point from current location.
        Method iterates throuth the list of coordinates checking if it is the most remote one. If it is - these coordinates are chosen
        as a destination. If not - converted to String and assigned to "waypoints" variable. 
    */

    private Location selectDestinationAndWaypoints (Location origin, List<LatLng> coordingatesList){

        float max = 0;
        Location result = new Location("");
        Location destination = new Location("");

        for (LatLng i: coordingatesList){

            destination.setLatitude(i.latitude);
            destination.setLongitude(i.longitude);

            if (origin.distanceTo(destination) > max){

                result.setLatitude(i.latitude);
                result.setLongitude(i.longitude);
            }
            else {
                waypoints= waypoints+CoordinatestoString(i)+"|";

            }

            max = origin.distanceTo(destination);
        }
        return result;
    }

}
