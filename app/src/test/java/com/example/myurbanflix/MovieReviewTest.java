package com.example.myurbanflix;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Suite for the MovieReview class, tests all methods and asserts they perform actions as
 * expected
 */
public class MovieReviewTest {
    /**
     * a dummy review for testing
     */
    MovieReview testReview;

    /**
     * initializes a dummy review for the purpose of testing
     */
    @Before
    public void initReview() {
        testReview = new MovieReview("Best Movie", "Happy Feet", "One2Remember",
                "04/17/2020 15:43:47", "I enjoyed this film");
    }

    /**
     * assert that review title from getter matches value given in constructor
     */
    @Test
    public void getReviewTitle() {
        assertEquals(testReview.getReviewTitle(), "Best Movie");
    }

    /**
     * assert that movie title from getter matches value given in constructor
     */
    @Test
    public void getMovieName() {
        assertEquals(testReview.getMovieName(), "Happy Feet");
    }

    /**
     * assert that lowercase movie title from getter matches value initialized in constructor
     */
    @Test
    public void getMovieNameLower() {
        assertEquals(testReview.getMovieNameLower(), "happy feet");
    }

    /**
     * assert that username from getter matches value given in constructor
     */
    @Test
    public void getUserName() {
        assertEquals(testReview.getUserName(), "One2Remember");
    }
    /**
     * assert that date created from getter matches value given in constructor
     */
    @Test
    public void getDateCreated() {
        assertEquals(testReview.getDateCreated(), "04/17/2020 15:43:47");
    }
    /**
     * assert that review contents from getter matches value given in constructor
     */
    @Test
    public void getContents() {
        assertEquals(testReview.getContents(), "I enjoyed this film");
    }
    /**
     * assert that # of upvotes from getter matches default value of 1 for a new review
     */
    @Test
    public void getUpvotes() {
        assertEquals(testReview.getUpvotes(), 1);
    }
    /**
     * assert that # of downvotes from getter matches default value of 0 for a new review
     */
    @Test
    public void getDownvotes() {
        assertEquals(testReview.getDownvotes(), 0);
    }
    /**
     * assert that user field from getter matches value of username (un)
     */
    @Test
    public void getUser() {
        assertEquals(testReview.getUser(), "One2Remember");
    }
}