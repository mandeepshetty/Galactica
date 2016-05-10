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
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class InterestsFragment extends Fragment {

    private static final String TITLE = "Interests";
    private Context context;
    TabLayout tabsTitleStrip;
    FrameLayout tabsFrame;

    public InterestsFragment() {
    }

    @Bind(R.id.viewPager) ViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.interests_fragment, container, false);
        ButterKnife.bind(this, v);
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
        PagerAdapter adapter = new InterestsPagerAdapter(getFragmentManager());
        tabsTitleStrip = (TabLayout) ((MainActivity) context).findViewById(R.id.tabs);
        tabsFrame = (FrameLayout) ((MainActivity) context).findViewById(R.id.tabsFrame);
        ((MainActivity) context).getSupportActionBar().setTitle(TITLE);
        pager.setAdapter(adapter);
        tabsTitleStrip.setupWithViewPager(pager);
        tabsTitleStrip.setVisibility(View.VISIBLE);
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

}
