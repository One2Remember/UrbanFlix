package com.example.myurbanflix;

import com.google.firebase.firestore.PropertyName;

/**
 * Defines a Movie Review for the purpose of pushing a review remotely to the database. The DB
 * is able to parse Java objects, and thus we pass to it review objects generated in the
 * CreateReviewActivity for it to parse and use to populate a firestore document containing the
 * related data.
 */
public class  MovieReview {
    /**
     * For holding the review's movie name
     */
    public String movieName;
    /**
     * For holding the review's movie name (lowercase - for ignoring case)
     */
    public String movieNameLower;
    /**
     * for holding the username of the author of the review
     */
    public String userName;
    /**
     * for holding the date that the review was created by the user
     */
    public String dateCreated;
    /**
     * for holding the body of the review
     */
    public String contents;
    /**
     * for holding the title of the review
     */
    public String reviewTitle;
    /**
     * for holding the number of upvotes in the review
     */
    public int upvotes;
    /**
     * fod holding the number of downvotes in the review
     */
    public int downvotes;
    /**
     * Currently a redundant field used to try to solve an odd (but unimportant) bug with the
     * database representation of a review. It can be ignored for now, as it does not affect
     * the UX at all.
     */
    String user;

    /**
     * No argument constructor to allow for deserialization
     */
    public MovieReview() {}

    /**
     * Normal constructor takes all relevant string fields pulled from create review activity and
     * uses them to populate the internal fields. Additionally, sets upvotes to 1 (assumes user will
     * upvote their own review) and downvotes to 0. Keys are provided by the database
     * @param revName
     * @param mName
     * @param un
     * @param date
     * @param cont
     */
    public MovieReview(String revName, String mName, String un, String date, String cont) {
        reviewTitle = revName;
        movieName = mName;
        movieNameLower = movieName.toLowerCase();
        userName = un;
        dateCreated = date;
        contents = cont;
        upvotes = 1;
        downvotes = 0;
        user = un;  // this field is currently semi-redundant
    }

    /**
     * adds an upvote to the review
     */
    public void upVote() {
        upvotes++;
    }
    /**
     * removes an upvote for the review
     */
    public void removeUpVote() {upvotes--;}

    /**
     * adds a downvote to the reivew
     */
    public void downVote() {
        downvotes++;
    }

    /**
     * removes a downvote from the review
     */
    public void removeDownVote() {downvotes--; }

    /**
     * @return review title
     */
    @PropertyName("reviewTitle")
    public String getReviewTitle() {
        return this.reviewTitle;
    }

    /**
     * @return name of movie
     */
    @PropertyName("movieName")
    public String getMovieName() {
        return this.movieName;
    }

    /**
     * @return lowercase version of movie name
     */
    @PropertyName("movieNameLower")
    public String getMovieNameLower() {
        return this.movieNameLower;
    }

    /**
     * @return username of review author
     */
    @PropertyName("userName")
    public String getUserName() {
        return this.userName;
    }

    /**
     * @return the date this object was created by the application
     */
    @PropertyName("dateCreated")
    public String getDateCreated() {
        return this.dateCreated;
    }

    /**
     * @return movie review contents
     */
    @PropertyName("contents")
    public String getContents() {
        return this.contents;
    }

    /**
     * @return number of upvotes
     */
    @PropertyName("upvotes")
    public int getUpvotes() {
        return this.upvotes;
    }

    /**
     * @return number of downvotes
     */
    @PropertyName("downvotes")
    public int getDownvotes() {
        return this.downvotes;
    }

    /**
     * @return user field (currently redundant, can ignore for now)
     */
    @PropertyName("user")
    public String getUser() {
        return this.user;
    }
}
