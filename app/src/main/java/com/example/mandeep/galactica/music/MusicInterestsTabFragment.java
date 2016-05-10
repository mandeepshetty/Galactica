package com.example.mandeep.galactica.music;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.mandeep.galactica.MainActivity;
import com.example.mandeep.galactica.R;

/**
 * Created by mandeep on 3/4/16.
 */
public class MusicInterestsTabFragment extends Fragment implements SearchView.OnQueryTextListener,
        FragmentManager.OnBackStackChangedListener{

    private Context context;
    SearchView searchView;
    private RecyclerView musicInterestsGrid;
    SwipeRefreshLayout refresh;
    MusicInterestsModel adapter;

    public MusicInterestsTabFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.music_interests_tab_fragment, container, false);
        musicInterestsGrid = (RecyclerView) v.findViewById(R.id.musicInterestsGrid);
        refresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeLayout);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicInterestsGrid.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = MusicInterestsModel.getInstance().setContext(context);
        musicInterestsGrid.setAdapter(adapter);
        musicInterestsGrid.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(true);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(this);
    }

    public static MusicInterestsTabFragment newInstance() {
        return new MusicInterestsTabFragment();
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

        Fragment newFragment = MusicSearchResultsFragment.newInstance();
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
        adapter.notifyDataSetChanged();
    }
}
