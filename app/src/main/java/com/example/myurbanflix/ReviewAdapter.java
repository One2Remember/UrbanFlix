package com.example.myurbanflix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Provides an adapter which takes a java List of MovieReview objects and turns it into
// a form that a recycler view can use to populate its own list
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
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

        // Check if user is logged in
        Context applicationContext = MainActivity.getContextOfApplication();
        SharedPreferences myPrefs = applicationContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        boolean loggedIn = myPrefs.getBoolean("LoggedIn", false);

        // Set item views based on whats inside each movie review
        final TextView mName = viewHolder.movieName;
        final TextView revName = viewHolder.reviewTitle;
        final TextView revContents = viewHolder.reviewContents;
        final TextView revAuthor = viewHolder.reviewAuthor;
        final TextView dateView = viewHolder.date;
        Button ubutton = viewHolder.upButton;
        Button dbutton = viewHolder.downButton;

        /*
        TODO: PULL DOWN FROM USER PREFERENCES THE USER'S USERNAME
        TODO: QUERY THE DB'S UPVOTE/DOWNVOTE TABLE FOR THE COMBINATION OF REVIEW KEY + USERNAME
        TODO: TO SEE IF THE USER HAS UPVOTED/DOWNVOTED ALREADY
        TODO: IF THEY HAVE UPVOTED OR DOWNVOTED (QUERY IS NOT NUL), USE BOOLEAN TO TRACK USER'S
        TODO: UPVOTE/DOWNVOTE STATUS. BASED ON THAT STATUS, ENABLE/DISABLE UPVOTE/DOWNVOTE
         */
        boolean upvoted = false;
        boolean downvoted = false;

        revName.setText(mReview.reviewTitle);
        mName.setText(mReview.movieName);   // set text for movie name text view
        revContents.setText(mReview.contents);  // set text for review contents
        revAuthor.setText(mReview.userName);    // set text for review author's username
        dateView.setText(mReview.dateCreated);  // set text for date created

        // set text for upvote button to number of upvotes
        ubutton.setText("+ " + String.valueOf(mReview.upvotes));
        // enable/disable button based on if user is logged in and if theyve upvoted already
        if(loggedIn && !upvoted) {
            ubutton.setEnabled(true);
        }
        else {
            ubutton.setEnabled(false);
        }
        // attach on click listener
        ubutton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: ASK UP/DOWN DB IF THIS USER HAS UPDATE REVIEW: KEY, USE TO SET BOOL
                boolean downvoted = false;  // TODO: PULL THIS INFO FROM UP/DOWN DB
                // TODO: SEND UPVOTE/DOWNVOTE DB INFO THAT THIS USER HAS UPVOTED REVIEW: KEY
                // TODO: SEND REVIEW DATABASE
                if(downvoted) {
                    mReview.removeDownVote();
                }
                mReview.upVote();   // modify our local version
                ReviewAdapter.this.notifyItemChanged(position); // tell adapter this item changed
                }
        });

        // set text for downvote button to number of downvotes
        dbutton.setText("- " + String.valueOf(mReview.downvotes));
        // enable/disable button based on if user is logged in
        if(loggedIn && !downvoted) {
            dbutton.setEnabled(true);
        }
        else {
            dbutton.setEnabled(false);
        }
        // attack on click listener
        dbutton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                /// TODO: ASK UP/DOWN DB IF THIS USER HAS UPDATE REVIEW: KEY, USE TO SET BOOL
                boolean upvoted = false;  // TODO: PULL THIS INFO FROM UP/DOWN DB
                // TODO: SEND UPVOTE/DOWNVOTE DB INFO THAT THIS USER HAS UPVOTED REVIEW: KEY
                // TODO: SEND REVIEW DATABASE
                if(upvoted) {
                    mReview.removeUpVote();
                }
                mReview.downVote();
                ReviewAdapter.this.notifyItemChanged(position); // tell adapter this item changed
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myMovieList.size();
    }
}
