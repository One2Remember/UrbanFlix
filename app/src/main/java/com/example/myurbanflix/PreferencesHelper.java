package com.example.myurbanflix;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Acts as a helper (an interface) for other activities to use in order to access shared preferences
 */
public class PreferencesHelper {
    private Context mainContext;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor editor;

    /**
     * Instantiates a SharedPreferences helper based on the Context of the MainActivity.
     */
    public PreferencesHelper() {
        // get context of main activity so shared preferences can be referenced
        mainContext = MainActivity.getContextOfApplication();
        // grab handle to shared preferences
        myPrefs = mainContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
    }

    /**
     * Allows client to access shared preference string field by key, returning a default value if
     * preference is not found
     * @param id - preference key to search
     * @param defaultValue - default value to return if key is not found
     * @return either the preference value if found, or the default value otherwise
     */
    public String getStringPreference(String id, String defaultValue) {
        return myPrefs.getString(id, defaultValue);
    }

    /**
     * Allows client to access shared preference bool field by key, returning a default value if
     * preference is not found
     * @param id - preference key to search
     * @param defaultValue - default value to return if key is not found
     * @return either the preference value if found, or the default value otherwise
     */
    public boolean getBoolPreference(String id, boolean defaultValue) {
        return myPrefs.getBoolean(id, defaultValue);
    }

    /**
     * Allows client to access shared preference integer field by key, returning a default value if
     * preference is not found
     * @param id - preference key to search
     * @param defaultValue - default value to return if key is not found
     * @return either the preference value if found, or the default value otherwise
     */
    public int getIntPreference(String id, int defaultValue) {
        return myPrefs.getInt(id, defaultValue);
    }

    /**
     * Adds or edits a boolean to shared preferences
     * @param id - key of preference
     * @param value - value of preference
     */
    public void setPreference(String id, boolean value) {
        editor = myPrefs.edit();
        editor.putBoolean(id, value);
        editor.apply();
    }

    /**
     * Adds or edits a String to shared preferences
     * @param id - key of preference
     * @param value - value of preference
     */
    public void setPreference(String id, String value) {
        editor = myPrefs.edit();
        editor.putString(id, value);
        editor.apply();
    }

    /**
     * Adds or edits an integer to shared preferences
     * @param id - key of preference
     * @param value - value of preference
     */
    public void setPreference(String id, int value) {
        editor = myPrefs.edit();
        editor.putInt(id, value);
        editor.apply();
    }

}
