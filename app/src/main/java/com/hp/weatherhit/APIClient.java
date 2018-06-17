package com.hp.weatherhit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class APIClient {
    private static Retrofit retrofit = null;
    private static final String URL = "http://api.openweathermap.org/";

    public interface APIInterface {
        @GET("/data/2.5//weather")
        Call<WeatherData> getLocationWeather(
                @Query("q") String location,
                @Query("appId") String appId,
                @Query("lang") String lang);
    }

    static Retrofit getClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit;
    }

}