package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MovieSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        // Get the Intent that started this activity and extract the query message
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.SEARCH_MESSAGE);

        // find the layout's TextView and set the string as its text (JUST FOR PROOF OF CONCEPT)
        TextView test = findViewById(R.id.search_confirmation);
        test.setText(message);
    }
}
