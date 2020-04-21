package com.example.myurbanflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This defines the Account Creation page, which takes input from text fields and compares
 * to database to ensure no duplicate usernames/emails are being used. If new user fields
 * are valid, creates a new user and pushes it to the database
 */
public class CreateAccountActivity extends AppCompatActivity {
    /**
     * For instantiating shared preferences
     */
    private SharedPreferences myPrefs;
    /**
     * For instantiating shared preferences editor
     */
    private SharedPreferences.Editor prefEditor;
    /**
     * a handle to the edit text field for the email address, for reading user input
     */
    private EditText emailField;
    /**
     * a handle to the edit text field for the user name, for reading user input
     */
    private EditText usernameField;
    /**
     * a handle to the edit text field for the password, for reading user input
     */
    private EditText passwordField;
    /**
     * a handle to the textview in the case that the credentials provided are already in use in
     * the database
     */
    private TextView accWarning;
    /**
     * a handle to the account create button, to give it functionality
     */
    private Button accCreateButton;

    /**
     * initializes user preferences, google firestore connection, and all views on the page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initPreferences();  // Initializes User Preferences
        initViews();        // initialize views
    }

    /**
     * initialize user preferences handle
     */
    public void initPreferences() {
        myPrefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
    }

    /**
     * initializes all views on the screen, adding default text as well as listeners and click
     * methods for buttons and fields as needed
     */
    public void initViews() {
        accWarning = findViewById(R.id.acc_exists_warning);
        emailField = findViewById(R.id.acc_create_email);
        usernameField = findViewById(R.id.acc_create_username);
        passwordField = findViewById(R.id.acc_create_password);
        accWarning.setVisibility(View.INVISIBLE);   // make warning invisible by default
        accCreateButton = findViewById(R.id.submit_review_button);  // set create acc button
        accCreateButton.setOnClickListener(new View.OnClickListener() { // add listener
            @Override
            public void onClick(View v) {
                // Get the user information from text fields
                final String email = emailField.getText().toString();
                final String un = usernameField.getText().toString();
                final String pw = passwordField.getText().toString();
                tryCreateAccountUN(email, un, pw);
            }
        });
    }

    /**
     * Called when the user succesfully creates an account: logs user in and
     * takes user to account page
     */
    public void setLogInThenView() {
        prefEditor = myPrefs.edit();    /// open shared pref editor
        prefEditor.putBoolean("LoggedIn", true);    // set login to true
        prefEditor.apply(); // commit changes
        // Take user to View Account Page
        startActivity(new Intent(this, ViewAccountActivity.class));
    }

    /**
     * save grabbed fields from username/password into SharedPrefences
     */
    public void saveLoginInfo(String un, String pw) {
        prefEditor = myPrefs.edit();
        prefEditor.putString("UN", un);
        prefEditor.putString("PW", pw);
        prefEditor.apply();
    }

    /** Called when user taps the Home button */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }


    /**
     * called when user clicks create account, if the UN is in use, show warning, else make sure
     * email is not in use either before trying to create account
     */
    private void tryCreateAccountUN(final String email, final String username, final String password) {
        // a listener for the db helper to use so it knows what to do after it queries
        // the database to see if a user already exists with the given credentials
        OnCompleteListener<QuerySnapshot> createAccount = new  OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean isEmpty = true;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        isEmpty = false;
                    }
                    if(isEmpty) {   // if that username is not in use already
                        accWarning.setVisibility(View.INVISIBLE);   // make warning invisible
                        // Query user collection to see if user email already exists
                        tryCreateAccountEmail(email, username, password);
                    } else {    // that username is already in use
                        accWarning.setVisibility(View.VISIBLE); // make warning visible
                        accWarning.setText("That username is already in use!");
                    }
                } else {
                    Log.d("query_fail", "Error getting documents: ", task.getException());
                }
            }
        };
        // ask dbHelper to execute the contents of our listener when it is done querying the
        // db to check the user's username
        MainActivity.dbHelper.validateUsername(createAccount, username);
    }

    /**
     * called when user clicks create account, if the email is in use, show warning, else
     * create the new account
     */
    private void tryCreateAccountEmail(final String email, final String username, final String password) {
        // a listener for the db helper to use so it knows what to do after it queries
        // the database to see if a user already exists with the given email
        OnCompleteListener<QuerySnapshot> createAccount = new  OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean isEmpty = true;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        isEmpty = false;
                    }
                    if(isEmpty) {   // if that email is not in use already
                        accWarning.setVisibility(View.INVISIBLE);   // make warning invisible
                        User newUser = new User(email, username, password); // Create user
                        MainActivity.dbHelper.pushUserToDB(newUser);    // push new user to db
                        saveLoginInfo(username, password);  // save login info
                        setLogInThenView(); // log in and go to account
                    } else {
                        accWarning.setVisibility(View.VISIBLE); // make warning visible
                        accWarning.setText("That email is already in use!");
                    }
                } else {
                    Log.d("query_fail", "Error getting documents: ", task.getException());
                }
            }
        };
        // ask dbHelper to execute the contents of our listener when it is done querying the
        // db to check the user's email
        MainActivity.dbHelper.validateEmail(createAccount, email);
    }
}
