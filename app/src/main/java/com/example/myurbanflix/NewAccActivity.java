package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * This defines the Account Creation page (ignore the warning about the missing layout...)
 */
public class NewAccActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** This complains for some reason, just ignore it; it works */
        setContentView(R.layout.activity_new_acc);
    }

    /** Called when the user taps the create account button, creates account, logs user in and
     * takes user to account page
     */
    public void createAccountThenView(View view) {
        /** CREATE AN ACCOUNT CODE GOES HERE */

        /** LOG IN CODE GOES HERE, CURRENTLY JUST SETS LOGGEDIN to true*/
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = myPrefs.edit();
        prefEditor.putBoolean("LoggedIn", true);
        prefEditor.apply();

        /** Take user to View Account Page */
        startActivity(new Intent(this, ViewAccountActivity.class));
    }
}
