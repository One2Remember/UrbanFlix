package com.example.myurbanflix;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Used to access the firestore database from one central location. Gives clients Queries from
 * database matching particular search parameters they desire, and appropriately logs in
 * users for the client (or keeps them logged out if they have invalid credentials).
 */
public class FirestoreHelper {
    /**
     * A handle to the programs connection to the firestore
     */
    private FirebaseFirestore mFirestore;

    /**
     * Initialize the db helper's connection to the firestore (where our database is hosted).
     * This constructor is only called once, in main activity, to initialize a single public static
     * instance of the FirestoreHelper that other classes can use freely
     */
    public FirestoreHelper() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    /**
     * For any client method which wants a direct handle to the database
     * @return a handle to the database
     */
    public FirebaseFirestore getDBInstance() {
        return this.mFirestore;
    }

    /**
     * Performs the actions dictated by a listener when a query to the database is complete.
     * The query searches the database for a user with matching username or email and password.
     * If the query is non-empty (the user exists), the on complete listener will log them in,
     * otherwise, it will display a warning to the user
     * @param loginInIfLegal - the listener that will be called when this query is complete
     * @param un_or_pw - the input username or email from the text field in login
     * @param password - the password from the text field in login
     */
    public void logInUser(OnCompleteListener<QuerySnapshot> loginInIfLegal, String un_or_pw, String password) {
        final CollectionReference users = mFirestore.collection("users");
        if(un_or_pw.contains("@")) {
            users
                    .whereEqualTo("email", un_or_pw)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnCompleteListener(loginInIfLegal);
        } else {
            users
                    .whereEqualTo("username", un_or_pw)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnCompleteListener(loginInIfLegal);
        }
    }

    public void updateUserPrefs(OnCompleteListener<QuerySnapshot> updatePrefs, String username,
                                String dateCreated) {
        mFirestore.collection("reviews")
                .whereEqualTo("userName", username)
                .whereEqualTo("dateCreated", dateCreated)
                .get()
                .addOnCompleteListener(updatePrefs);
    }

    /**
     * pushes a MovieReview object to the database
     * @param review - to push
     */
    public void pushReviewToDB(MovieReview review) {
        mFirestore.collection("reviews").add(review);
    }

    /**
     * pushes a User object to the database
     * @param user - to push
     */
    public void pushUserToDB(User user) {
        mFirestore.collection("users").add(user);
    }

    public void validateUsername(OnCompleteListener<QuerySnapshot> createAccIfLegal, String username) {
        // Query user collection to see if username already exists
        mFirestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(createAccIfLegal);
    }

    public void validateEmail(OnCompleteListener<QuerySnapshot> createAccIfLegal, String email) {
        // Query user collection to see if user email already exists
        mFirestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(createAccIfLegal);
    }

    /**
     * Used to pass query to caller based on the data the user needs from the Firestore
     * @param collection - the collection the user wants to query from
     * @param fieldToSort - the field the user wants to sort by
     * @param direction - the direction the user wants it sorted by (ASCENDING or DESCENDING)
     * @param sizeLimit - the max number of elements to include in case search is huge
     * @return the query containing the results of this database access
     */
    public Query getGeneralRecyclerQuery(String collection, String fieldToSort,
                                         Query.Direction direction, int sizeLimit ) {
        return mFirestore.collection(collection)
                .orderBy(fieldToSort, direction)
                .limit(sizeLimit);
    }

    /**
     * Used to pass query to caller based on the data the user needs from the Firestore that matches
     * a SPECIFIC field value
     * @param collection - the collection the user wants to query from
     * @param fieldToMatch - the field we want to match exactly
     * @param fieldValue - the value of the field that must match
     * @param fieldToSort - the field the user wants to sort by
     * @param direction - the direction the user wants it sorted by (ASCENDING or DESCENDING)
     * @param sizeLimit - the max number of elements to include in case search is huge
     * @return
     */
    public Query getMatchingRecyclerQuery(String collection, String fieldToMatch, String fieldValue,
                                          String fieldToSort, Query.Direction direction, int sizeLimit) {
        return mFirestore.collection(collection).whereEqualTo(fieldToMatch, fieldValue)
                .orderBy(fieldToSort, direction).limit(sizeLimit);
    }
}
