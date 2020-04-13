package com.example.myurbanflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This defines the Login Activity
 */
public class LoginActivity extends AppCompatActivity {
    SharedPreferences myPrefs;
    SharedPreferences.Editor prefEditor;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // set preference editors
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        prefEditor = myPrefs.edit();
        // set warning to invisible
        ((TextView)findViewById(R.id.invalid_credentials)).setVisibility(View.INVISIBLE);
        // set up connection to database
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void goHome() {
        startActivity(new Intent(this, MainActivity.class));
    }

    // hides keyboard when user clicks out of text edit
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** Called when the user taps the Login button, logs in and takes user home */
    public void login(View view) {
        hideKeyboard(view); // hide the keyboard

        final String username, password;
        // pull data from text fields
        username = ((EditText)findViewById(R.id.acc_create_username)).getText().toString();
        password = ((EditText)findViewById(R.id.acc_create_password)).getText().toString();
        // Gets a reference to the collection of users in the database
        final CollectionReference users = mFirestore.collection("users");

        // Query user collection to see if user exists
        users
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean MatchingUser = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MatchingUser = true;
                            }
                            // if user matches an entry in database
                            if(MatchingUser) {
                                // mark user as logged in locally
                                prefEditor.putBoolean("LoggedIn", true);
                                prefEditor.apply();
                                // save users username locally
                                prefEditor.putString("UN", username);
                                prefEditor.apply();
                                // save users password locally
                                prefEditor.putString("PW", password);
                                prefEditor.apply();
                                // hide warning
                                ((TextView)findViewById(R.id.invalid_credentials)).setVisibility(View.INVISIBLE);
                                // Take user home
                                goHome();
                            } else {
                                // show warning that the user does not exist in our database
                                ((TextView)findViewById(R.id.invalid_credentials)).setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("query_fail", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /** Called when user clicks "Create Account" button */
    public void goToCreateAcc(View view) {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }
}
