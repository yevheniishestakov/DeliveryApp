package com.example.a585552.deliveryapp.DirectionDataModel;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class DirectionModel {

    public String status;

    public GeocodedWaypoints[] geocoded_waypoints;

    public Routes[] routes;

    public String getStatus() {
        return status;
    }

    public GeocodedWaypoints[] getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public Routes[] getRoutes() {
        return routes;
    }
}
