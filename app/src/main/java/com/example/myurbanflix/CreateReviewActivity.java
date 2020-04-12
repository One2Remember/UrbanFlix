package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
    }

    /**
     * Called when the user taps the Submit button, adds review to database and goes home
     */
    public void createNewReview(View view) {
        String movieTitle, reviewTitle, reviewBody, date;
        int key;
        key = 1;    // TODO: THIS WILL ACTUALLY BE THE KEY WE PULL FROM THE DB
        // make connection to database
        // pull data from text fields
        reviewTitle = ((EditText)findViewById(R.id.title_field)).getText().toString();
        movieTitle = ((EditText)findViewById(R.id.review_title_field)).getText().toString();
        reviewBody = ((EditText)findViewById(R.id.comment_body)).getText().toString();
        // use data to create object to insert into database (upvotes should be 1, downvotes 0)
        // send object to database

        // go home
        startActivity(new Intent(this, MainActivity.class));
    }
}
