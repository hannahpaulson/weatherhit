package com.hp.weatherhit;

import android.content.Context;
import android.content.SharedPreferences;

public class Language {

    String currentLang = "en";

    private String getCurrentLang(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        currentLang = sharedPreferences.getString("lang", null);
        if (currentLang.equals(null)) {
            return "en";
        }
        return currentLang;
    }

    public boolean langIsNew(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String prefLang = sharedPreferences.getString("lang", null);
        return prefLang.equals(currentLang);
    }
}
