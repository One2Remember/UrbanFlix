package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ViewAccountActivity extends AppCompatActivity {
    // these are for use with the recycler view
    private RecyclerView recyclerView;
    private MovieReviewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private SharedPreferences myPrefs;

    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        initFirestore();   // initialize handle to FireStore
        initRecycler(); // initialize the recycler view, populate from database
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // Get reviews from firestore
        // .orderBy("criteria", Query.Direction.{ASCENDING | DESCENDING})
        // .limit(int) will limit the number of reviews pulled from firestore
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = myPrefs.getString("UN", "Admin");
        mQuery = mFirestore.collection("reviews").whereEqualTo("userName", username)
                .orderBy("dateCreated", Query.Direction.ASCENDING).limit(50);
    }

    public void initRecycler() {
        // Set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        mAdapter = new MovieReviewAdapter(mQuery) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter CURRENTLY JUST USES GENERATED DATA SET FROM MovieReview.java
        // THIS NEEDS TO BE ADAPTED TO USE OUR DATABASE SOMEHOW WITH A QUERY BUILT FROM THE
        // USERS QUERY ABOVE ^^ CALLED "message"
        // mAdapter = new ReviewAdapterNoButton(MovieReview.GenerateReviewList());
        recyclerView.setAdapter(mAdapter);
    }

    /** Called when the user taps the Logout button, logs out and takes user home */
    public void logoutAndGoHome(View view) {
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = myPrefs.edit();
        prefEditor.putBoolean("LoggedIn", false);
        prefEditor.apply();
        // remove username and password from shared preferences
        prefEditor.remove("Username");
        prefEditor.apply();
        prefEditor.remove("Password");
        prefEditor.apply();

        /** Take user home */
        startActivity(new Intent(this, MainActivity.class));
    }

    /** Called when user taps the Home button */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
