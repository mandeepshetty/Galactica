package com.example.mandeep.galactica.music;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.mandeep.galactica.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicInterestsMatchFragment extends Fragment {

    Context context;
    List<String> interests;

    public MusicInterestsMatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_music_interests_match, container, false);
        ExpandableListView l = (ExpandableListView) v.findViewById(R.id.musicInterestMatchList);
        l.setAdapter(new MatchMusicAdapter(context, interests));
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public Fragment setInterestList(List<String> musicInterests) {
        this.interests = musicInterests;
        return this;
    }
}
