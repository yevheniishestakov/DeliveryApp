package com.example.a585552.deliveryapp;

import com.example.a585552.deliveryapp.DirectionDataModel.DirectionModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public interface API {

    @GET("/maps/api/directions/json?waypoints=optimize:true|")
    Call<DirectionModel> directions(

                                    @Query("origin") String origin,
                                    @Query("waypoints") List<String> waypoints,
                                    @Query("destination") String destination,
                                    @Query("key") String api_key);
}
