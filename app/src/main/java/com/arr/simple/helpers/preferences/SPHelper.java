package com.arr.simple.helpers.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class SPHelper {
    
    private final SharedPreferences sp;

    public SPHelper(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    // Métodos adicionales para otros tipos de datos
    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public void removeKey(String key) {
        sp.edit().remove(key).apply();
    }

    public void clearAll() {
        sp.edit().clear().apply();
    }
}
