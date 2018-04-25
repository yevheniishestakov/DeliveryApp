package com.example.yevhenii.deliveryapp.DirectionDataModel;

import java.util.List;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class Routes {

    public String summary;

    public List<Legs> legs;

    public String getSummary() {
        return summary;
    }

    public List<Legs> getLegs() {
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
