package com.example.mandeep.galactica;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mandeep.galactica.movies.MovieInterestsTabFragment;
import com.example.mandeep.galactica.music.MusicInterestsTabFragment;

/**
 * Created by mandeep on 3/4/16.
 */
public class InterestsPagerAdapter extends FragmentStatePagerAdapter {

    private String interestTabTitles[] = {"Movies", "Music"};

    public InterestsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment newFragment;
        switch (position) {
            case 0:
                newFragment = MovieInterestsTabFragment.newInstance();
                break;
            case 1:
                newFragment = MusicInterestsTabFragment.newInstance();
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
