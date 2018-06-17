package com.hp.weatherhit;

public interface RetrofitCallback {
    void onSuccess(WeatherData weatherData);
    void onFailure(int errorCode, String error);
}
