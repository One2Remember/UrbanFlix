package com.example.myurbanflix;

import java.util.ArrayList;

public class MovieReview {
    public String movieName;
    public String userName;
    public String dateCreated;
    public String contents;
    public String reviewTitle;
    public int upvotes;
    public int downvotes;
    public long key;
    public boolean userUpvoted;
    public boolean userDownvoted;

    public MovieReview(String revName, String mName, String un, String date, String cont, int key) {
        reviewTitle = revName;
        movieName = mName;
        userName = un;
        dateCreated = date;
        contents = cont;
        upvotes = 0;
        downvotes = 0;
        key = -1;
        userUpvoted = false;
        userDownvoted = false;
    }

    public void upVote() {
        upvotes++;
    }

    public void removeUpVote() {upvotes--;}

    public void downVote() {
        downvotes++;
    }

    public void removeDownVote() {downvotes--; }

    // THIS IS JUST FOR PROOF OF CONCEPT -- RETURNS A GENERIC ARRAYLIST OF MOVIE REVIEWS
    public static ArrayList<MovieReview> GenerateReviewList() {
        ArrayList<MovieReview> movieList = new ArrayList<MovieReview>();
        movieList.add(new MovieReview("Bad film", "Jaws", "gofurkle", "4/11/2020", "When a young woman is killed by a shark while skinny-dipping near the New England tourist town of Amity Island, police chief Martin Brody (Roy Scheider) wants to close the beaches, but mayor Larry Vaughn (Murray Hamilton) overrules him, fearing that the loss of tourist revenue will cripple the town. Ichthyologist Matt Hooper (Richard Dreyfuss) and grizzled ship captain Quint (Robert Shaw) offer to help Brody capture the killer beast, and the trio engage in an epic battle of man vs. nature.", 1));
        movieList.add(new MovieReview("Okay film", "Up", "gofurkle", "4/11/2020", "Carl Fredricksen, a 78-year-old balloon salesman, is about to fulfill a lifelong dream. Tying thousands of balloons to his house, he flies away to the South American wilderness. But curmudgeonly Carl's worst nightmare comes true when he discovers a little boy named Russell is a stowaway aboard the balloon-powered house. A Pixar animation.",2 ));
        movieList.add(new MovieReview("Great film", "Finding Nemo", "dpatelism", "4/11/2020", "Marlin (Albert Brooks), a clown fish, is overly cautious with his son, Nemo (Alexander Gould), who has a foreshortened fin. When Nemo swims too close to the surface to prove himself, he is caught by a diver, and horrified Marlin must set out to find him. A blue reef fish named Dory (Ellen DeGeneres) -- who has a really short memory -- joins Marlin and complicates the encounters with sharks, jellyfish, and a host of ocean dangers. Meanwhile, Nemo plots his escape from a dentist's fish tank.", 3));
        movieList.add(new MovieReview("Fantastic film", "Finding Dory", "dpatelism", "4/11/2020", "Dory (Ellen DeGeneres) is a wide-eyed, blue tang fish who suffers from memory loss every 10 seconds or so. The one thing she can remember is that she somehow became separated from her parents as a child. With help from her friends Nemo and Marlin, Dory embarks on an epic adventure to find them. Her journey brings her to the Marine Life Institute, a conservatory that houses diverse ocean species. Dory now knows that her family reunion will only happen if she can save mom and dad from captivity.", 4));
        movieList.add(new MovieReview("Shitty film", "Lethal Weapon", "dpatelism", "4/11/2020", "Rebooting the hit movie franchise of the same name, \"Lethal Weapon\" is set in modern-day Los Angeles, where Detective Roger Murtaugh works a crime-ridden beat. Some of Murtaugh's colleagues at the LAPD include Detective Bailey, Capt. Brooks Avery, and a pathologist named Scorsese. Murtaugh, married to Trish, and a father of three, finds himself working with Detective Wesley Cole, a former international CIA operative who has been everywhere and seen everything. Cole must adjust to life on the West Coast while navigating his partnership with Murtaugh.", 5));
        movieList.add(new MovieReview("Yes", "The Emoji Movie", "gofurkle", "4/11/2020", "Wow.", 6));
        movieList.add(new MovieReview("No", "Free Willy", "one2remember", "4/11/2020", "MJ is a treasure.", 7));
        movieList.add(new MovieReview("GOAT", "Pan's Labyrinth", "one2remember", "4/11/2020", "GOAT", 8));
        return movieList;
    }
}
