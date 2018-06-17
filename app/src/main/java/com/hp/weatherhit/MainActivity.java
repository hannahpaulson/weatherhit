package com.hp.weatherhit;

import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hp.weatherhit.R.drawable.ic_wi_cloudy;
import static java.time.LocalDateTime.*;

public class MainActivity extends AppCompatActivity {
    APIClient.APIInterface apiInterface;
    WeatherData weatherData;
    String unitType = "imperial";

    TextView temp;
    TextView temp_hi;
    TextView temp_low;
    Button button;
    EditText editText;
    TextView day_label;
    TextView night_label;
    ImageView weather_image;
    TextView desc;


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
        day_label.setVisibility(View.GONE);
        night_label.setVisibility(View.GONE);

        Switch unit_switch = findViewById(R.id.unit_switch);

        unit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    unitType = "metric";
                    if (!editText.getText().toString().matches("")) {
                        requestForWeatherData(editText.getText().toString());
                    }
                } else {
                    unitType = "imperial";
                    if (!editText.getText().toString().matches("")) {
                        requestForWeatherData(editText.getText().toString());
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

    private void requestForWeatherData(final String location) {
        apiInterface = APIClient.getClient().create(APIClient.APIInterface.class);
        Call<WeatherData> call = apiInterface.getLocationWeather(location, "96d06b04ae1e61a7d850e288a8f16b2d", unitType);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {

                day_label.setVisibility(View.VISIBLE);
                night_label.setVisibility(View.VISIBLE);
                weatherData = response.body();
                String degreeType = getDegreeMeasurement();
                temp.setText(trimDecimal(weatherData.getMain().getTemp().toString()) + degreeType);
                temp_hi.setText(trimDecimal(weatherData.getMain().getTempMax().toString()) + degreeType);
                temp_low.setText(trimDecimal(weatherData.getMain().getTempMin().toString()) + degreeType);
                desc.setText(weatherData.getWeather().get(0).getDescription());
                setImage(weatherData.getWeather().get(0).getMain(), weather_image);
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                call.cancel();
                Log.e("RequestWeatherData", "Couldn't make the call");
            }
        });
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

    private String getDegreeMeasurement() {
        if (unitType == "imperial") {
            return "°F";
        } else {
            return "°C";
        }
    }

    private String trimDecimal(String value) {
        if (value.length() == 5) {
            return value.substring(0, value.length() - 3);
        } else {
            return value.substring(0, value.length() - 2);
        }
    }
}

