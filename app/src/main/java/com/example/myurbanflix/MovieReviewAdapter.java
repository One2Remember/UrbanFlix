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

public class MovieReviewAdapter extends FirestoreAdapter<MovieReviewAdapter.ViewHolder> {

    private boolean enable_buttons;

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView movieName;
        public TextView reviewContents;
        public TextView reviewAuthor;
        public TextView reviewTitle;
        public TextView date;
        public TextView upValue;
        public TextView downValue;
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
            downValue = (TextView) itemView.findViewById(R.id.num_downvotes);
            upButton = (ImageButton) itemView.findViewById(R.id.upvote_button);
            downButton = (ImageButton) itemView.findViewById(R.id.downvote_button);
        }

        public void bind(final DocumentSnapshot snapshot, final boolean enable_buttons) {
            MovieReview review = snapshot.toObject(MovieReview.class);
            Resources resources = itemView.getResources();

            movieName.setText(review.getMovieName());
            reviewContents.setText(review.getContents());
            reviewAuthor.setText(review.getUserName());
            reviewTitle.setText(review.getReviewTitle());
            date.setText(review.getDateCreated().substring(0, 10));  // truncate date
            upValue.setText(formatInt(review.getUpvotes()));
            downValue.setText(formatInt(review.getDownvotes()));
            // set button onclick functionality
            if(enable_buttons) {
                setButtonFunctionality(snapshot);
            } else {
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            }
        }

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

            // set default button colors
            if(upvoted) {
                upButton.setColorFilter(Color.argb(255,13, 59, 195));
            }
            if(downvoted) {
                downButton.setColorFilter(Color.argb(255,13, 59, 195));
            }

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~ UPBUTTON CODE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            // enable/disable upButton based on if user is logged in and if theyve upvoted already
            if(loggedIn && !upvoted) {
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
                    // if item was previously downvoted
                    if(downvoted) {
                        downButton.setEnabled(true);   // enable the upvote button
                        downButton.setClickable(true);
//                        try {
//                            curDownvotes = Integer.parseInt(getFieldValue("reviews", review_id, "downvotes"));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        // set tint of downvoted button to white (default)
                        // set downvote button to white as it is now clickable
                        downButton.setColorFilter(Color.argb(255,255,255,255));
                        // update the database
                        updateVotes("reviews", review_id, "downvotes", review.getDownvotes() - 1);
                    }
                    // set shared preferences so it is upvoted
                    SharedPreferences.Editor prefEditor = myPrefs.edit();
                    prefEditor.putInt(review_id, MainActivity.UPVOTED);
                    prefEditor.apply();
                    upButton.setEnabled(false);  // disable the upvote button
//                    try {
//                        curUpvotes = Integer.parseInt(getFieldValue("reviews", review_id, "upvotes"));
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    // set tint of upvoted button to blue
                    // set upvote to blue as it is now unclickable
                    upButton.setColorFilter(Color.argb(255,13, 59, 195));
                    // update the database
                    updateVotes("reviews", review_id, "upvotes", review.getUpvotes() + 1);
                }
            });

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~ DOWNBUTTON CODE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            // enable/disable downButton based on if user is logged in and if theyve downvoted already
            if(loggedIn && !downvoted) {
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
                    // if item was previously upvoted
                    if(upvoted) {
                        upButton.setEnabled(true);   // enable the upvote button
                        upButton.setClickable(true);
//                        try {
//                            curUpvotes = Integer.parseInt(getFieldValue("reviews", review_id, "upvotes"));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        // set tint of downvoted button to white (default)
                        // set the upvote button to white as it is now clickable
                        upButton.setColorFilter(Color.argb(255,255,255,255));
                        // update the data in our database
                        updateVotes("reviews", review_id, "upvotes", review.getUpvotes() - 1);
                    }
                    // set shared preferences so it is downvoted
                    SharedPreferences.Editor prefEditor = myPrefs.edit();
                    prefEditor.putInt(review_id, MainActivity.DOWNVOTED);
                    prefEditor.apply();
                    downButton.setEnabled(false);  // disable the downvote button
//                        try {
//                            curDownvotes = Integer.parseInt(getFieldValue("reviews", review_id, "downvotes"));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    // set downvoted color to blue
                    // set downvote button to blue as it is now unclickable
                    downButton.setColorFilter(Color.argb(255,13, 59, 195));
                    // update the data in our database
                    updateVotes("reviews", review_id, "downvotes", review.getDownvotes() + 1);
                }
            });
        }
        // Set the field 'field' of the document 'doc_key', in collection 'collection' to 'new_value
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

        // get the value of field 'field' of the document 'doc_key', in collection 'collection'
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
