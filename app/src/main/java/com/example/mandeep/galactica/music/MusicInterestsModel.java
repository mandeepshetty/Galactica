package com.example.mandeep.galactica.music;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mandeep.galactica.MainActivity;
import com.example.mandeep.galactica.R;
import com.example.mandeep.galactica.music.MusicAPI.MusicSearchResult.Artist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/***
 * Alice in chains: c14c0e48-31db-3137-1caa-b2bc2b9df2a1
 * Metallica : e7a06e67-a6b5-11e0-b446-00251188dd67
 * Megadeth: e2e83f66-a6b5-11e0-b446-00251188dd67
 * Led Zeppelin: edc53fa6-a6b5-11e0-b446-00251188dd67
 * Justin Timberlake: e43f4e15-a6b5-11e0-b446-00251188dd67
 * Opeth : 4748d13c-4aac-cc41-69ab-8b10938c2fab
 */


public class MusicInterestsModel extends RecyclerView.Adapter<MusicInterestsModel.MusicCardHolder>
        implements MusicAPI.MusicAsyncSimilarArtistsResponse {

    List<Pair<Artist, List<Artist>>> musicInterests = new ArrayList<>();

    static MusicInterestsModel modelInstance;
    private Context context;

    private MusicInterestsModel() {
    }

    @Override
    public MusicCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.music_card, parent, false);
        return new MusicCardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MusicCardHolder holder, int position) {

        final MusicAPI.MusicSearchResult.Artist artist = musicInterests.get(position).first;
        Timber.i("onBindViewHolder:Fetching artist %s", artist.name);
        if (!TextUtils.isEmpty(artist.spotify_image_uri)) {
            Picasso.with(context)
                    .load(artist.spotify_image_uri)
                    .resize(620, 620)
                    .centerInside()
                    .into(holder.poster);
        }

        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(
                        ((MainActivity) context).findViewById(R.id.fragmentHolder)
                        , artist.name, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicInterests.size();
    }

    public MusicInterestsModel setContext(Context context) {
        this.context = context;
        return this;
    }

    public List<Artist> getIntersectionWith(List<String> otherGuysInterests) {

        List<Artist> intersection = new ArrayList<>();
        for (String id : otherGuysInterests) {
            int pos = findPositionOfArtist(id);
            if (pos != -1) {
                intersection.add(musicInterests.get(pos).first);
            }
        }
        return intersection;
    }

    private int findPositionOfArtist(String id) {
        for (int i = 0; i < musicInterests.size(); i++) {
            Pair<Artist, List<Artist>> pair = musicInterests.get(i);
            if (pair.first.id.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Artist> getFirstHopMatches(List<String> otherGuysInterests) {
        Set<Artist> firstHopsSet = new HashSet<>();
        for (String id : otherGuysInterests) {
            for (Pair<Artist, List<Artist>> pair : musicInterests) {
                for (Artist a : pair.second) {
                    if (a.id.equals(id)) firstHopsSet.add(a);
                }
            }
        }
        List<Artist> firstHopsList = new ArrayList<>();
        firstHopsList.addAll(firstHopsSet);
        return firstHopsList;
    }

    public static MusicInterestsModel getInstance() {
        if (modelInstance == null)
            modelInstance = new MusicInterestsModel();
        return modelInstance;
    }

    public void addArtist(MusicAPI.MusicSearchResult.Artist artist) {
        musicInterests.add(new Pair<Artist, List<Artist>>(artist, new ArrayList<Artist>()));
        Timber.i("Added artist %s with id: %s", artist.name, artist.id);

        new MusicAPI().setSimilarArtistsCallback(this)
                .searchSimilarArtists(artist.id);
    }

    public void removeArtistFromInterests(Artist artist) {

        if (musicInterests.isEmpty()) return;

        int i = 0;
        for (Pair<Artist, List<Artist>> a : musicInterests) {
            if (a.first.id.equals(artist.id)) {
                break;
            }
            ++i;
        }
        musicInterests.remove(i);
    }

    public boolean isArtistAlreadyAdded(MusicAPI.MusicSearchResult.Artist artist) {

        for (Pair<Artist, List<Artist>> a : musicInterests) {
            if (a.first.id.equals(artist.id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onReceiveResult(String id, Object results) {

        for (Pair<Artist, List<Artist>> a : musicInterests) {
            if (a.first.id.equals(id)) {
                a.second.addAll(((MusicAPI.MusicSearchResult) results).data);
                return;
            }
        }
        Timber.e("Received similar artist search for %s not in model", id);
    }

    public List<String> getMusicIDs() {
        List musicIDs = new ArrayList();
        for (Pair<Artist, List<Artist>> pair : musicInterests){
            musicIDs.add(pair.first.id);
        }
        return musicIDs;
    }

    public int getMatchScore(List<String> otherGuysInterests){

        if (otherGuysInterests.isEmpty()) return 0;

        int a = getIntersectionWith(otherGuysInterests).size();
        int b = getFirstHopMatches(otherGuysInterests).size();
        return 5 * a + 3 * b;
    }


    public class MusicCardHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        public MusicCardHolder(View itemView) {
            super(itemView);
            this.poster = (ImageView) itemView.findViewById(R.id.artistPosterCard);
        }
    }

}
