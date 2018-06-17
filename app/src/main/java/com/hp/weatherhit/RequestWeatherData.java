package com.hp.weatherhit;

import android.support.annotation.Nullable;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestWeatherData {

    APIClient.APIInterface apiInterface = APIClient.getClient().create(APIClient.APIInterface.class);

    public void requestForWeatherData(final String location, String language, @Nullable final RetrofitCallback callback) {
        Call<WeatherData> call = apiInterface.getLocationWeather(location, "96d06b04ae1e61a7d850e288a8f16b2d", language);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response != null) {
                    WeatherData weatherData = response.body();
                    callback.onSuccess(weatherData);
                } else {
                    callback.onFailure(response.code(), response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                call.cancel();
                Log.e("RequestWeatherData", "Couldn't make the call");
            }
        });
    }
}
