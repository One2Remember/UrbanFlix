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
        movieList.add(new MovieReview("Jaws", "gofurkle", "4/11/2020", "When a young woman is killed by a shark while skinny-dipping near the New England tourist town of Amity Island, police chief Martin Brody (Roy Scheider) wants to close the beaches, but mayor Larry Vaughn (Murray Hamilton) overrules him, fearing that the loss of tourist revenue will cripple the town. Ichthyologist Matt Hooper (Richard Dreyfuss) and grizzled ship captain Quint (Robert Shaw) offer to help Brody capture the killer beast, and the trio engage in an epic battle of man vs. nature.", false));
        movieList.add(new MovieReview("Up", "gofurkle", "4/11/2020", "Carl Fredricksen, a 78-year-old balloon salesman, is about to fulfill a lifelong dream. Tying thousands of balloons to his house, he flies away to the South American wilderness. But curmudgeonly Carl's worst nightmare comes true when he discovers a little boy named Russell is a stowaway aboard the balloon-powered house. A Pixar animation.", true));
        movieList.add(new MovieReview("Finding Nemo", "dpatelism", "4/11/2020", "Marlin (Albert Brooks), a clown fish, is overly cautious with his son, Nemo (Alexander Gould), who has a foreshortened fin. When Nemo swims too close to the surface to prove himself, he is caught by a diver, and horrified Marlin must set out to find him. A blue reef fish named Dory (Ellen DeGeneres) -- who has a really short memory -- joins Marlin and complicates the encounters with sharks, jellyfish, and a host of ocean dangers. Meanwhile, Nemo plots his escape from a dentist's fish tank.", true));
        movieList.add(new MovieReview("Finding Dory", "dpatelism", "4/11/2020", "Dory (Ellen DeGeneres) is a wide-eyed, blue tang fish who suffers from memory loss every 10 seconds or so. The one thing she can remember is that she somehow became separated from her parents as a child. With help from her friends Nemo and Marlin, Dory embarks on an epic adventure to find them. Her journey brings her to the Marine Life Institute, a conservatory that houses diverse ocean species. Dory now knows that her family reunion will only happen if she can save mom and dad from captivity.", true));
        movieList.add(new MovieReview("Lethal Weapon", "dpatelism", "4/11/2020", "Rebooting the hit movie franchise of the same name, \"Lethal Weapon\" is set in modern-day Los Angeles, where Detective Roger Murtaugh works a crime-ridden beat. Some of Murtaugh's colleagues at the LAPD include Detective Bailey, Capt. Brooks Avery, and a pathologist named Scorsese. Murtaugh, married to Trish, and a father of three, finds himself working with Detective Wesley Cole, a former international CIA operative who has been everywhere and seen everything. Cole must adjust to life on the West Coast while navigating his partnership with Murtaugh.", false));
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
