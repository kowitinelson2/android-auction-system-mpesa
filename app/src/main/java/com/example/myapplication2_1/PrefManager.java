package com.example.myapplication2_1;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    //used when you want to create a preference activity or load some preferences

    private final Context context;
    private SharedPreferences prefs;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void writeIntoPreferences(String[] names, String[] values) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        for (int i = 0; i < names.length; i++) {
            editor.putString(names[i], values[i]);
        }
        editor.apply();
    }

    public String readPreference(String name, String defaultValue) {
        return getSharedPreferences().getString(name, defaultValue);
    }

    public void clearPreference(String[] names) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        for (int i = 0; i < names.length; i++) {
            editor.putString(names[i], "");
        }
        editor.apply();
    }

    public void clearAll() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        }
        return prefs;
    }

}
