package com.example.yevhenii.deliveryapp.DirectionDataModel;

import java.util.List;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class GeocodedWaypoints {

    public List<GeocodedWaypoints> geocoded_waypoints;

    public String geocoder_status;

    public List<GeocodedWaypoints> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public String getGeocoder_status() {
        return geocoder_status;
    }

    public String getPlace_id() {
        return place_id;
    }

    public List<String> getTypes() {
        return types;
    }

    public String place_id;
    public List<String> types;
}
