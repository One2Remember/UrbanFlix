package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CreateReviewActivity extends AppCompatActivity {
    private String message; // for holding query if a query brought user here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);

        // get query that brought us here
        message = getIntent().getStringExtra(MainActivity.SEARCH_MESSAGE);
        if(!message.isEmpty()) {
            ((EditText)findViewById(R.id.title_field)).setText(message);
        }

        // add listeners for text edit fields
        addClickListeners();
    }

    /**
     * for hiding the keyboard when user clicks away from text field
     */
    public void addClickListeners() {
        ((EditText)findViewById(R.id.title_field)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    // hides keyboard when user clicks out of text edit
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Called when the user taps the Submit button, adds review to database and goes home
     */
    public void createNewReview(View view) {
        String movieTitle, reviewTitle, reviewBody, date;
        int key;
        key = 1;    // TODO: THIS WILL ACTUALLY BE THE KEY WE PULL FROM THE DB, THE NUMBER OF REVIEWS +1
        // make connection to database
        // pull data from text fields
        movieTitle = ((EditText)findViewById(R.id.title_field)).getText().toString();
        reviewTitle = ((EditText)findViewById(R.id.review_title_field)).getText().toString();
        reviewBody = ((EditText)findViewById(R.id.comment_body)).getText().toString();
        // use data to create object to insert into database (upvotes should be 1, downvotes 0)
        // send object to database

        // go home
        startActivity(new Intent(this, MainActivity.class));
    }
}
