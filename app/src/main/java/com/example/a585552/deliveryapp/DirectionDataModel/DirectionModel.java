package com.example.a585552.deliveryapp.DirectionDataModel;

import java.util.List;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class DirectionModel {

    public String status;

    public List<GeocodedWaypoints> geocoded_waypoints;

    public List<Routes> routes;

    public String getStatus() {
        return status;
    }

    public List<GeocodedWaypoints> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public List<Routes> getRoutes() {
        return routes;
    }
}
