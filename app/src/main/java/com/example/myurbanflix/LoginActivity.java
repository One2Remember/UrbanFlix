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
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This defines the Login Activity page, which allows the user to input their credentials, and
 * if they match, logs the user in and returns them home. Logging in allows the users to view
 * their profiles (post history) as well as upvote, downvote, and create reviews. The page also
 * contains a link to the create account page if the user does not have an account yet
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * For instantiating shared preferences
     */
    private SharedPreferences myPrefs;
    /**
     * For instantiating shared preferences editor
     */
    private SharedPreferences.Editor prefEditor;

    /**
     * When activity is created, initializes all views on the page, makes connection to firestore,
     * and pulls down handles to shared preferences
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // set preference editors
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        prefEditor = myPrefs.edit();
        initView(); // set warning to invisible
    }

    /**
     * hides the invalid credentials warning by default
     * sets the text of username/password field
     */
    public void initView() {
        findViewById(R.id.invalid_credentials).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.textview_username)).setText("Username / Email");
    }

    /**
     * takes user to the home page when they click the login button with valid credentials
     */
    public void goHome() {
        startActivity(new Intent(this, MainActivity.class));
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
     * Called when the user taps the Login button, logs in (if credentials are valid) and takes
     * the user to the home page
     */
    public void login(View view) {
        hideKeyboard(view); // hide the keyboard

        // pull data from text fields (username or email AND password)
        final String un_or_email, password;
        un_or_email = ((EditText)findViewById(R.id.acc_create_username)).getText().toString();
        password = ((EditText)findViewById(R.id.acc_create_password)).getText().toString();

        // create on complete listener for database to use when checking user credentials,
        // appropriately logging in user if credentials are valid or showing warning and keeping
        // user logged out if credentials are not valid
        OnCompleteListener<QuerySnapshot> loginInIfLegal = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean MatchingUser = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MatchingUser = true;
                        // if user matches an entry in database
                        if (MatchingUser) {
                            // mark user as logged in locally
                            prefEditor.putBoolean("LoggedIn", true);
                            prefEditor.apply();
                            // save users username locally
                            prefEditor.putString("UN", document.getString("username"));
                            prefEditor.apply();
                            // save users password locally
                            prefEditor.putString("PW", password);
                            prefEditor.apply();
                            // hide warning
                            findViewById(R.id.invalid_credentials).setVisibility(View.INVISIBLE);
                            // Take user to home activity
                            goHome();
                        }
                    }
                    if(!MatchingUser) {
                        // show warning that the user does not exist in our database
                        findViewById(R.id.invalid_credentials).setVisibility(View.VISIBLE);
                        Log.d("LOGGER", "invalid credentials");
                    }
                } else {
                    Log.d("LOGGER", "Error getting documents: ", task.getException());
                }
            }
        };
        // hand db helper our listener, along with email/username and password, so it can
        // appropriately call our listener above when the query is complete, either logging in the
        // user or showing them a warning that their credentials are invalid
        MainActivity.dbHelper.logInUser(loginInIfLegal, un_or_email, password);
    }

    /**
     * Called when user clicks "Create Account" button (takes user to create account activity)
     */
    public void goToCreateAcc(View view) {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }
}
