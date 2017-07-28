package com.example.a585552.deliveryapp;

import com.example.a585552.deliveryapp.DirectionDataModel.DirectionModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public interface API {

    @GET("/maps/api/directions/json")
    Call<DirectionModel> directions(@Query("origin") String origin,
                                    @Query("destination") String destination,
                                    @Query("key") String api_key);
}
