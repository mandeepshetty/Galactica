package com.example.mandeep.galactica.music;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mandeep.galactica.MainActivity;
import com.example.mandeep.galactica.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class MusicSearchResultsFragment extends Fragment implements SearchView.OnQueryTextListener,
        MusicAPI.MusicAsyncSearchResponse {

    private Context context;
    SearchView searchView;
    TabLayout tabsTitleStrip;

    public MusicSearchResultsFragment() {
    }

    MusicAPI.MusicSearchResult.Artist currentArtist;

    @Bind(R.id.artistPoster) ImageView poster;
    @Bind(R.id.artistName) TextView name;
    @Bind(R.id.country) TextView country;
    @Bind(R.id.decades) TextView decades;
    @Bind(R.id.addRemoveArtist) Button addRemoveArtist;
    @Bind(R.id.noResultsFoundGroupMusic) LinearLayout noResultsFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.music_search_results_fragment, container, false);
        ButterKnife.bind(this, v);

        tabsTitleStrip = (TabLayout) ((MainActivity) context).findViewById(R.id.tabs);
        tabsTitleStrip.setVisibility(View.GONE);

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

    @Override
    public void onReceiveResult(MusicAPI.MusicAPIRequestType requestType, Object results) {

        switch (requestType) {
            case SEARCH:
                processSearchResult((MusicAPI.MusicSearchResult) results);
                break;
            case GET_SIMILAR:
                break;
            case SPOTIFY_IMAGE_SEARCH:
                setArtistImage((MusicAPI.SpotifyImageResult) results);
                break;
            default:
        }
    }

    private void setArtistImage(MusicAPI.SpotifyImageResult results) {

        if (results.images.isEmpty()) {
            Timber.e("setArtistImage:No spotify images!");
            return;
        }
        currentArtist.spotify_image_uri = results.images.get(0).url;

        Picasso.with(context)
                .load(Uri.parse(results.images.get(0).url))
                .into(poster);
    }

    private void processSearchResult(MusicAPI.MusicSearchResult searchResults) {

        if (searchResults.data.isEmpty()) {
            noResultsFound.setVisibility(View.VISIBLE);
            return;
        }
        noResultsFound.setVisibility(View.GONE);
        if (searchResults.data.size() > 1)
            Timber.e("processSearchResult:More than 1 artist returned");

        final MusicAPI.MusicSearchResult.Artist artist =
                searchResults.data.get(0);

        currentArtist = artist;

        if (!TextUtils.isEmpty(artist.name))
            name.setText(artist.name);
        if (!TextUtils.isEmpty(artist.country_of_origin))
            country.setText(artist.country_of_origin);
        if (!TextUtils.isEmpty(artist.decade))
            decades.setText(artist.decade);

        if (!TextUtils.isEmpty(artist.spotify_id)) {

            new MusicAPI().setSearchCallback(this)
                    .fetchSpotifyProfileImage(artist.spotify_id);
        }
        else if (!TextUtils.isEmpty(artist.musicbrainz_image_url)) {
            Timber.e("processSearchResult:No spotify image.");
            Picasso.with(context)
                    .load(Uri.parse(artist.musicbrainz_image_url))
                    .into(poster);
        }
        else {
            Timber.e("processSearchResult:No Images found at all");
        }

        if (MusicInterestsModel.getInstance().isArtistAlreadyAdded(artist)) {
            addRemoveArtist.setTextColor(Color.GREEN);
        }
        else {
            addRemoveArtist.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        addRemoveArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MusicInterestsModel.getInstance().isArtistAlreadyAdded(artist)) {
                    addRemoveArtist.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    MusicInterestsModel.getInstance().removeArtistFromInterests(artist);
                }
                else {
                    MusicInterestsModel.getInstance().addArtist(artist);
                    addRemoveArtist.setTextColor(Color.GREEN);
                }
            }
        });
    }

    private void search(String query) {
        Timber.i("search:Searching for %s", query);
        clearViews();
        new MusicAPI().setSearchCallback(this).searchArtist(query.trim());
    }

    private void clearViews() {
        name.setText("");
        country.setText("");
        decades.setText("");
        poster.setImageDrawable(null);
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
        tabsTitleStrip.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static Fragment newInstance() {
        return new MusicSearchResultsFragment();
    }


}
