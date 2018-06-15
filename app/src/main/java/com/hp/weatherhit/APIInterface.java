package com.hp.weatherhit;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("/data/2.5/weather?q=London,UK&appid=96d06b04ae1e61a7d850e288a8f16b2d")
    Call<WeatherData> getLocationWeather();
}
