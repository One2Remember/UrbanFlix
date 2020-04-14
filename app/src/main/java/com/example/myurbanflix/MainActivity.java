package com.example.myurbanflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * This opens the home page
 */
public class MainActivity extends AppCompatActivity {
    // this is for sending a message to movie search from search bar
    public static final String SEARCH_MESSAGE = "com.example.myurbanflix.MESSAGE";
    public static final int UPVOTED = 1;
    public static final int DOWNVOTED = -1;
    public static final int NOTVOTED = 0;
    // these are for use with the recycler view
    private RecyclerView recyclerView;
    // private RecyclerView.Adapter mAdapter;
    private MovieReviewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    // for non-activity classes to access shared user prefs
    public static Context contextOfApplication;
    SharedPreferences myPrefs;
    SharedPreferences.Editor prefEditor;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // login user if they have stored credentials
        loginIfAvailable();
        // grab user preferences
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        // set my context for sharing with other non-activity classes
        contextOfApplication = getApplicationContext();
        // declare handle to recyclerView
        recyclerView = findViewById(R.id.movie_list_recycler);
        initFirestore();    // Initialize Firestore
        initView();         // Initialize recycler view
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

    /**
     * initialize various components within the view
     */
    public void initView() {
        searchBarToMovieSearch();   // adds a listener to search bar
        setUpRecycler();    // set up recycler view
        setUpAccountButton();   // set login button text based on user logged in status
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
                .orderBy("upvotes", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("upvotes", 0)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(50);
    }
    /**
     * sets account button to say either account or login based on login status
     */
    public void setUpAccountButton() {
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);
        if(loggedIn) {
            ((Button)findViewById(R.id.login)).setText("Account");
        }
        else {
            ((Button)findViewById(R.id.login)).setText("Login");
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
