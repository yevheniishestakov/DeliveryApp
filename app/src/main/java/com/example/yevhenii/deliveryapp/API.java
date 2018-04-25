package com.example.yevhenii.deliveryapp;

import com.example.yevhenii.deliveryapp.DirectionDataModel.DirectionModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public interface API {

    public boolean optimized = true;

    @GET("/maps/api/directions/json?")
    Call<DirectionModel> directions(
            @Query(value = "waypoints")String waypoints,
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key);
}
