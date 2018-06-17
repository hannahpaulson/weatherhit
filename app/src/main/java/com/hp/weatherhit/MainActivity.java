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

public class MainActivity extends AppCompatActivity {

    // TODO Private or public?
    // TODO group by type (views, APIs,...)
    WeatherData weatherData;
    String unitType = "imperial";
    Boolean isFreedomUnit = true;
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
    String currentLang = "en";
    String searchLocation;
    Toolbar myToolbar;
    RequestWeatherData requestForWeatherData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setLabelsVisibility(View.GONE);
        setSupportActionBar(myToolbar);

        unit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSwitchUnitType(isChecked);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocation = editText.getText().toString();
                if (searchLocation.matches("")) {
                    Toast.makeText(getApplicationContext(), "Pls Type a location", Toast.LENGTH_SHORT).show();
                } else {
                    getWeatherData(searchLocation, getLanguage());
                }
                closeKeyboard();
            }
        });
    }

    private void getWeatherData(String searchLocation, String currentLang) {
        requestForWeatherData.requestForWeatherData(searchLocation, currentLang,
                new RetrofitCallback() {
                    @Override
                    public void onSuccess(WeatherData response) {
                        displayWeatherData(response);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("getWeatherData", error);
                    }
                });
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void setSwitchUnitType(boolean isChecked) {
        boolean isSearchLocationEmpty = searchLocation.matches("");
        if (isChecked) {
            isFreedomUnit = false;
        } else {
            isFreedomUnit = true;
        }
        if (!isSearchLocationEmpty) {
            setDegreeMeasurement(weatherData.getMain().getTemp(),
                    weatherData.getMain().getTempMax(),
                    weatherData.getMain().getTempMin());
        }
    }

    private void setLabelsVisibility(int gone) {
        day_label.setVisibility(gone);
        night_label.setVisibility(gone);
    }

    private void findViews() {
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
        myToolbar = findViewById(R.id.my_toolbar);
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

    // TODO Is language somthing that this activity should own?
    private String getLanguage() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        currentLang = sharedPreferences.getString("lang", null);
        if (currentLang.equals(null)) {
            return "en";
        }
        return currentLang;
    }

    private void displayWeatherData(WeatherData weatherData) {
        setLabelsVisibility(View.VISIBLE);

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
        // TODO Unit type should be injected, also, it shouldn't be a string (what if I pass a string that says "Imperial"?)
        // If you map it to boolean it would make more sense, but then you'll have to sort this: https://medium.com/@amlcurran/clean-code-the-curse-of-a-boolean-parameter-c237a830b7a3
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

    // TODO is this what the activity shold be doing?
    public static int kelvinToCelsius(double kelvinValue) {
        return (int) (kelvinValue - 273.15);
    }

    public static int kelvinToFahrenheit(double kelvinValue) {
        return (int) (((kelvinValue - 273) * 9 / 5) + 32);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String prefLang = sharedPreferences.getString("lang", null);
        if (!prefLang.equals(currentLang)) {
            getWeatherData(searchLocation, getLanguage());
        }
    }
}

