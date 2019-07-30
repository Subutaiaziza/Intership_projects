package com.example.taxiapp.model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static String BASE_URL = "http://openfreecabs.org/nearest/";

    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static TaxiApi getTaxiApi() {
        return taxiApi;
    }
    static TaxiApi taxiApi = retrofit.create(TaxiApi.class);


}
