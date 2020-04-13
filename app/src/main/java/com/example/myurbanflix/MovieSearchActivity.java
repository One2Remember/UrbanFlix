package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import static android.view.View.VISIBLE;

/**
 * This is the activity that is opened when a user searches for a particular movie.
 * String 'message' contains the user query, so what this needs to do is use that query
 * to populate from our database a recycler view containing movie titles that match that query
 */
public class MovieSearchActivity extends AppCompatActivity {
    // these are for use with the recycler view
    private RecyclerView recyclerView;
    private MovieReviewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String message; // for holding user query that got user to this page

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        // adds a listener to search bar at the top
        searchBarToMovieSearch();

        // Get the Intent that started this activity and extract the query message
        message = getIntent().getStringExtra(MainActivity.SEARCH_MESSAGE);
        // Set title of page based on user query (this is all we're doing with the query right now)
        TextView title = findViewById(R.id.results_for);
        title.setText("Results for: " + message);

        initFirestore();
        initRecycler();

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
        mQuery = mFirestore.collection("reviews").whereEqualTo("movieName", message)
                .orderBy("dateCreated", Query.Direction.DESCENDING).limit(50);
    }

    public void initRecycler() {
        // Set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        mAdapter = new MovieReviewAdapter(mQuery, true) {
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
        recyclerView.setAdapter(mAdapter);

        // hide the create a review button if the user is not logged in
        showHideMakeReviewButton();
    }

    public void showHideMakeReviewButton() {
        // get whether user is logged in; if preference does not already exist, assume false
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        if(loggedIn) {
            ((FloatingActionButton)findViewById(R.id.add_button)).setVisibility(View.VISIBLE);
        }
        else {
            ((FloatingActionButton)findViewById(R.id.add_button)).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Attaches a listener to the search bar to send data (query) to new activity:
     * MovieSearchActivity
     */
    public void searchBarToMovieSearch() {
        final SearchView searchView = (SearchView)findViewById(R.id.movie_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // when the user hits enter this will call callSearch(query) which I define below
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false); // clear the search bar
                searchView.clearFocus();    // hides the search bar
                callSearch(query);  // uses search to start new activity
                return true;
            }
            // if we want to do auto suggest features or something they can go here
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            // this is what is called when the user hits enter in the search bar
            public void callSearch(String query) {
                if(!query.isEmpty()) {  // if query is not empty
                    // make a new Intent to open the MovieSearchActivity
                    Intent intent = new Intent(MovieSearchActivity.this, MovieSearchActivity.class);
                    // add the search query to the Intent so MovieSearchActivity can see the query
                    intent.putExtra(MainActivity.SEARCH_MESSAGE, query);
                    // start the new Activity (with the message attached)
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Called when user clicks "Home" button
     * @param view
     */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Called when user clicks FAB
     */
    public void goToCreateReview(View view) {
        Intent intent = new Intent(this, CreateReviewActivity.class);
        intent.putExtra(MainActivity.SEARCH_MESSAGE, message);
        startActivity(intent);
    }
}
