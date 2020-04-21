package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * This opens the home page and inits a connection to the firebase db in order to populate
 * a local recycler view which the user can interact with; Upvotes and Downvotes are sent
 * to the database and refreshed locally; SharedPreferences are also initialized here via the
 * PreferencesHelper class
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
     * for non-activity classes to access shared user prefs, we allow them to pretend they are
     * operating within the context of the main activity
     */
    public static Context contextOfApplication;
    /**
     * So other classes can access a single instance of PreferencesHelper to edit/pull from
     * shared preferences
     */
    public static PreferencesHelper prefHelper;
    /**
     * A helper for accessing our database for the purpose of generating queries for this class
     * as well as other classes (public static for this reason)
     */
    public static FirestoreHelper dbHelper;
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
     * For holding the query used to populate the recycler view from the db
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
        dbHelper = new FirestoreHelper();   // initialize a helper to access the firestore
        prefHelper = new PreferencesHelper();   // initialize a helper to access shared preferences
        loginIfAvailable(); // login user if they have stored credentials
        initView();         // Initialize recycler view, search bar, account button
    }

    /**
     * Logs user in if they have locally stored credentials, return whether login was successful
     * or not
     */
    boolean loginIfAvailable() {
        // get local credentials if saved
        String un = prefHelper.getPreference("UN", "null");
        String pw = prefHelper.getPreference("PW", "null");
        // if they are both stored previously, log in user
        if(!un.equals("null") && !pw.equals("null")) {
            prefHelper.setPreference("LoggedIn", true);
            return true;
        }
        return false;
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
        searchBarToMovieSearch();   // adds a listener to search bar
        initRecycler();    // set up recycler view
        initButtons();     // set login and fab button based on user logged in status
    }

    /**
     * sets account button to say either account or login based on login status, also enables
     * or disables the FAB for adding a review to keep non-logged users from writing reviews
     */
    public void initButtons() {
        // get login status
        boolean loggedIn = prefHelper.getPreference("LoggedIn", false);
        if(loggedIn) {  // set account button and enable fab
            ((Button)findViewById(R.id.login)).setText("Account");
            findViewById(R.id.create_review_main).setVisibility(View.VISIBLE);
        }
        else {  // set login button and disable fab
            ((Button)findViewById(R.id.login)).setText("Login");
            findViewById(R.id.create_review_main).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set up the recycler view based on the query result provided by the database. Uses our
     * personally designed adapter as well as the preconstructed linear layout manager
     */
    public void initRecycler() {
        // initialize handle to recyclerView
        recyclerView = findViewById(R.id.movie_list_recycler_main);
        // initialize our connection to the firestore and have dbHelper set the query which will
        // populate our recyclerview
        mQuery = dbHelper.getGeneralRecyclerQuery("reviews", "dateCreated", Query.Direction.DESCENDING, 50);
        // improves performance since our design allows us to fix the recycler size
        recyclerView.setHasFixedSize(true);
        // Pull and display data from database
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
        // get login status
        boolean loggedIn = prefHelper.getPreference("LoggedIn", false);
        if(loggedIn) {  // go to view account page
            startActivity(new Intent(this, ViewAccountActivity.class));
        }
        else {  // go to login page
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
