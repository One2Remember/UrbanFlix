package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


/**
 * This opens the home page and makes a connection to the firebase db in order to populate
 * a local recycler view which the user can interact with. Upvotes and Downvotes are sent
 * to the database and refreshed locally
 */
public class MainActivity extends AppCompatActivity {
    /**
     * this is for sending a query message to movie search activity from search bar
     */
    public static final String SEARCH_MESSAGE = "com.example.myurbanflix.MESSAGE";
    /**
     * functions as a global const for more readable code in db operations
     */
    public static final int UPVOTED = 1;
    /**
     * functions as a global const for more readable code in db operations
     */
    public static final int DOWNVOTED = -1;
    /**
     * functions as a global const for more readable code in db operations
     */
    public static final int NOTVOTED = 0;
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
     * for non-activity classes to access shared user prefs, we allow them to pretend they are
     * operating within the context of the main activity
     */
    public static Context contextOfApplication;
    /**
     * For instantiating shared preferences
     */
    private SharedPreferences myPrefs;
    /**
     * For instantiating shared preferences editor
     */
    private SharedPreferences.Editor prefEditor;
    /**
     * A handle to the firestore connection so it need only be instantiated once
     */
    private FirebaseFirestore mFirestore;
    /**
     *
     */
    private Query mQuery;

    /**
     * grabs user preferences, logs in the user if they have stored login credentials, grabs a
     * handle to this activity's context so that MovieReview and User classes may access shared
     * preferences as needed (w/o a context), initialize connection to database, initialize all
     * views on the screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext(); // set context for sharing w/non-activities
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);   // grab user prefs
        loginIfAvailable(); // login user if they have stored credentials
        initFirestore();    // Initialize Firestore
        initView();         // Initialize recycler view, search bar, account button
    }

    /**
     * Logs user in if they have locally stored credentials
     */
    void loginIfAvailable() {
        prefEditor = myPrefs.edit();
        String un = myPrefs.getString("UN", "null");
        String pw = myPrefs.getString( "PW", "null");
        if(!un.equals("null") && !pw.equals("null")) {
            prefEditor.putBoolean("LoggedIn", true);
        }
        prefEditor.apply();
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
     * initialize various view components within the activity
     */
    public void initView() {
        recyclerView = findViewById(R.id.movie_list_recycler_main); // declare handle to recyclerView
        searchBarToMovieSearch();   // adds a listener to search bar
        setUpRecycler();    // set up recycler view
        setUpButtons();     // set login and fab button based on user logged in status
    }

    /**
     * initialize our connection to the firestore and set the query which will populate our
     * recyclerview
     */
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        // Get reviews from firestore
        // .orderBy("criteria", Query.Direction.{ASCENDING | DESCENDING})
        // .limit(int) will limit the number of reviews pulled from firestore
        mQuery = mFirestore.collection("reviews")
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(50);
    }
    /**
     * sets account button to say either account or login based on login status, also enables
     * or disables the FAB for adding a review to keep non-logged users from writing reviews
     */
    public void setUpButtons() {
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        if(loggedIn) {
            ((Button)findViewById(R.id.login)).setText("Account");
            ((FloatingActionButton)findViewById(R.id.create_review_main)).setVisibility(View.VISIBLE);
        }
        else {
            ((Button)findViewById(R.id.login)).setText("Login");
            ((FloatingActionButton)findViewById(R.id.create_review_main)).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * set up the recycler view
     */
    public void setUpRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // Pull and display from database
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
        recyclerView.setAdapter(mAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(contextOfApplication,
                DividerItemDecoration.VERTICAL));
    }

    /**
     * @return context for classes that are not activities (so they can see shared preferences)
     */
    public static Context getContextOfApplication(){
        return contextOfApplication;
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
     * Called when the user clicks the create a review button
     * @param view
     */
    public void goToCreateReview(View view) {
        startActivity(new Intent(this, CreateReviewActivity.class));
    }

    /**
     * Called when the user taps the Account button, either takes them to account if
     * they are logged in, or takes them to login page if they are not
     */
    public void goToAccountScreen(View view) {
        // get whether user is logged in; if preference does not already exist, assume false
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        Log.d("logged_in_main", String.valueOf(loggedIn));
        if(loggedIn) {
            startActivity(new Intent(this, ViewAccountActivity.class));
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
