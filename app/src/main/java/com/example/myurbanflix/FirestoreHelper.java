package com.example.myurbanflix;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Used to access the firestore database from one central location; Gives clients Queries from
 * database matching particular search parameters they desire, and appropriately logs in
 * users for the client (or keeps them logged out if they have invalid credentials).
 */
public class FirestoreHelper {
    /**
     * A handle to the programs connection to the firestore
     */
    private FirebaseFirestore mFirestore;

    /**
     * Initialize the db helper's connection to the firestore (where our database is hosted);
     * This constructor is only called once, in main activity, to initialize a single public static
     * instance of the FirestoreHelper that other classes can use freely
     */
    public FirestoreHelper() {
        mFirestore = FirebaseFirestore.getInstance();
    }


    /**
     * get the value of field 'field' of the document 'doc_key', in collection 'collection',
     * then use those values to query the database for a review, then either upvotes or downvotes
     * that review based on inc_or_dec
     * @param collection collection to query
     * @param doc_key document of interest
     * @param field field of interest
     * @param inc_or_dec is either "INCREASE" OR "DECREASE" based on if we want to upvote or downvote
     */
    public void updateVotes(String collection, String doc_key, final String field, final String inc_or_dec){
        final DocumentReference docRef = mFirestore.collection(collection).document(doc_key);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if(inc_or_dec.equals("INCREASE")) {
                            // update the value field to value + 1
                            docRef
                                    .update(field, document.getLong(field) + 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("LOGGER", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("LOGGER", "Error updating document", e);
                                        }
                                    });
                        } else {
                            // update the value field to value - 1
                            docRef
                                    .update(field, document.getLong(field) - 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("LOGGER", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("LOGGER", "Error updating document", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Performs the actions dictated by a listener when a query to the database is complete;
     * The query searches the database for a user with matching username or email and password;
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

    /**
     * Called in create review activity, takes a listener which will update user preferences once
     * this db query is completed, finding the review that was just pushed to the db, and giving
     * back the review's key so that it can be stored in sharedpreferences
     * @param updatePrefs - listener that will update preferences
     * @param username - user's un
     * @param dateCreated - the exact date and time it was created
     */
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

    /**
     * Checks if a given username is already in use and calls the createAccIfLegal listener
     * defined in CreateAccountActivity
     * @param createAccIfLegal - listener to call upon query completion
     * @param username - username to validate
     */
    public void validateUsername(OnCompleteListener<QuerySnapshot> createAccIfLegal, String username) {
        // Query user collection to see if username already exists
        mFirestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(createAccIfLegal);
    }

    /**
     * Checks if a given email is already in use and calls the createAccIfLegal listener
     * defined in CreateAccountActivity
     * @param createAccIfLegal - listener to call upon query completion
     * @param email - email to validate
     */
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
