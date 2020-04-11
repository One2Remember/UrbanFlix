package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * This is the activity that is opened when a user searches for a particular movie.
 * String 'message' contains the user query, so what this needs to do is use that query
 * to populate from our database a recycler view containing movie titles that match that query
 */
public class MovieSearchActivity extends AppCompatActivity {
    // this is for sending a message to movie search from search bar
    public static final String SEARCH_MESSAGE = "com.example.myurbanflix.MESSAGE";
    // these are for use with the recycler view
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        // adds a listener to search bar at the top
        searchBarToMovieSearch();

        // Get the Intent that started this activity and extract the query message
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.SEARCH_MESSAGE);

        // Set title of page based on user query (this is all we're doing with the query right now)
        TextView title = findViewById(R.id.results_for);
        title.setText("Results for: " + message);

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
        mAdapter = new ReviewAdapter(MovieReview.GenerateReviewList());
        recyclerView.setAdapter(mAdapter);
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
                    intent.putExtra(SEARCH_MESSAGE, query);
                    // start the new Activity (with the message attached)
                    startActivity(intent);
                }
            }
        });
    }

    /** Called when user clicks "Home" button */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
