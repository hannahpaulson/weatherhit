package com.hp.weatherhit;

import android.content.Context;
import android.content.Intent;
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

    public WeatherData weatherData;
    private Boolean isFreedomUnit = true;
    private TextView temp;
    private TextView temp_hi;
    private TextView temp_low;
    private TextView day_label;
    private TextView night_label;
    private TextView desc;
    private Button button;
    private EditText editText;
    private ImageView weather_image;
    private Switch unit_switch;
    private String searchLocation;
    private Toolbar myToolbar;
    public RequestWeatherData request = new RequestWeatherData();
    private Language language = new Language();
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        findViews();
        setLabelsVisibility(View.GONE);
        setSupportActionBar(myToolbar);

        unit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isSearchLocationEmpty = searchLocation.matches("") || searchLocation.equals(null);
                if (isChecked) {
                    isFreedomUnit = false;
                } else {
                    isFreedomUnit = true;
                }
                if (!isSearchLocationEmpty) {
                    setWeatherDataWithUnit(weatherData);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocation = editText.getText().toString();
                if (searchLocation.matches("")) {
                    Toast.makeText(context, "Pls Type a location", Toast.LENGTH_SHORT).show();
                } else {
                    getWeatherData(searchLocation, language.getCurrentLang(context));
                }
                closeKeyboard();
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

    private void displayWeatherData(WeatherData weatherData) {
        setLabelsVisibility(View.VISIBLE);

        desc.setText(weatherData.getWeather().get(0).getDescription());
        setImage(weatherData.getWeather().get(0).getMain(), weather_image);

        setWeatherDataWithUnit(weatherData);
    }

    private void setWeatherDataWithUnit(WeatherData weatherData) {
        if (isFreedomUnit) {
            temp.setText(weatherData.getMain().getTempInFahrenheit() + "°F");
            temp_hi.setText(weatherData.getMain().getTempMaxInFahrenheit() + "°F");
            temp_low.setText(weatherData.getMain().getTempMinInFahrenheit() + "°F");
        } else {
            temp.setText(weatherData.getMain().getTempInCelsius() + "°C");
            temp_hi.setText(weatherData.getMain().getTempMaxCelsius() + "°C");
            temp_low.setText(weatherData.getMain().getTempMinCelsius() + "°C");
        }
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

    private void getWeatherData(String searchLocation, String currentLang) {
        request.requestForWeatherData(searchLocation, currentLang,
                new RetrofitCallback() {
                    @Override
                    public void onSuccess(WeatherData response) {
                        if (response != null) {
                            weatherData = response;
                            displayWeatherData(response);
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String error) {
                        Log.e("getWeatherData", error);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchLocation != null) {
            getWeatherData(searchLocation, language.getCurrentLang(context));
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}