package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.TextView;

/**
 * This opens the home page
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayLoginStatus();   // displays view if user is logged in
    }

    public void displayLoginStatus() {
        TextView tl = (TextView)findViewById(R.id.is_logged_in);
        // get whether user is logged in; if preference does not already exist, assume false
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        if(loggedIn) {
            tl.setVisibility(View.VISIBLE);
        }
        else {
            tl.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Called when the user taps the Account button, either takes them to account if
     * they are logged in, or takes them to login page if they are not
     */
    public void goToAccountScreen(View view) {
        // get whether user is logged in; if preference does not already exist, assume false
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);

        if(loggedIn) {
            startActivity(new Intent(this, ViewAccountActivity.class));
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
