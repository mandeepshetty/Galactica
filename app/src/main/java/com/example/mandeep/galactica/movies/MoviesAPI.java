package com.example.mandeep.galactica.movies;

import android.os.AsyncTask;
import android.util.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import timber.log.Timber;

public class MoviesAPI extends AsyncTask<Object, Object, Object> {

    private static final String API_KEY = "09b4a8e4bbb4fbb73d2767ecd9a3eafa";

    public enum MoviesAPIRequestType {
        SEARCH, GET_SIMILAR, GET_SIMILAR_FOR_MODEL
    }

    MoviesAsyncResponse callback;

    public interface MoviesAsyncResponse {
        void onReceiveResult(MoviesAPIRequestType requestType, Object results);
    }

    public MoviesAPI setCallback(MoviesAsyncResponse callback) {
        this.callback = callback;
        return this;
    }

    MoviesAPIRequestType requestType;

    public MoviesAPI(MoviesAPIRequestType requestType) {

        this.requestType = requestType;
    }

    @Override
    protected Object doInBackground(Object... params) {

        if (params == null)
            Timber.d("null params");

        Object results = null;

        if (requestType == MoviesAPIRequestType.SEARCH) {
            results = search((String) params[0]);
        }
        else if (requestType == MoviesAPIRequestType.GET_SIMILAR) {
            results = getSimilarMovies((Integer) params[0]);
        }
        else if (requestType == MoviesAPIRequestType.GET_SIMILAR_FOR_MODEL) {
            getSimilarMoviesForModel();
        }


        return results;
    }

    private void getSimilarMoviesForModel() {

        for (Pair<MovieDb, List<MovieDb>> pair : MovieInterestsModel.getInstance().myInterests) {

            MovieResultsPage similar = getSimilarMovies(pair.first.getId());
            Collections.sort(
                    similar.getResults(), new Comparator<MovieDb>() {
                        @Override
                        public int compare(MovieDb lhs, MovieDb rhs) {
                            if (lhs.getPopularity() < rhs.getPopularity()) return 1;
                            else if (lhs.getPopularity() > rhs.getPopularity()) return -1;
                            else return 0;
                        }
                    });
            pair.second.addAll(similar.getResults().subList(0, 5));
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        if (callback != null)
            callback.onReceiveResult(requestType, o);
    }

    private MovieResultsPage search(String name) {
        Timber.i("Searching for movies with query %s", name);
        TmdbApi api = new TmdbApi(API_KEY);
        return api.getSearch().searchMovie(name, null, "en", false, 0);
    }

    private MovieResultsPage getSimilarMovies(int id) {
        Timber.i("Searching for similar movies with id %d", id);
        TmdbApi api = new TmdbApi(API_KEY);
        return api.getMovies().getSimilarMovies(id, "en", 1);
    }
}