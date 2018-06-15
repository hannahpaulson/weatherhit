package com.hp.weatherhit;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    APIClient.APIInterface apiInterface;
    WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.hannah);

        apiInterface = APIClient.getClient().create(APIClient.APIInterface.class);
        Call<WeatherData> call = apiInterface.getLocationWeather("London", "96d06b04ae1e61a7d850e288a8f16b2d");
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (weatherData != null) {
                    weatherData = response.body();
                    Log.i("onCreate", weatherData.getName());
                    textView.setText(weatherData.getName());
                }
                Log.i("onCreate", "" + response);
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                call.cancel();
                Log.e("Hannah", "Couldn't make the call");
            }
        });
    }
}

