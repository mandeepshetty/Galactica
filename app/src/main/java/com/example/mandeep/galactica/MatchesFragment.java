package com.example.mandeep.galactica;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import timber.log.Timber;


public class MatchesFragment extends Fragment {

    private static final String TITLE = "Matches";
    private Context context;
    private List<Integer> movieInterests;
    private List<String> musicInterests;

    public MatchesFragment() {}

    TabLayout tabsTitleStrip;
    ViewPager pager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.matches_fragment, container, false);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (ViewPager) view.findViewById(R.id.viewPager);
        Timber.e("onViewCreated:Before");
        PagerAdapter adapter = new MatchesPagerAdapter(getFragmentManager(), movieInterests,
                musicInterests);
        Timber.e("onViewCreated:After");
        tabsTitleStrip = (TabLayout) ((MainActivity) context).findViewById(R.id.tabs);
        ((MainActivity) context).getSupportActionBar().setTitle(TITLE);
        pager.setAdapter(adapter);
        tabsTitleStrip.setVisibility(View.VISIBLE);
        tabsTitleStrip.setupWithViewPager(pager);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabsTitleStrip.setVisibility(View.GONE);
    }

    public MatchesFragment setMovieInterestList(List<Integer> toPass) {
        this.movieInterests = toPass;
        return this;
    }

    public MatchesFragment setMusicInterestList(List<String> toPass) {
        this.musicInterests = toPass;
        return this;
    }
}
