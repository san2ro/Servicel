package com.arr.simple.helpers.theme;
import android.content.Context;
import android.content.SharedPreferences;

public class ThemePreference {
    
    private static final String PREF_NAME = "theme_preferences";
    private static final String KEY_AMOLED_THEME = "amoled_theme";

    public static void setAmoledThemeEnabled(Context context, boolean enabled) {
        SharedPreferences preferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_AMOLED_THEME, enabled).apply();
    }

    public static boolean isAmoledThemeEnabled(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_AMOLED_THEME, false);
    }
}
