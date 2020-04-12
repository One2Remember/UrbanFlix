package com.example.myurbanflix;

import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

// Provides an adapter which takes a java List of MovieReview objects and turns it into
// a form that a recycler view can use to populate its own list
public class ReviewAdapterNoButton extends ReviewAdapter{

    public ReviewAdapterNoButton(List<MovieReview> movieList) {
        super(movieList);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // This version is similar to that from superclass ReviewAdapter except it does not add
    // listeners to the up/down buttons and instead disables them. This is good for when user
    // is viewing profile, or if user is not logged in, and we don't want them to be able to
    // manipulate data
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        // Get the data model based on position
        final MovieReview mReview = myMovieList.get(position);

        // Set item views based on whats inside each movie review
        final TextView mName = viewHolder.movieName;
        final TextView revName = viewHolder.reviewTitle;
        final TextView revContents = viewHolder.reviewContents;
        final TextView revAuthor = viewHolder.reviewAuthor;
        final TextView dateView = viewHolder.date;
        Button ubutton = viewHolder.upButton;
        Button dbutton = viewHolder.downButton;

        revName.setText(mReview.reviewTitle);
        mName.setText(mReview.movieName);   // set text for movie name text view
        revContents.setText(mReview.contents);  // set text for review contents
        revAuthor.setText(mReview.userName);    // set text for review author's username
        dateView.setText(mReview.dateCreated);  // set text for date created

        // set text for upvote button to number of upvotes
        ubutton.setText("+ " + String.valueOf(mReview.upvotes));
        ubutton.setEnabled(false);

        // set text for downvote button to number of downvotes
        dbutton.setText("- " + String.valueOf(mReview.downvotes));
        dbutton.setEnabled(false);
    }
}
