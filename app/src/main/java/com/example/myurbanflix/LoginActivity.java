package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * This defines the Login Activity
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /** Called when the user taps the Login button, logs in and takes user home */
    public void loginAndGoHome(View view) {
        /** LOG IN CODE GOES HERE, MISSING VALIDATION OF CREDENTIALS */

        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = myPrefs.edit();
        prefEditor.putBoolean("LoggedIn", true);
        // TODO: ADD PREFEDITOR FUNCTIONALITY TO ADD USERNAME/PASSWORD TO PREFERENCES
        prefEditor.apply();

        /** Take user home */
        startActivity(new Intent(this, MainActivity.class));
    }

    /** Called when user clicks "Create Account" button */
    public void goToCreateAcc(View view) {
        startActivity(new Intent(this, NewAccActivity.class));
    }
}
