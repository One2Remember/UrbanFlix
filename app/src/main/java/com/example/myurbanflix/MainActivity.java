package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * This opens the home page
 */
public class MainActivity extends AppCompatActivity {
    // this is for sending a message to movie search from search bar
    public static final String SEARCH_MESSAGE = "com.example.myurbanflix.MESSAGE";
    // these are for use with the recycler view
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    // for non-activity classes to access shared user prefs
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set my context for sharing with other non-activity classes
        contextOfApplication = getApplicationContext();

        // displayLoginStatus();   // displays view if user is logged in (NOT USED)

        searchBarToMovieSearch();   // adds a listener to search bar

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

    // get context for objects not in proper context
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    /** Displays a view if user is logged in telling user they are logged in - for testing */
//    public void displayLoginStatus() {
//        // find element to make either visible or invisible
//        TextView tl = (TextView)findViewById(R.id.is_logged_in);
//        // get whether user is logged in; if preference does not already exist, assume false
//        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
//        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
//        // set textview to visible or invisible based on status of loggedIn variable from prefs
//        if(loggedIn) {
//            tl.setVisibility(View.VISIBLE);
//        }
//        else {
//            tl.setVisibility(View.INVISIBLE);
//        }
//    }

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
                    Intent intent = new Intent(MainActivity.this, MovieSearchActivity.class);
                    // add the search query to the Intent so MovieSearchActivity can see the query
                    intent.putExtra(SEARCH_MESSAGE, query);
                    // start the new Activity (with the message attached)
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Called when the user taps the Account button, either takes them to account if
     * they are logged in, or takes them to login page if they are not
     */
    public void goToAccountScreen(View view) {
        // get whether user is logged in; if preference does not already exist, assume false
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);

        if(loggedIn) {
            startActivity(new Intent(this, ViewAccountActivity.class));
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
