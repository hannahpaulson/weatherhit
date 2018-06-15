package com.hp.weatherhit;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        final TextView temp = findViewById(R.id.temp);
        final TextView locationTextView = findViewById(R.id.location);
        final TextView temp_hi = findViewById(R.id.temp_hi);
        final TextView temp_low = findViewById(R.id.temp_low);


        final EditText editText = findViewById(R.id.user_input);
        final Button button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Hannah", editText.getText().toString());
                if (editText.getText().toString() != "") {
                    requestForWeatherData(temp, locationTextView, temp_hi, temp_low, editText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Pls Type a location", Toast.LENGTH_SHORT).show();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    private void requestForWeatherData(final TextView temp, final TextView locationTextView, final TextView temp_hi, final TextView temp_low, final String location) {
        apiInterface = APIClient.getClient().create(APIClient.APIInterface.class);
        Call<WeatherData> call = apiInterface.getLocationWeather(location, "96d06b04ae1e61a7d850e288a8f16b2d", "imperial");
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                weatherData = response.body();
                temp.setText(weatherData.getMain().getTemp().toString());
                locationTextView.setText(weatherData.getName());
                temp_hi.setText(weatherData.getMain().getTempMax().toString());
                temp_low.setText(weatherData.getMain().getTempMin().toString());
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                call.cancel();
                Log.e("Hannah", "Couldn't make the call");
            }
        });
    }
}

