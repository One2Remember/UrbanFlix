package com.example.myurbanflix;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Provides an adapter which takes a java List of MovieReview objects and turns it into
// a form that a recycler view can use to populate its own list
public class ReviewAdapterNoButton extends RecyclerView.Adapter<ReviewAdapterNoButton.MyViewHolder>{
    Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView movieName;
        public TextView reviewContents;
        public TextView reviewAuthor;
        public Button upButton;
        public Button downButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.movie_name);
            reviewContents = (TextView) itemView.findViewById(R.id.review_contents);
            reviewAuthor = (TextView) itemView.findViewById(R.id.author_un);
            upButton = (Button) itemView.findViewById(R.id.upvote_button);
            downButton = (Button) itemView.findViewById(R.id.downvote_button);
        }
    }

    // Store a member variable for the contacts
    private List<MovieReview> myMovieList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewAdapterNoButton(List<MovieReview> movieList) {
        myMovieList = movieList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapterNoButton.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.movie_review_view, parent, false);

        // Return a new holder instance
        MyViewHolder viewHolder = new MyViewHolder(movieView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        // Get the data model based on position
        final MovieReview mReview = myMovieList.get(position);

        // Set item views based on whats inside each movie review
        final TextView mName = viewHolder.movieName;
        final TextView revContents = viewHolder.reviewContents;
        final TextView revAuthor = viewHolder.reviewAuthor;
        Button ubutton = viewHolder.upButton;
        Button dbutton = viewHolder.downButton;

        mName.setText(mReview.movieName);   // set text for movie name text view
        revContents.setText(mReview.contents);  // set text for review contents
        revAuthor.setText(mReview.userName);    // set text for review author's username

        // set text for upvote button to number of upvotes
        ubutton.setText(String.valueOf(mReview.upvotes));
        ubutton.setEnabled(false);

        // set text for downvote button to number of downvotes
        dbutton.setText(String.valueOf(mReview.downvotes));
        dbutton.setEnabled(false);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myMovieList.size();
    }
}
