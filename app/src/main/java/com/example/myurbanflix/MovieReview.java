package com.example.myurbanflix;

import java.util.ArrayList;

public class MovieReview {
    public String movieName;
    public String userName;
    public String dateCreated;
    public String contents;
    public int upvotes;
    public int downvotes;
    public boolean userVote;

    public MovieReview(String mName, String un, String date, String cont, boolean uVote) {
        movieName = mName;
        userName = un;
        dateCreated = date;
        contents = cont;
        upvotes = 0;
        downvotes = 0;
        userVote = uVote;
    }

    public void upVote() {
        upvotes++;
    }

    public void downVote() {
        downvotes++;
    }

    // THIS IS JUST FOR PROOF OF CONCEPT -- RETURNS A GENERIC ARRAYLIST OF MOVIE REVIEWS
    public static ArrayList<MovieReview> GenerateReviewList() {
        ArrayList<MovieReview> movieList = new ArrayList<MovieReview>();
        movieList.add(new MovieReview("Jaws", "gofurkle", "4/11/2020", "This movie sucks.", false));
        movieList.add(new MovieReview("Up", "gofurkle", "4/11/2020", "This movie rocks.", true));
        movieList.add(new MovieReview("Finding Nemo", "dpatelism", "4/11/2020", "My favorite movie.", true));
        movieList.add(new MovieReview("Finding Dory", "dpatelism", "4/11/2020", "Even better than the original.", true));
        movieList.add(new MovieReview("Lethal Weapon", "dpatelism", "4/11/2020", "Never seen it.", false));
        movieList.add(new MovieReview("Emoji Movie", "gofurkle", "4/11/2020", "Wow.", true));
        movieList.add(new MovieReview("Free Willy", "one2remember", "4/11/2020", "MJ is a treasure.", true));
        movieList.add(new MovieReview("Pan's Labyrinth", "one2remember", "4/11/2020", "GOAT", true));
        movieList.add(new MovieReview("Jaws2", "gofurkle", "4/11/2020", "This movie sucks.2", false));
        movieList.add(new MovieReview("Up2", "gofurkle", "4/11/2020", "This movie rocks.2", true));
        movieList.add(new MovieReview("Finding Nemo2", "dpatelism", "4/11/2020", "My favorite movie.2", true));
        movieList.add(new MovieReview("Finding Dory2", "dpatelism", "4/11/2020", "Even better than the original.2", true));
        movieList.add(new MovieReview("Lethal Weapon2", "dpatelism", "4/11/2020", "Never seen it.2", false));
        movieList.add(new MovieReview("Emoji Movie2", "gofurkle", "4/11/2020", "Wow.2", true));
        movieList.add(new MovieReview("Free Willy2", "one2remember", "4/11/2020", "MJ is a treasure.2", true));
        movieList.add(new MovieReview("Pan's Labyrinth2", "one2remember", "4/11/2020", "GOAT2", true));
        movieList.add(new MovieReview("Jaws3", "gofurkle", "4/11/2020", "This movie sucks.3", false));
        movieList.add(new MovieReview("Up3", "gofurkle", "4/11/2020", "This movie rocks.3", true));
        movieList.add(new MovieReview("Finding Nemo3", "dpatelism", "4/11/2020", "My favorite movie.3", true));
        movieList.add(new MovieReview("Finding Dory3", "dpatelism", "4/11/2020", "Even better than the original.3", true));
        movieList.add(new MovieReview("Lethal Weapon3", "dpatelism", "4/11/2020", "Never seen it.3", false));
        movieList.add(new MovieReview("Emoji Movie3", "gofurkle", "4/11/2020", "Wow.3", true));
        movieList.add(new MovieReview("Free Willy3", "one2remember", "4/11/2020", "MJ is a treasure.3", true));
        movieList.add(new MovieReview("Pan's Labyrinth3", "one2remember", "4/11/2020", "GOAT3", true));

        return movieList;
    }
}
