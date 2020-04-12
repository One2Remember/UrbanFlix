package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * This defines the Account Creation page (ignore the warning about the missing layout...)
 */
public class NewAccActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** This complains for some reason, just ignore it; it works */
        setContentView(R.layout.activity_new_acc);

        // Initializes Firestore
        initFirestore();

        // Action listener for account creation button
        final Button accCreateButton = findViewById(R.id.acc_create_register);
        accCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateAccountClick();
           }
        });
    }

    /**
     * Called when the user taps the create account button, creates account, logs user in and
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

    /** Called when user taps the Home button */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    private void onCreateAccountClick() {
        // Gets a reference to the collection of users in the database
        DocumentReference users = mFirestore.collection("users").document();

        // Get the user information from text fields
        TextView emailTextView = findViewById(R.id.textview_email);
        TextView usernameTextView = findViewById(R.id.textview_username);
        TextView passwordTextView = findViewById(R.id.textview_password);

        // Create user
        User newUser = new User(emailTextView.toString(), usernameTextView.toString(), passwordTextView.toString());

        System.out.println(newUser);
        // Push user to database
        users.set(newUser);
    }
}
