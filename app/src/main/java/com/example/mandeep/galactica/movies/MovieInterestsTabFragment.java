package com.example.mandeep.galactica.movies;


import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mandeep.galactica.MainActivity;
import com.example.mandeep.galactica.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieInterestsTabFragment extends Fragment implements SearchView.OnQueryTextListener,
        FragmentManager.OnBackStackChangedListener{

    private Context context;
    SearchView searchView;
    RecyclerView moviesInterestGrid;
    public MovieInterestsTabFragment() {}

    @Bind(R.id.noMoviesInterests) TextView noMovieInterests;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.movie_interests_tab_fragment, container, false);
        ButterKnife.bind(this, v);
        moviesInterestGrid = (RecyclerView) v.findViewById(R.id.interestsGrid);
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moviesInterestGrid.setLayoutManager(
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        moviesInterestGrid.setAdapter(MovieInterestsModel.getInstance().setContext(context));
        moviesInterestGrid.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) context).getSupportFragmentManager().
                addOnBackStackChangedListener(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setVisibility(View.VISIBLE);
        searchView.setLayoutTransition(new LayoutTransition());
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(true);
    }

    public static MovieInterestsTabFragment newInstance() {
        return new MovieInterestsTabFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Bundle args = new Bundle();
        args.putString("query", query);

        Fragment newFragment = MovieSearchResultsFragment.newInstance();
        newFragment.setArguments(args);

        ((MainActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder, newFragment)
                .addToBackStack(null)
                .hide(this)
                .commit();

        searchView.clearFocus();
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onBackStackChanged() {
        if (MovieInterestsModel.getInstance().getItemCount() == 0){
            noMovieInterests.setVisibility(View.VISIBLE);
        }else {
            noMovieInterests.setVisibility(View.GONE);
        }
    }
}