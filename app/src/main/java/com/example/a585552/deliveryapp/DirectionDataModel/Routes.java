package com.example.a585552.deliveryapp.DirectionDataModel;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class Routes {

    public String summary;

    public Legs[] legs;

    public String getSummary() {
        return summary;
    }

    public Legs[] getLegs() {
        return legs;
    }

    public Polyline getOverview_polyline() {
        return overview_polyline;
    }

    public int[] getWaypoint_order() {
        return waypoint_order;
    }

    public Polyline overview_polyline;

    public int[] waypoint_order;


}
