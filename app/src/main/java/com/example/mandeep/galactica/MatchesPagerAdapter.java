package com.example.mandeep.galactica;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mandeep.galactica.movies.MoviesInterestMatchFragment;
import com.example.mandeep.galactica.music.MusicInterestsMatchFragment;

import java.util.List;

/**
 * Created by mandeep on 10/4/16.
 */
public class MatchesPagerAdapter extends FragmentStatePagerAdapter{


    private String interestTabTitles[] = {"Movies", "Music"};
    List<Integer> movieInterests;
    List<String> musicInterests;

    public MatchesPagerAdapter(FragmentManager fm,
                               List<Integer> movieInterests,
                               List<String> musicInterests) {
        super(fm);
        this.movieInterests = movieInterests;
        this.musicInterests = musicInterests;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment newFragment;
        switch (position) {
            case 0:
                newFragment = new MoviesInterestMatchFragment().setInterestList(movieInterests);
                break;
            case 1:
                newFragment = new MusicInterestsMatchFragment().setInterestList(musicInterests);
//                newFragment = new Fragment();
                break;
            default:
                newFragment = new Fragment();
        }
        return newFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return interestTabTitles[position];
    }

    @Override
    public int getCount() {
        return interestTabTitles.length;
    }
}
