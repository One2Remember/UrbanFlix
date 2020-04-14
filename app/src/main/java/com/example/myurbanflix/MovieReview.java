package com.example.myurbanflix;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class  MovieReview {
    public String movieName;
    public String movieNameLower;
    public String userName;
    public String dateCreated;
    public String contents;
    public String reviewTitle;
    public int upvotes;
    public int downvotes;

    // 2BeFixed
    String user;


    /** No argument constructor to allow for deserialization
     */
    public MovieReview() {}

    public MovieReview(String revName, String mName, String un, String date, String cont) {
        reviewTitle = revName;
        movieName = mName;
        movieNameLower = movieName.toLowerCase();
        userName = un;
        dateCreated = date;
        contents = cont;
        upvotes = 1;
        downvotes = 0;

        user = un;
    }

    public void upVote() {
        upvotes++;
    }

    public void removeUpVote() {upvotes--;}

    public void downVote() {
        downvotes++;
    }

    public void removeDownVote() {downvotes--; }

    @PropertyName("reviewTitle")
    // return review title
    public String getReviewTitle() {
        return this.reviewTitle;
    }

    @PropertyName("movieName")
    // return name of movie
    public String getMovieName() {
        return this.movieName;
    }

    @PropertyName("movieNameLower")
    public String getMovieNameLower() {
        return this.movieNameLower;
    }

    @PropertyName("userName")
    // return username of review author
    public String getUserName() {
        return this.userName;
    }

    @PropertyName("dateCreated")
    public String getDateCreated() {
        return this.dateCreated;
    }

    @PropertyName("contents")
    // return movie review contents
    public String getContents() {
        return this.contents;
    }

    @PropertyName("upvotes")
    // return number of upvotes
    public int getUpvotes() {
        return this.upvotes;
    }

    @PropertyName("downvotes")
    public int getDownvotes() {
        return this.downvotes;
    }

    // 2BeFixed
    @PropertyName("user")
    public String getUser() {
        return this.user;
    }
}
