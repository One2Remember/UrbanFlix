package com.example.myurbanflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This defines the Account Creation page (ignore the warning about the missing layout...)
 */
public class NewAccActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_acc);

        // initialize user preferences
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        prefEditor = myPrefs.edit();

        // make warning invisible
        ((TextView)findViewById(R.id.acc_exists_warning)).setVisibility(View.INVISIBLE);

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
     * Called when the user succesfully creates an account: logs user in and
     * takes user to account page
     */
    public void setLogInThenView() {
        // set login to true
        prefEditor.putBoolean("LoggedIn", true);
        prefEditor.apply();

        // Take user to View Account Page
        startActivity(new Intent(this, ViewAccountActivity.class));
    }

    /**
     * save grabbed fields from username/password into SharedPrefences
     */
    public void saveLoginInfo(String un, String pw) {
        prefEditor.putString("UN", un);
        prefEditor.putString("PW", pw);
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
        final CollectionReference users = mFirestore.collection("users");

        // Get the user information from text fields
        final String email = ((TextView)findViewById(R.id.acc_create_email)).getText().toString();
        final String un = ((TextView)findViewById(R.id.acc_create_username)).getText().toString();
        final String pw = ((TextView)findViewById(R.id.acc_create_password)).getText().toString();

        // Query user collection to see if user exists
        users
            .whereEqualTo("username", un)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        boolean isEmpty = true;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            isEmpty = false;
                        }
                        if(isEmpty) {
                            // make warning invisible
                            ((TextView)findViewById(R.id.acc_exists_warning)).setVisibility(View.INVISIBLE);
                            // Create user
                            User newUser = new User(email, un, pw);
                            // Push user to database
                            users.add(newUser);
                            // save login info (THIS MAY BE SET TO A TOGGLE BUTTON LATER)
                            saveLoginInfo(un, pw);
                            // log in and go to account
                            setLogInThenView();
                        } else {
                            // make warning visible
                            ((TextView)findViewById(R.id.acc_exists_warning)).setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("query_fail", "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}
