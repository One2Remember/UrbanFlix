package com.example.myurbanflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class manages the Create Review Activity using Firestore, in which users can create
 * reviews if they are logged in, and push those reviews to the firestore. This class takes
 * care of autopopulating some data like username and date, and suggests a field for the
 * name of the movie depending on the query that brought it here
 */
public class CreateReviewActivity extends AppCompatActivity {
    private String message; // for holding query if a query brought user here
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        // make connection to database
        mFirestore = FirebaseFirestore.getInstance();

        // get query that brought us here
        message = getIntent().getStringExtra(MainActivity.SEARCH_MESSAGE);
        if(!message.isEmpty()) {
            ((EditText)findViewById(R.id.movie_title_field)).setText(message);
        }

        // add listeners for text edit fields
        addClickListeners();
    }

    /**
     * for hiding the keyboard when user clicks away from text field
     */
    public void addClickListeners() {
        ((EditText)findViewById(R.id.movie_title_field)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        ((EditText)findViewById(R.id.review_title_field)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        ((EditText)findViewById(R.id.comment_body)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    /**
     * hides keyboard when user clicks out of text edit
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //

    /***
     * @return a neatly formatted date from the current date
     */
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    /**
     * Called when the user taps the Submit button, adds review to database and goes home
     */
    public void createNewReview(View view) {
        String movieTitle, reviewTitle, reviewBody, date, myUN;
        final String[] id = new String[1];
        // make connection to database
        // pull data from text fields
        movieTitle = ((EditText)findViewById(R.id.movie_title_field)).getText().toString();
        reviewTitle = ((EditText)findViewById(R.id.review_title_field)).getText().toString();
        reviewBody = ((EditText)findViewById(R.id.comment_body)).getText().toString();
        // get username from User Preferences
        SharedPreferences myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        myUN = myPrefs.getString("UN", "Admin");
        // get date from java functions
        date = getDate();
        // use data to create object to insert into database (upvotes should be 1, downvotes 0)
        MovieReview newReview = new MovieReview(reviewTitle, movieTitle, myUN, date, reviewBody);

        // Gets a reference to the collection of users in the database
        final CollectionReference reviewDB = mFirestore.collection("reviews");
        reviewDB.add(newReview);    // push object to database

        // pull down review that was just made and grab its key
        // Query user collection to see if user exists
        reviewDB
                .whereEqualTo("userName", myUN)
                .whereEqualTo("dateCreated", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("query_success", document.getId() + " => " + document.getData());
                                id[0] = document.getId();   // grab the key of the review
                            }
                        } else {
                            Log.d("query_fail", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // mark it as upvoted in user preferences
        SharedPreferences.Editor prefEditor = myPrefs.edit();
        prefEditor.putInt(id[0], MainActivity.UPVOTED);
        prefEditor.apply();

        // go home
        startActivity(new Intent(this, MainActivity.class));
    }
}
