package com.example.myurbanflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
 * name of the movie depending on the query that brought it here. It also enforces strict
 * character limits for all fields to ensure the formatting looks nice in the app
 */
public class CreateReviewActivity extends AppCompatActivity {
    /**
     * for holding query if a search bar search brought the user here
     */
    private String message;
    /**
     * A handle to the firestore connection so it need only be instantiated once
     */
    private FirebaseFirestore mFirestore;
    /**
     * a handle to the textview which warns the viewer if they are approaching the character limit
     * for the movie title
     */
    private TextView movieTitleWarn;
    /**
     * a handle to the textview which warns the viewer if they are approaching the character limit
     * for the review title
     */
    private TextView reviewTitleWarn;
    /**
     * a handle to the textview which warns the viewer if they are approaching the character limit
     * for the review comment body
     */
    private TextView reviewBodyWarn;
    /**
     * a handle to the edittext field for the movie title to check user's input
     */
    private EditText movieTitleField;
    /**
     * a handle to the edittext field for the review title to check user's input
     */
    private EditText reviewTitleField;
    /**
     * a handle to the edittext field for the review body to check user's input
     */
    private EditText commentBodyField;
    /**
     * a handle to the submit review button in order to enable/disable it as needed
     */
    private Button submitReviewButton;
    /**
     * For instantiating shared preferences
     */
    private SharedPreferences myPrefs;
    /**
     * For instantiating shared preferences editor
     */
    private SharedPreferences.Editor prefEditor;

    /**
     * grabs handle to shared preferences, makes connection to firebase, and initializes all views
     * on the page to give them functionality
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        // get user preferences
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        // make connection to database
        mFirestore = FirebaseFirestore.getInstance();
        // initialize all views on screen
        initViews();
    }

    /**
     * initializes all views on the page programatically including adding text listeners and
     * properly displaying EditText char limits
     */
    public void initViews() {
        movieTitleWarn = ((TextView)findViewById(R.id.movie_title_warning));
        reviewTitleWarn = ((TextView)findViewById(R.id.review_title_warning));
        reviewBodyWarn = ((TextView)findViewById(R.id.review_body_warning));
        movieTitleField = ((EditText)findViewById(R.id.movie_title_field));
        reviewTitleField = ((EditText)findViewById(R.id.review_title_field));
        commentBodyField = ((EditText)findViewById(R.id.comment_body_field));
        submitReviewButton = ((Button)findViewById(R.id.submit_review_button));
        // sets default warning limits for text field sizes
        initWarnings(24, 38, 300);
        addClickListeners();    // adds on click listeners for focus changes on screen
        // add listeners for text fields to appropriately display warnings if input is too long
        addTextFieldLimitListener(movieTitleWarn, movieTitleField, 24); // mov title is 24
        addTextFieldLimitListener(reviewTitleWarn, reviewTitleField, 38);   // rev is 38
        addTextFieldLimitListener(reviewBodyWarn, commentBodyField, 300);   // body is 300
    }

    /**
     * sets default warning limits for each of the three input fields
     */
    public void initWarnings(int mov_title_lim, int rev_title_lim, int body_lim) {
        processQuery(mov_title_lim);  // populate movie title field if movie query brought us here
        reviewTitleWarn.setText("0/" + String.valueOf(rev_title_lim));
        reviewBodyWarn.setText("0/" + String.valueOf(body_lim));
    }

    /**
     * processes initial message if intent from search brought us here, adds default text for
     * text field and populates initial warning
     */
    public void processQuery(int mov_title_lim) {
        // get query that brought us here if came from movie search page
        message = getIntent().getStringExtra(MainActivity.SEARCH_MESSAGE);
        if(message != null) {
            movieTitleField.setText(message);
            int initSize = message.length();    // get initial size of movie title
            // edit default text for char count for movie title
            movieTitleWarn.setText(String.valueOf(initSize) + '/' + String.valueOf(mov_title_lim));
            if(initSize > 36) { // disable submit button if initial movie title size is too large
                submitReviewButton.setEnabled(false);   // disable submit review
            }
        } else {
            movieTitleWarn.setText("0/" + String.valueOf(mov_title_lim));
        }
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
        ((EditText)findViewById(R.id.comment_body_field)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void addTextFieldLimitListener(final TextView myWarning, final EditText toLimit, final int limit) {
        toLimit.addTextChangedListener(new TextWatcher() {
            /**
             * undefined behavior, not needed for project (included only because it is necessary)
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            /**
             * Show a warning if text is too long
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myWarning.setText(String.valueOf(toLimit.getText().toString().length()) + '/' + String.valueOf(limit));
                if(toLimit.getText().length() > limit) {
                    myWarning.setTextColor(Color.RED); // set warning color
                    submitReviewButton.setEnabled(false);   // disable submit review
                } else {
                    myWarning.setTextColor(Color.parseColor("#0D3BC3")); // set safe color
                    submitReviewButton.setEnabled(true);   // disable submit review
                }
            }

            /**
             * undefined behavior, not needed for project (included only because it is necessary)
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) { }
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

    /**
     * @return a neatly formatted date from the current date
     */
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * mark a review as upvoted in user preferences by review key (provided by the firestore)
     * @param key
     */
    public void updateUserPreference(String key) {
        prefEditor = myPrefs.edit();
        prefEditor.putInt(key, MainActivity.UPVOTED);
        prefEditor.apply();
    }

    /**
     * Called when the user taps the Submit button, adds review to database and goes home
     */
    public void createNewReview(View view) {
        String movieTitle, reviewTitle, reviewBody, date, myUN;
        // make connection to database
        // pull data from text fields
        movieTitle = movieTitleField.getText().toString();
        reviewTitle = reviewTitleField.getText().toString();
        reviewBody = commentBodyField.getText().toString();
        // get username from User Preferences
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
                                updateUserPreference(document.getId());
                            }
                        } else {
                            Log.d("query_fail", "Error getting documents: ", task.getException());
                        }
                    }
                });
        // go home
        startActivity(new Intent(this, MainActivity.class));
    }
}
