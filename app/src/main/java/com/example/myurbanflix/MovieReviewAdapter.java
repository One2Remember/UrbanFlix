package com.example.myurbanflix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * This adapter is used for all recycler views in order to inflate movie_review_views and
 * populate them with data from our firestore query. Additional functionality allows
 * for recycler to enable/disable upvote downvote buttons for particular views, such as
 * the ViewAccountActivity, which we do not want to be able to mutate reviews
 */
public class MovieReviewAdapter extends FirestoreAdapter<MovieReviewAdapter.ViewHolder> {

    private boolean enable_buttons; // local field for storing if buttons should be enabled

    /**
     * Constructor which initializes the moviereviewadapter
     * @param query the firestore query which will be used to populate the view
     * @param EnableButtons tells adapter whether buttons should be enabled or not
     */
    public MovieReviewAdapter(Query query, boolean EnableButtons) {
        super(query);
        enable_buttons = EnableButtons;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.movie_review_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), enable_buttons);
    }

    /**
     * grabs handles to local textviews
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView movieName;
        public TextView reviewContents;
        public TextView reviewAuthor;
        public TextView reviewTitle;
        public TextView date;
        public TextView upValue;
        public ImageButton upButton;
        public ImageButton downButton;

        public ViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.movie_name);
            reviewContents = (TextView) itemView.findViewById(R.id.review_contents);
            reviewAuthor = (TextView) itemView.findViewById(R.id.author_un);
            reviewTitle = (TextView) itemView.findViewById(R.id.review_title);
            date = (TextView) itemView.findViewById(R.id.date_created);
            upValue = (TextView) itemView.findViewById(R.id.num_upvotes);
            upButton = (ImageButton) itemView.findViewById(R.id.upvote_button);
            downButton = (ImageButton) itemView.findViewById(R.id.downvote_button);
        }

        /**
         * binds contents from a particular document snapshot to a view which will be used to
         * inflate the recycler view
         * @param snapshot is the document snapshot containing the data of a single db object
         * @param enable_buttons tells binder whether or not to enable buttons
         */
        public void bind(final DocumentSnapshot snapshot, final boolean enable_buttons) {
            MovieReview review = snapshot.toObject(MovieReview.class);
            Resources resources = itemView.getResources();

            movieName.setText(review.getMovieName());
            reviewContents.setText(review.getContents());
            reviewAuthor.setText(review.getUser());
            reviewTitle.setText(review.getReviewTitle());
            date.setText(review.getDateCreated().substring(0, 10));  // truncate date
            upValue.setText(formatInt(review.getUpvotes() - review.getDownvotes()));
            // set button onclick functionality
            if(enable_buttons) {
                setButtonFunctionality(snapshot);
            } else {
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            }
        }

        /**
         * does the heavy lifting of adding on-click listeners which update the database and
         * set the colors of the buttons based on user input
         * @param snapshot the same document snapshot is passed through
         */
        public void setButtonFunctionality(final DocumentSnapshot snapshot) {
            // Check if user is logged in
            Context applicationContext = MainActivity.getContextOfApplication();
            final SharedPreferences myPrefs = applicationContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
            boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);

            final String review_id = snapshot.getId();

            // check user prefs to see if this activity has been upvoted/downvoted already
            int upvoteValue = myPrefs.getInt(review_id, MainActivity.NOTVOTED);
            final boolean upvoted = upvoteValue == MainActivity.UPVOTED;
            final boolean downvoted = upvoteValue == MainActivity.DOWNVOTED;
            // set both buttons to white by default
            upButton.setColorFilter(Color.argb(255,255,255,255));
            downButton.setColorFilter(Color.argb(255,255,255,255));
            if(upvoteValue == MainActivity.UPVOTED) {    // set upvote button to blue
                upButton.setColorFilter(Color.argb(255,13, 59, 195));
            }
            else if (upvoteValue == MainActivity.DOWNVOTED) {   // set downvote button blue
                downButton.setColorFilter(Color.argb(255,13, 59, 195));
            }

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~ UPBUTTON CODE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            // enable/disable upButton based on if user is logged in and if theyve upvoted already
            if(loggedIn) {
                upButton.setEnabled(true);
            }
            else {
                upButton.setEnabled(false);
            }
            /**
             * sets heavily loaded onclick listener to upvote button to behave like a reddit
             * upvote button
             */
            upButton.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    // grab a reference to our review for easy field checking
                    MovieReview review = snapshot.toObject(MovieReview.class);
                    if(upvoted) {   // if review was previously upvoted
                        // update the database to remove an upvote
                        updateVotes("reviews", review_id, "upvotes", review.getUpvotes() - 1);
                        // set shared preference so there is no vote
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.remove(review_id);
                        prefEditor.apply();
                    }
                    else {  // review was not previously upvoted
                        if(downvoted) { // update the database to remove a downvote
                            updateVotes("reviews", review_id, "downvotes", review.getDownvotes() - 1);
                        }
                        // update the database to add an upvote
                        updateVotes("reviews", review_id, "upvotes", review.getUpvotes() + 1);
                        // set shared preferences so it is upvoted
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.putInt(review_id, MainActivity.UPVOTED);
                        prefEditor.apply();
                    }
                }
            });

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~ DOWNBUTTON CODE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            // enable/disable downButton based on if user is logged in and if theyve downvoted already
            if(loggedIn) {
                downButton.setEnabled(true);
            }
            else {
                downButton.setEnabled(false);
            }
            /**
             * sets heavily loaded onclick listener to downvote button to behave like a reddit
             * downvote button
             */
            downButton.setOnClickListener( new View.OnClickListener() {
                // grab a reference to our review for easy field checking
                MovieReview review = snapshot.toObject(MovieReview.class);
                public void onClick(View v) {
                    // if item was previously downvoted
                    if(downvoted) { // update the database to remove a downvote
                        updateVotes("reviews", review_id, "downvotes", review.getDownvotes() - 1);
                        // set shared preference so there is no vote
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.remove(review_id);
                        prefEditor.apply();
                    }
                    else {  // item was not downvoted
                        if(upvoted) {   // update the database to remove an upvote
                            updateVotes("reviews", review_id, "upvotes", review.getUpvotes() - 1);
                        }
                        // update the database to add a downvote
                        updateVotes("reviews", review_id, "downvotes", review.getDownvotes() + 1);
                        // set shared preferences so it is downvoted
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.putInt(review_id, MainActivity.DOWNVOTED);
                        prefEditor.apply();
                    }
                }
            });
        }

        /**
         * Set the field 'field' of the document 'doc_key', in collection 'collection' to 'new_value
         * @param collection db connection to modify
         * @param doc_key document in db to modify
         * @param field field in document to modify
         * @param new_value new value to modify field
         */
        public void updateVotes(String collection, String doc_key, String field, int new_value) {
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = mFirestore.collection(collection).document(doc_key);
            // Set the field 'field' of the document 'doc_key' to 'new_value
            docRef
                    .update(field, new_value)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("success", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("failure", "Error updating document", e);
                        }
                    });
        }


        /**
         * get the value of field 'field' of the document 'doc_key', in collection 'collection'
         * @param collection collection to query
         * @param doc_key document of interest
         * @param field field of interest
         * @return value of field
         * @throws InterruptedException
         */
        public String getFieldValue(String collection, String doc_key, final String field) throws InterruptedException {
            final String[] fieldValue = {""};
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = mFirestore.collection(collection).document(doc_key);
            Log.d("LOGGER", "docRefID is:" + docRef.getId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d("LOGGER", "task complete");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            Log.d("LOGGER", "Got in here");
                            fieldValue[0] = document.getString(field);
                        } else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            });
            while(fieldValue[0].equals("")) {
                Log.d("LOGGER", "still waiting");
                Thread.sleep(2000);
            }
            return fieldValue[0];
        }

        /**
         * for providing a neatly formatted string <= 5 characters long to fit next to button
         * @param x number to fit
         * @return String format version of x
         */
        public String formatInt(int x) {
            if(x < 1000) { return String.valueOf(x); }
            if(x < 100000) { return String.valueOf((float)(x / 100) / 10.0) + 'k'; }
            if(x < 1000000) { return String.valueOf((x / 1000)) + 'k'; }
            return String.valueOf(x);
        }
    }
}
