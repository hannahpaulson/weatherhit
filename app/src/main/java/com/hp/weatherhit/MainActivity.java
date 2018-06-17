package com.hp.weatherhit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    APIClient.APIInterface apiInterface;
    WeatherData weatherData;
    String unitType;
    TextView temp;
    TextView temp_hi;
    TextView temp_low;
    Button button;
    EditText editText;
    TextView day_label;
    TextView night_label;
    ImageView weather_image;
    TextView desc;
    Switch unit_switch;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp = findViewById(R.id.temp_text);
        temp_hi = findViewById(R.id.temp_hi_text);
        temp_low = findViewById(R.id.temp_low_text);
        editText = findViewById(R.id.user_input);
        button = findViewById(R.id.button);
        day_label = findViewById(R.id.temp_hi_label);
        night_label = findViewById(R.id.temp_low_label);
        weather_image = findViewById(R.id.weather_image);
        desc = findViewById(R.id.description);
        unit_switch = findViewById(R.id.unit_switch);
        day_label.setVisibility(View.GONE);
        night_label.setVisibility(View.GONE);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        unit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    unitType = "metric";
                    if (!editText.getText().toString().matches("")) {

                        setDegreeMeasurement(weatherData.getMain().getTemp(),
                                weatherData.getMain().getTempMax(),
                                weatherData.getMain().getTempMin());
                    }
                } else {
                    unitType = "imperial";
                    if (!editText.getText().toString().matches("")) {
                        setDegreeMeasurement(weatherData.getMain().getTemp(),
                                weatherData.getMain().getTempMax(),
                                weatherData.getMain().getTempMin());
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Pls Type a location", Toast.LENGTH_SHORT).show();
                } else {
                    requestForWeatherData(editText.getText().toString());
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void requestForWeatherData(final String location) {
        setUnitType();
        apiInterface = APIClient.getClient().create(APIClient.APIInterface.class);
        Call<WeatherData> call = apiInterface.getLocationWeather(location, "96d06b04ae1e61a7d850e288a8f16b2d");
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                displayWeatherData(response);
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                call.cancel();
                Log.e("RequestWeatherData", "Couldn't make the call");
            }
        });
    }

    private void getLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        unitType = sharedPreferences.getString("unit", null);
        if (unitType == null) {
            unitType = "metric";
        }
    }

    private void displayWeatherData(Response<WeatherData> response) {
        day_label.setVisibility(View.VISIBLE);
        night_label.setVisibility(View.VISIBLE);
        weatherData = response.body();

        setDegreeMeasurement(weatherData.getMain().getTemp(),
                weatherData.getMain().getTempMax(),
                weatherData.getMain().getTempMin());

        desc.setText(weatherData.getWeather().get(0).getDescription());
        setImage(weatherData.getWeather().get(0).getMain(), weather_image);
    }


    private void setImage(String weatherData, ImageView weather_image) {
        switch (weatherData.toLowerCase()) {
            case "clouds":
                weather_image.setImageResource(R.drawable.ic_wi_cloudy);
                break;
            case "clear":
                weather_image.setImageResource(R.drawable.ic_wi_day_sunny);
                break;
            default:
                weather_image.setImageResource(R.drawable.ic_wi_na);
                break;
        }
    }

    private void setDegreeMeasurement(Double tempValue, Double tempMax, Double tempMin) {
        if (unitType == "imperial") {
            temp.setText(kelvinToFahrenheit(tempValue) + "°F");
            temp_hi.setText(kelvinToFahrenheit(tempMax) + "°F");
            temp_low.setText(kelvinToFahrenheit(tempMin) + "°F");
        } else {
            temp.setText(kelvinToCelsius(tempValue) + "°C");
            temp_hi.setText(kelvinToCelsius(tempMax) + "°C");
            temp_low.setText(kelvinToCelsius(tempMin) + "°C");
        }
    }

    public static int kelvinToCelsius(double kelvinValue) {
        return (int) (kelvinValue - 273.15);
    }

    public static int kelvinToFahrenheit(double kelvinValue) {
        return (int) (((kelvinValue - 273) * 9 / 5) + 32);
    }
}

