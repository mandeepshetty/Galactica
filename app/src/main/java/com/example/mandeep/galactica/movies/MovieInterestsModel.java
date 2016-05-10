package com.example.mandeep.galactica.movies;

import android.content.Context;
import android.net.Uri;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.movito.themoviedbapi.model.MovieDb;
import timber.log.Timber;

/**
 * Created by mandeep on 5/4/16.
 */
public class MovieInterestsModel extends RecyclerView.Adapter<MovieInterestsModel.MovieCardHolder> {

    List<Pair<MovieDb, List<MovieDb>>> myInterests = new ArrayList<>();

    static MovieInterestsModel modelInstance;
    private Context context;

    private MovieInterestsModel() {}

    @Override
    public MovieInterestsModel.MovieCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.movie_card, parent, false);
        return new MovieCardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MovieInterestsModel.MovieCardHolder holder, int position) {

        final MovieDb movie = myInterests.get(position).first;

        Timber.i("onBindViewHolder:Fetching movie %s", movie.getTitle());
        if (!TextUtils.isEmpty(movie.getPosterPath())) {
            Uri posterUri = new Uri.Builder()
                    .scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t").appendPath("p").appendPath("w342")
                    .appendEncodedPath(movie.getPosterPath()).build();

            Picasso.with(context).load(posterUri).fit().into(holder.poster);
        }
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(
                        ((MainActivity) context).findViewById(R.id.fragmentHolder)
                        , movie.getTitle()
                        , Snackbar.LENGTH_LONG).show();
            }
        });

        holder.poster.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return myInterests.size();
    }

    public MovieInterestsModel setContext(Context context) {
        this.context = context;
        return this;
    }

    public static MovieInterestsModel getInstance() {
        if (modelInstance == null)
            modelInstance = new MovieInterestsModel();
        return modelInstance;
    }

    public void addMovie(MovieDb movie) {
        myInterests.add(new Pair<MovieDb, List<MovieDb>>(movie, new ArrayList<MovieDb>()));
    }

    public void removeMovieFromInterests(int movieID) {

        int pos = findPositionOfMovie(movieID);
        if (pos != -1)
            myInterests.remove(pos);
    }

    private int findPositionOfMovie(int movieID) {

        for (int i = 0; i < myInterests.size(); i++) {
            Pair<MovieDb, List<MovieDb>> pair = myInterests.get(i);
            if (pair.first.getId() == movieID) {
                return i;
            }
        }
        return -1;
    }

    public boolean isMovieInInterests(int movieID) {
        return findPositionOfMovie(movieID) != -1;
    }

    public List<MovieDb> getIntersectionWith(List<Integer> otherGuysInterests) {

        List<MovieDb> intersection = new ArrayList<>();
        for (int id: otherGuysInterests){
            int pos = findPositionOfMovie(id);
            if (pos != -1) {
                intersection.add(myInterests.get(pos).first);
            }
        }
        return intersection;
    }

    public List<MovieDb> getFirstHopMatches(List<Integer> otherGuysInterests) {

        List<MovieDb> intersection = getIntersectionWith(otherGuysInterests);

        Set<MovieDb> firstHopsSet = new HashSet<>();
        for (int id : otherGuysInterests) {
            for (Pair<MovieDb, List<MovieDb>> pair : myInterests) {
                for (MovieDb m : pair.second) {
                    if (m.getId() == id) {
                        if(!isMovieInIntersection(intersection, m))
                            firstHopsSet.add(m);
                    }
                }
            }
        }
        List<MovieDb> firstHopsList = new ArrayList<>();
        firstHopsList.addAll(firstHopsSet);
        return firstHopsList;
    }

    private boolean isMovieInIntersection(List<MovieDb> intersection, MovieDb m) {
        for (MovieDb x : intersection){
            if (x.getId() == m.getId())
                return true;
        }
        return false;
    }

    public int getMatchScore(List<Integer> otherGuysInterests){

        if (otherGuysInterests.isEmpty()) return 0;
        int a = getIntersectionWith(otherGuysInterests).size();
        int b = getFirstHopMatches(otherGuysInterests).size();
        return 5 * a + 3 * b;
    }

    public List<Integer> getMovieIDs() {
        List movieIDs = new ArrayList();
        for (Pair<MovieDb, List<MovieDb>> pair : myInterests){
            movieIDs.add(pair.first.getId());
        }
        return movieIDs;
    }

    public class MovieCardHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        public MovieCardHolder(View itemView) {
            super(itemView);
            this.poster = (ImageView) itemView.findViewById(R.id.cardPoster);
        }

    }
}
