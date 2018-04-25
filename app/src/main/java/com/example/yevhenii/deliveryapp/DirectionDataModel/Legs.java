package com.example.yevhenii.deliveryapp.DirectionDataModel;

import java.sql.Time;
import java.util.List;

/**
 * Created by Yevhenii on 26-Jul-17.
 */

public class Legs {

    public Distance distance;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getDuration_in_traffic() {
        return duration_in_traffic;
    }

    public Time getArrival_time() {
        return arrival_time;
    }

    public Location getStart_location() {
        return start_location;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public String getStart_address() {
        return start_address;
    }

    public String getEnd_address() {
        return end_address;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public Duration duration;

    public int duration_in_traffic;

    public Time arrival_time;

    public Location start_location;

    public Location end_location;

    public String start_address;

    public String end_address;

    public List<Steps> steps;


}
