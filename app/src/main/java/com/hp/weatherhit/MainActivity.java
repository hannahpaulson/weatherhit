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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    APIClient.APIInterface apiInterface;
    WeatherData weatherData;
    Boolean isFreedomUnits = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView temp = findViewById(R.id.temp_text);
        final TextView temp_hi = findViewById(R.id.temp_hi_text);
        final TextView temp_low = findViewById(R.id.temp_low_text);
        final EditText editText = findViewById(R.id.user_input);
        final Button button = findViewById(R.id.button);
        final TextView day_label = findViewById(R.id.temp_hi_label);
        final TextView night_label = findViewById(R.id.temp_low_label);
        day_label.setVisibility(View.GONE);
        night_label.setVisibility(View.GONE);

        Switch unit_switch = findViewById(R.id.unit_switch);

        unit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (editText.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Pls Type a location", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        isFreedomUnits = false;
                        requestForWeatherData(temp, temp_hi, temp_low, editText.getText().toString(), day_label, night_label, "metric");
                    } else {
                        isFreedomUnits = true;
                        requestForWeatherData(temp, temp_hi, temp_low, editText.getText().toString(), day_label, night_label, "imperial");
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
                    if(isFreedomUnits) {
                        requestForWeatherData(temp, temp_hi, temp_low, editText.getText().toString(), day_label, night_label, "imperial");
                    }else {
                        isFreedomUnits = false;
                        requestForWeatherData(temp, temp_hi, temp_low, editText.getText().toString(), day_label, night_label, "metric");
                    }
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    private void requestForWeatherData(final TextView temp, final TextView temp_hi, final TextView temp_low, final String location, final TextView dayLabel, final TextView night_label, String unitType) {
        apiInterface = APIClient.getClient().create(APIClient.APIInterface.class);
        Call<WeatherData> call = apiInterface.getLocationWeather(location, "96d06b04ae1e61a7d850e288a8f16b2d", unitType);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                dayLabel.setVisibility(View.VISIBLE);
                night_label.setVisibility(View.VISIBLE);
                weatherData = response.body();
                String degreeType = getDegreeMeasurement();
                temp.setText(trimDecimal(weatherData.getMain().getTemp().toString()) + degreeType);
                temp_hi.setText(trimDecimal(weatherData.getMain().getTempMax().toString()) + degreeType);
                temp_low.setText(trimDecimal(weatherData.getMain().getTempMin().toString()) + degreeType);
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                call.cancel();
                Log.e("RequestWeatherData", "Couldn't make the call");
            }
        });
    }

    private String getDegreeMeasurement() {
        if (isFreedomUnits) {
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

