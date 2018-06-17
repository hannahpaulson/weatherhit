package com.hp.weatherhit;

import android.content.Context;
import android.content.SharedPreferences;

public class Language {

    private String currentLang;

    public String getCurrentLang(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String prefLang = sharedPreferences.getString("lang", null);
        if (currentLang == null || prefLang == null) {
            return currentLang = "en";
        } else if (prefLang.equals(currentLang)) {
            return currentLang;
        } else {
            return prefLang;
        }
    }
}
