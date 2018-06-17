package com.hp.weatherhit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Test
    public void kelvinToCelciusTest() {
        assertThat(MainActivity.kelvinToCelsius(289.15), is(16));
    }

    @Test
    public void kelvinToFahrenheitTest() {
        assertThat(MainActivity.kelvinToFahrenheit(290), is(62));
    }


}