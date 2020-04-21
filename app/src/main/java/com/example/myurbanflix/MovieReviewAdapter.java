package com.example.myurbanflix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * This adapter is used for all recycler views in order to inflate movie_review_views and
 * populate them with data from our firestore query. Additional functionality allows
 * for recycler to enable/disable upvote downvote buttons for particular views, such as
 * the ViewAccountActivity, which we do not want to be able to mutate reviews
 */
public class MovieReviewAdapter extends FirestoreAdapter<MovieReviewAdapter.ViewHolder> {
    /**
     * local field for storing if buttons should be enabled
     */
    private boolean enable_buttons;

    /**
     * Constructor which initializes the moviereviewadapter
     * @param query the firestore query which will be used to populate the view
     * @param EnableButtons tells adapter whether buttons should be enabled or not
     */
    public MovieReviewAdapter(Query query, boolean EnableButtons) {
        super(query);
        enable_buttons = EnableButtons;
    }

    /**
     * when a view holder is created, this takes its parent inflator and returns a new viewholder
     * containing the information needed to inflate the view
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.movie_review_view, parent, false));
    }

    /**
     * when view holder is bound, gives holder document snapshot as well as a boolean value telling
     * it whether it needs to enable the buttons on that view (usually based on user login status)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), enable_buttons);
    }

    /**
     * This subclass is used in order to take empty views, inflate them with data from a document
     * within a query (in our case), and pass them back to the adapter
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * holds a handle to the textview storing the movie name
         */
        public TextView movieName;
        /**
         * holds a handle to the textview storing the review contents
         */
        public TextView reviewContents;
        /**
         * holds a handle to the textview storing the review author
         */
        public TextView reviewAuthor;
        /**
         * holds a handle to the textview storing the review title
         */
        public TextView reviewTitle;
        /**
         * holds a handle to the textview storing the date
         */
        public TextView date;
        /**
         * holds a handle to the textview storing the value of upvotes - downvotes
         */
        public TextView upValue;
        /**
         * holds a handle to the upvote button
         */
        public ImageButton upButton;
        /**
         * holds a handle to the downvote button
         */
        public ImageButton downButton;

        /**
         * constructor takes our view to be inflated for the recyclerview, grabs all the necessary
         * elements on the page for easy reference throughout the rest of the adapter
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            movieName = itemView.findViewById(R.id.movie_name);
            reviewContents = itemView.findViewById(R.id.review_contents);
            reviewAuthor = itemView.findViewById(R.id.author_un);
            reviewTitle = itemView.findViewById(R.id.review_title);
            date = itemView.findViewById(R.id.date_created);
            upValue = itemView.findViewById(R.id.num_upvotes);
            upButton = itemView.findViewById(R.id.upvote_button);
            downButton = itemView.findViewById(R.id.downvote_button);
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
            reviewAuthor.setText(review.getUserName());
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
            // set appropriate buttons to blue as needed
            if(loggedIn) {
                if(upvoteValue == MainActivity.UPVOTED) {    // set upvote button to green
                    upButton.setColorFilter(Color.argb(255,155,250,50));
                }
                else if (upvoteValue == MainActivity.DOWNVOTED) {   // set downvote button orange
                    downButton.setColorFilter(Color.argb(180,255,150,0));
                }
            }

            // enable/disable upButton based on if user is logged in
            if(loggedIn) {
                upButton.setEnabled(true);
                downButton.setEnabled(true);
            }
            else {
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            }

            /**
             * sets heavily loaded onclick listener to upvote button to behave like a reddit
             * upvote button:
             *    if upvoted prior, removes upvote
             *    elif downvoted prior, removes downvote and changes to upvote
             *    else adds an upvote
             * in all cases this updates shared preferences (to disallow repeat votes) and updates
             * the database appropriately for all three cases
             */
            upButton.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    // grab a reference to our review for easy field checking
                    MovieReview review = snapshot.toObject(MovieReview.class);
                    if(upvoted) {   // if review was previously upvoted
                        // update the database to remove an upvote
                        MainActivity.dbHelper.updateVotes("reviews", review_id, "upvotes", "DECREASE");
                        // set shared preference so there is no vote
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.remove(review_id);
                        prefEditor.apply();
                    }
                    else {  // review was not previously upvoted
                        if(downvoted) { // update the database to remove a downvote
                            MainActivity.dbHelper.updateVotes("reviews", review_id, "downvotes", "DECREASE");
                        }
                        // update the database to add an upvote
                        MainActivity.dbHelper.updateVotes("reviews", review_id, "upvotes", "INCREASE");
                        // set shared preferences so it is upvoted
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.putInt(review_id, MainActivity.UPVOTED);
                        prefEditor.apply();
                    }
                }
            });

            /**
             * sets heavily loaded onclick listener to downvote button to behave like a reddit
             * downvote button:
             *    if downvoted prior, removes downvote
             *    elif upvoted prior, removes upvote and changes to downvote
             *    else adds a downvote
             * in all cases this updates shared preferences (to disallow repeat votes) and updates
             * the database appropriately for all three cases
             */
            downButton.setOnClickListener( new View.OnClickListener() {
                // grab a reference to our review for easy field checking
                MovieReview review = snapshot.toObject(MovieReview.class);
                public void onClick(View v) {
                    // if item was previously downvoted
                    if(downvoted) {
                        // update the database to remove a downvote
                        MainActivity.dbHelper.updateVotes("reviews", review_id, "downvotes", "DECREASE");
                        // set shared preference so there is no vote
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.remove(review_id);
                        prefEditor.apply();
                    }
                    else {  // item was not downvoted
                        if(upvoted) {   // update the database to remove an upvote
                            MainActivity.dbHelper.updateVotes("reviews", review_id, "upvotes", "DECREASE");
                        }
                        // update the database to add a downvote
                        MainActivity.dbHelper.updateVotes("reviews", review_id, "downvotes", "INCREASE");
                        // set shared preferences so it is downvoted
                        SharedPreferences.Editor prefEditor = myPrefs.edit();
                        prefEditor.putInt(review_id, MainActivity.DOWNVOTED);
                        prefEditor.apply();
                    }
                }
            });
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
