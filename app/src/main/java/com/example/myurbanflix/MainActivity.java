package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayLoginStatus();   // displays view if user is logged in
        searchBarToMovieSearch();   // adds a listener to search bar
    }

    /** Displays a view if user is logged in telling user they are logged in - for testing */
    public void displayLoginStatus() {
        // find element to make either visible or invisible
        TextView tl = (TextView)findViewById(R.id.is_logged_in);
        // get whether user is logged in; if preference does not already exist, assume false
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        // set textview to visible or invisible based on status of loggedIn variable from prefs
        if(loggedIn) {
            tl.setVisibility(View.VISIBLE);
        }
        else {
            tl.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Attaches a listener to the search bar to send data (query) to new activity:
     * MovieSearchActivity
     */
    public void searchBarToMovieSearch() {
        SearchView searchView = (SearchView)findViewById(R.id.movie_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // when they hit enter it'll call callSearch(query) which I define below
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }
            // if we want to do like auto suggest features or something they can go here
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
