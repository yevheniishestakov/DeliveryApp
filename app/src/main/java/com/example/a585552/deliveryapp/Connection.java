package com.example.a585552.deliveryapp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yevhenii on 16-Jul-17.
 */

public class Connection {

    private static final String API_URL = "https://maps.googleapis.com";

    private API api;



    Connection() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        final GsonConverterFactory converterFactory = GsonConverterFactory.create();

        api = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build()
                .create(API.class);
    }

    API getApi() {
        return api;
    }

}
