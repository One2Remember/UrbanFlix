package com.example.myurbanflix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.Query;

import java.util.List;

// Provides an adapter which takes a java List of MovieReview objects and turns it into
// a form that a recycler view can use to populate its own list
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{
    private FirebaseFirestore mFirestore;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private View view;
        // each data item is just a string in this case
        public TextView movieName;
        public TextView reviewContents;
        public TextView reviewAuthor;
        public TextView reviewTitle;
        public TextView date;
        public Button upButton;
        public Button downButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.movie_name);
            reviewContents = (TextView) itemView.findViewById(R.id.review_contents);
            reviewAuthor = (TextView) itemView.findViewById(R.id.author_un);
            reviewTitle = (TextView) itemView.findViewById(R.id.review_title);
            date = (TextView) itemView.findViewById(R.id.date_created);
            upButton = (Button) itemView.findViewById(R.id.upvote_button);
            downButton = (Button) itemView.findViewById(R.id.downvote_button);
        }

        public void setMovieName(String movieName) {
            this.movieName.setText(movieName);
        }
    }

    // Store a member variable for the contacts
    protected List<MovieReview> myMovieList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewAdapter(List<MovieReview> movieList) {
        myMovieList = movieList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.movie_review_view, parent, false);

        // Return a new holder instance
        MyViewHolder viewHolder = new MyViewHolder(movieView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final MovieReview mReview = myMovieList.get(position);

        // Set item views based on whats inside each movie review
        final TextView mName = viewHolder.movieName;
        final TextView revName = viewHolder.reviewTitle;
        final TextView revContents = viewHolder.reviewContents;
        final TextView revAuthor = viewHolder.reviewAuthor;
        final TextView dateView = viewHolder.date;
        final Button ubutton = viewHolder.upButton;
        final Button dbutton = viewHolder.downButton;

        // for holding the key of the creator of the review
        final String[] id = new String[1];

        // make connection to database
        mFirestore = FirebaseFirestore.getInstance();

        // Check if user is logged in
        Context applicationContext = MainActivity.getContextOfApplication();
        final SharedPreferences myPrefs = applicationContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);

        // get a reference to our reviews collection in the firestore
        final CollectionReference reviewDB = mFirestore.collection("reviews");

        // Grab the id of this particular review from the database (will now be a String in id[0])
        reviewDB
                .whereEqualTo("userName", mReview.userName)
                .whereEqualTo("dateCreated", mReview.dateCreated)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("query_success", document.getId() + " => " + document.getData());
                                id[0] = document.getId();   // grab the key of the review
                            }
                        } else {
                            Log.d("query_fail", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // check user prefs to see if this activity has been upvoted/downvoted already
        int upvoteValue = myPrefs.getInt(id[0], MainActivity.NOTVOTED);
        final boolean upvoted = upvoteValue == MainActivity.UPVOTED;
        final boolean downvoted = upvoteValue == MainActivity.DOWNVOTED;

        revName.setText(mReview.reviewTitle);   // set text for review title
        mName.setText(mReview.movieName);       // set text for movie name text view
        revContents.setText(mReview.contents);  // set text for review contents
        revAuthor.setText(mReview.userName);    // set text for review author's username
        dateView.setText(mReview.dateCreated);  // set text for date created

        // set text for upvote button to number of upvotes
        ubutton.setText("+ " + mReview.upvotes);
        // enable/disable button based on if user is logged in and if theyve upvoted already
        if(loggedIn && !upvoted) {
            ubutton.setEnabled(true);
        }
        else {
            ubutton.setEnabled(false);
        }
        // attach on click listener for upvote button
        ubutton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                // if item was previously downvoted
                if(downvoted) {
                    mReview.removeDownVote();   // remove downvote from local item
                    dbutton.setEnabled(true);   // enable the upvote button
                    dbutton.setClickable(true);
                    // TODO: remove a downvote from the item in the database
                }
                mReview.upVote();   // modify our local version
                myPrefs.edit().putInt(id[0], MainActivity.UPVOTED); // update user preferences
                ubutton.setEnabled(false);  // disable the upvote button
                // TODO: add an upvote to the item on the database
                ReviewAdapter.this.notifyItemChanged(position); // tell adapter this item changed
                }
        });

        // set text for downvote button to number of downvotes
        dbutton.setText("- " + mReview.downvotes);
        // enable/disable button based on if user is logged in
        if(loggedIn && !downvoted) {
            dbutton.setEnabled(true);
        }
        else {
            dbutton.setEnabled(false);
        }
        // attack on click listener for downvote button
        dbutton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                // if item was previously upvoted
                if(upvoted) {
                    mReview.removeUpVote();   // remove upvote from local item
                    ubutton.setEnabled(true);   // enable the upvote button
                    // TODO: remove an upvote from the item in the database
                }
                mReview.downVote();   // modify our local version
                myPrefs.edit().putInt(id[0], MainActivity.DOWNVOTED);   // update user preferences
                dbutton.setEnabled(false);  // disable the downvote button
                ReviewAdapter.this.notifyItemChanged(position); // tell adapter this item changed
                // TODO: add a downvote to the item on the database
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myMovieList.size();
    }
}
