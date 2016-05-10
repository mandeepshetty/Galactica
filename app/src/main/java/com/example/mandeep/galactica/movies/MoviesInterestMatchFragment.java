package com.example.mandeep.galactica.movies;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.mandeep.galactica.R;

import java.util.List;


public class MoviesInterestMatchFragment extends Fragment {

    Context context;
    List<Integer> interests;

    public MoviesInterestMatchFragment() {}

    public MoviesInterestMatchFragment setInterestList(List<Integer> interests) {
        this.interests = interests;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movies_interest_match_fragment, container, false);
        ExpandableListView l = (ExpandableListView) v.findViewById(R.id.moviesInterestMatchList);
        l.setAdapter(new MatchMoviesAdapter(context, interests));
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


}
