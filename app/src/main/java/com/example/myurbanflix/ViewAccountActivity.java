package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

public class ViewAccountActivity extends AppCompatActivity {
    // these are for use with the recycler view
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        mFirestore = FirebaseFirestore.getInstance();   // initialize handle to FireStore
        initRecycler(); // initialize the recycler view, populate from database

        // FOR JAMIE
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = myPrefs.getString("UN", "Admin");

    }

    public void initRecycler() {
        // Set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter CURRENTLY JUST USES GENERATED DATA SET FROM MovieReview.java
        // THIS NEEDS TO BE ADAPTED TO USE OUR DATABASE SOMEHOW WITH A QUERY BUILT FROM THE
        // USERS QUERY ABOVE ^^ CALLED "message"
        mAdapter = new ReviewAdapterNoButton(MovieReview.GenerateReviewList());
        recyclerView.setAdapter(mAdapter);
    }

    /** Called when the user taps the Logout button, logs out and takes user home */
    public void logoutAndGoHome(View view) {
        /** LOG IN CODE GOES HERE, MISSING VALIDATION OF CREDENTIALS */
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = myPrefs.edit();
        prefEditor.putBoolean("LoggedIn", false);
        prefEditor.apply();

        /** Take user home */
        startActivity(new Intent(this, MainActivity.class));
    }

    /** Called when user taps the Home button */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
