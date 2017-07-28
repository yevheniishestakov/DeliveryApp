package com.example.a585552.deliveryapp.DirectionDataModel;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class GeocodedWaypoints {

    public GeocodedWaypoints[] geocoded_waypoints;

    public String geocoder_status;

    public GeocodedWaypoints[] getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public String getGeocoder_status() {
        return geocoder_status;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String[] getTypes() {
        return types;
    }

    public String place_id;
    public String[] types;
}
