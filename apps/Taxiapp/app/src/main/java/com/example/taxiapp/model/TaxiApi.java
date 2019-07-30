package com.example.taxiapp.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TaxiApi {

    @GET("nearest/{lat}/{lon}")

    Call<Main> getDetails(@Path("lat") double latitute,
                          @Path("lon") double lontitute);
}
