package com.example.mandeep.galactica;

import com.example.mandeep.galactica.movies.MovieInterestsModel;
import com.example.mandeep.galactica.music.MusicInterestsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 30/4/16.
 */
public class Person implements Comparable {

    private String name;
    private List<Integer> movieInterests;
    private List<String> musicInterests;
    private Integer score;

    public Person(String name, List<Integer> movieInterests, List<String> musicInterests) {
        this.name = name;
        if (movieInterests == null)
            this.movieInterests = new ArrayList<>();
        else
            this.movieInterests = movieInterests;

        if (musicInterests == null)
            this.musicInterests = new ArrayList<>();
        else
            this.musicInterests = musicInterests;

        if (movieInterests != null)
            score = MovieInterestsModel.getInstance().getMatchScore(movieInterests) +
                            MusicInterestsModel.getInstance().getMatchScore(musicInterests);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getMovieInterests() {
        return movieInterests;
    }

    public List<String> getMusicInterests() {
        return musicInterests;
    }

    @Override
    public int compareTo(Object another) {

        if (this == another) return 0;
        Person other = (Person) another;
        return other.score.compareTo(this.score);
    }

    public int getScore() {
        return score;
    }
}
