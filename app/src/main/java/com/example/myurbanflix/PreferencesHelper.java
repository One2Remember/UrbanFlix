package com.example.myurbanflix;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Acts as a helper (an interface) for other activities to use in order to access shared preferences
 * Contains several overloaded methods for getting and setting particular preferences based
 * on the type of the parameters given
 */
public class PreferencesHelper {
    /**
     * Allows this class to access shared preferences as if it were the MainActivity (invokes
     * connection to shared preferences through MainActivity's context)
     */
    private Context mainContext;
    /**
     * Local handle to shared preferences (initialized form MainActivity's context)
     */
    private SharedPreferences myPrefs;
    /**
     * Local handle for Shared Preferences mutator methods to use
     */
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
    public String getPreference(String id, String defaultValue) {
        return myPrefs.getString(id, defaultValue);
    }

    /**
     * Allows client to access shared preference bool field by key, returning a default value if
     * preference is not found
     * @param id - preference key to search
     * @param defaultValue - default value to return if key is not found
     * @return either the preference value if found, or the default value otherwise
     */
    public boolean getPreference(String id, boolean defaultValue) {
        return myPrefs.getBoolean(id, defaultValue);
    }

    /**
     * Allows client to access shared preference integer field by key, returning a default value if
     * preference is not found
     * @param id - preference key to search
     * @param defaultValue - default value to return if key is not found
     * @return either the preference value if found, or the default value otherwise
     */
    public int getPreference(String id, int defaultValue) {
        return myPrefs.getInt(id, defaultValue);
    }

    /**
     * Adds or edits a boolean to shared preferences
     * @param id - key of preference to mutate
     * @param value - value of preference
     */
    public void setPreference(String id, boolean value) {
        editor = myPrefs.edit();
        editor.putBoolean(id, value);
        editor.apply();
    }

    /**
     * Adds or edits a String to shared preferences
     * @param id - key of preference to mutate
     * @param value - value of preference
     */
    public void setPreference(String id, String value) {
        editor = myPrefs.edit();
        editor.putString(id, value);
        editor.apply();
    }

    /**
     * Adds or edits an integer to shared preferences
     * @param id - key of preference to mutate
     * @param value - value of preference
     */
    public void setPreference(String id, int value) {
        editor = myPrefs.edit();
        editor.putInt(id, value);
        editor.apply();
    }

    /**
     * Removes a preferences from shared preferences by key
     * @param id - key of preference to remove
     */
    public void removePreference(String id) {
        editor = myPrefs.edit();
        editor.remove(id);
        editor.apply();
    }

}
