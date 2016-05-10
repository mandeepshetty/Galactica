package com.example.mandeep.galactica.movies;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.mandeep.galactica.MainActivity;
import com.example.mandeep.galactica.R;

import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import timber.log.Timber;


public class MovieSearchResultsFragment extends Fragment implements SearchView.OnQueryTextListener,
        MoviesAPI.MoviesAsyncResponse {

    private Context context;
    SearchView searchView;
    RecyclerView moviesRecyclerView;
    TabLayout tabsTitleStrip;

    public MovieSearchResultsFragment() {}

    @Bind(R.id.noResultsFoundGroup) LinearLayout noResultsLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.movie_search_results_fragment, container, false);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        moviesRecyclerView = (RecyclerView) v.findViewById(R.id.moviesRecyclerView);
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        tabsTitleStrip = (TabLayout) ((MainActivity) context).findViewById(R.id.tabs);
        tabsTitleStrip.setVisibility(View.GONE);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            String query = args.getString("query");
            if (!TextUtils.isEmpty(query)) {
                Timber.i("onViewCreated: query is %s", query);
                search(query);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(true);
    }

    private void processSimilarMoviesResult(MovieResultsPage resultData) {

    }

    private void processSearchResult(MovieResultsPage searchResults) {

        progressBar.setVisibility(View.GONE);
        if (searchResults == null) {
            Timber.e("Null search results");
            return;
        }
        if (searchResults.getTotalResults() <= 0) {
            noResultsLayout.setVisibility(View.VISIBLE);
            return;
        }
        noResultsLayout.setVisibility(View.GONE);

        Collections.sort(
                searchResults.getResults(), new Comparator<MovieDb>() {
                    @Override
                    public int compare(MovieDb lhs, MovieDb rhs) {
                        if (lhs.getPopularity() < rhs.getPopularity()) return 1;
                        else if (lhs.getPopularity() > rhs.getPopularity()) return -1;
                        else return 0;
                    }
                });
        moviesRecyclerView.setAdapter(new MoviesListAdapter(context, searchResults));
        moviesRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_in));
    }

    @Override
    public void onReceiveResult(MoviesAPI.MoviesAPIRequestType requestType, Object results) {

        switch (requestType) {
            case SEARCH:
                MovieResultsPage searchResults = (MovieResultsPage) results;
                processSearchResult(searchResults);
                break;
            case GET_SIMILAR:
                MovieResultsPage similarMovies = (MovieResultsPage) results;
                processSimilarMoviesResult(similarMovies);
                break;
            default:
        }
    }

    private void search(String query) {
        new MoviesAPI(MoviesAPI.MoviesAPIRequestType.SEARCH).setCallback(this).execute(query);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        searchView.clearFocus();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        new MoviesAPI(MoviesAPI.MoviesAPIRequestType.GET_SIMILAR_FOR_MODEL).execute();
        tabsTitleStrip.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static MovieSearchResultsFragment newInstance() {
        return new MovieSearchResultsFragment();
    }
}
