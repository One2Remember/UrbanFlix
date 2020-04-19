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
    /**
     * a handle to the recycler view in the layout (contains review data)
     */
    private RecyclerView recyclerView;
    /**
     * a handle to the adapter which is used to inflate views to populate the recycler view
     */
    private MovieReviewAdapter mAdapter;
    /**
     * a handle to the layout manager which the recyclerview uses to display the views once
     * they are inflated
     */
    private RecyclerView.LayoutManager layoutManager;
    /**
     * A handle to the firestore connection so it need only be instantiated once
     */
    private FirebaseFirestore mFirestore;
    /**
     * For instantiating shared preferences
     */
    private SharedPreferences myPrefs;
    /**
     * For instantiating shared preferences editor
     */
    private SharedPreferences.Editor prefEditor;
    /**
     * for storing the query used to populate the recycler view
     */
    private Query mQuery;

    /**
     * initializes shared preferences, connection to firestore, and the recycler view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);    // init preference
        initFirestore();   // initialize handle to FireStore
        initRecycler(); // initialize the recycler view, populate from database
    }

    /**
     * for the recycler view to know to listen to db and user input on activity start
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }
    /**
     * for the recycler view to know to stop listening to db and user input on activity end
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    /**
     * initializes a connection to the firestore and produces the query of the db giving back
     * an ascending list of the user's reviews based on the date the reviews were created
     * (oldest reviews appear at the top)
     */
    public void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        // Get reviews from firestore
        // .orderBy("criteria", Query.Direction.{ASCENDING | DESCENDING})
        // .limit(int) will limit the number of reviews pulled from firestore
        String username = myPrefs.getString("UN", "Admin");
        mQuery = mFirestore.collection("reviews").whereEqualTo("userName", username)
                .orderBy("dateCreated", Query.Direction.ASCENDING).limit(50);
    }

    /**
     * initializes the recyclerview
     */
    public void initRecycler() {
        // Set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler_main);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        mAdapter = new MovieReviewAdapter(mQuery, false) {
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
        // specify an adapter
        recyclerView.setAdapter(mAdapter);
    }

    /** Called when the user taps the Logout button, logs out and takes user home */
    public void logoutAndGoHome(View view) {
        prefEditor = myPrefs.edit();
        // remove logged in status, username and password from shared preferences
        prefEditor.putBoolean("LoggedIn", false);
        prefEditor.remove("UN");
        prefEditor.remove("PW");
        prefEditor.apply();
        // Take user home
        startActivity(new Intent(this, MainActivity.class));
    }

    /** Called when user taps the Home button */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
