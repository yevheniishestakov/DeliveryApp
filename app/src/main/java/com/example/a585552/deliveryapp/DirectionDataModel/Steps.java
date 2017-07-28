package com.example.a585552.deliveryapp.DirectionDataModel;

/**
 * Created by Yevhenii on 26-Jul-17.
 */

public class Steps {

    public Distance distance;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public Location getStart_location() {
        return start_location;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public Duration duration;

    public Location end_location;

    public Location start_location;

    public Polyline polyline;
}
