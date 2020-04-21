package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * This is the activity that is opened when a user searches for a particular movie.
 * String 'message' contains the user query, so this activity uses that query
 * to populate from our database a recycler view containing movie titles that match that query
 */
public class MovieSearchActivity extends AppCompatActivity {
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
     * for holding user query that got user to this page
     */
    private String message;
    /**
     * for storing the query used to populate the recycler view
     */
    private Query mQuery;

    /**
     * grabs the query that brought the user here, uses it to initialize a connection to the
     * firestore and generate a query, then initializes all views (including the recyclerview)
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);
        // Get the Intent that started this activity and extract the query message
        message = getIntent().getStringExtra(MainActivity.SEARCH_MESSAGE);
        initViews();    // initialize all views (including recycler) in the activity
    }

    /**
     * sets the title bar (results for: [query]), adds a listener to the search bar so user
     * can make a new search, inits the recyclerview to display reviews that match the user's
     * query
     */
    private void initViews() {
        TextView title = findViewById(R.id.results_for);    // grab handle to the page title
        title.setText("Results for: " + message);   // set the title of the page based on user query
        searchBarToMovieSearch();   // adds a listener to search bar at the top
        initRecycler(); // initializes the recycler view
        showHideMakeReviewButton(); // hide the create a review button if the user is not logged in
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
     * initializes the recycler view used to display the movie reviews that match the user's
     * query (which is grabbed in the initFirestore() method above)
     */
    public void initRecycler() {
        // get query for use in recycler - up to 50 reviews, matching name (ignoring case),
        // sorted newest->oldest
        mQuery = MainActivity.dbHelper.getMatchingRecyclerQuery("reviews", "movieNameLower",
                message.toLowerCase(), "dateCreated", Query.Direction.DESCENDING, 50);
        // Set up the RecyclerView
        recyclerView = findViewById(R.id.movie_list_recycler_search);
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
        recyclerView.setAdapter(mAdapter);  // specify we want to user our adapter
    }

    /**
     * Shows or hides the FAB that allows user to create a review based on their logged_in status
     */
    public void showHideMakeReviewButton() {
        // get whether user is logged in; if preference does not already exist, assume false
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        if(loggedIn) {
            findViewById(R.id.add_button).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.add_button).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Attaches a listener to the search bar to send data (query) to new activity:
     * MovieSearchActivity
     */
    public void searchBarToMovieSearch() {
        final SearchView searchView = findViewById(R.id.movie_search);
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
     * Called when user clicks FAB (goes to the create review activity)
     * @param view
     */
    public void goToCreateReview(View view) {
        Intent intent = new Intent(this, CreateReviewActivity.class);
        intent.putExtra(MainActivity.SEARCH_MESSAGE, message);
        startActivity(intent);
    }
}
