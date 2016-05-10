package com.example.mandeep.galactica.music;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import timber.log.Timber;


public class MusicAPI {

    public static final String API_KEY = "c8303e90962e3a5ebd5a1f260a69b138";
    public static final String MUSICGRAPH_BASE_URL = "http://api.musicgraph.com/api/v2/artist/";


    public enum MusicAPIRequestType {
        SEARCH, GET_SIMILAR, SPOTIFY_IMAGE_SEARCH
    }

    MusicAsyncSearchResponse searchCallback;
    MusicAsyncSimilarArtistsResponse similarCallback;

    public interface MusicAsyncSearchResponse {
        void onReceiveResult(MusicAPIRequestType requestType, Object results);
    }

    public interface MusicAsyncSimilarArtistsResponse {
        void onReceiveResult(String id, Object results);
    }

    public MusicAPI setSearchCallback(MusicAsyncSearchResponse callback) {
        this.searchCallback = callback;
        return this;
    }

    public MusicAPI setSimilarArtistsCallback(MusicAsyncSimilarArtistsResponse callback) {
        this.similarCallback = callback;
        return this;
    }

    public void fetchSpotifyProfileImage(String spotifyID){

        retrofit.Retrofit retrofit = new retrofit.Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpotifyImageAPI api = retrofit.create(SpotifyImageAPI.class);
        Call<SpotifyImageResult> request = api.fetchArtistProfileImage(spotifyID);

        request.enqueue(new Callback<SpotifyImageResult>() {
            @Override
            public void onResponse(Response<SpotifyImageResult> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (searchCallback != null)
                        searchCallback.onReceiveResult(MusicAPIRequestType.SPOTIFY_IMAGE_SEARCH, response.body());
                }else {
                    Timber.e("onResponse: Error fetching image (%d)", response.code());
                }
            }

            @Override public void onFailure(Throwable t) {}
        });
    }

    public void searchArtist(String artistName) {

        retrofit.Retrofit retrofit = new retrofit.Retrofit.Builder()
                .baseUrl(MUSICGRAPH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("api_key", API_KEY);
        queryMap.put("name", artistName.trim());

        retrofit.create(MusicGraphAPI.class)
                .exactNameSearch(queryMap)
                .enqueue(new Callback<MusicSearchResult>() {
                    @Override
                    public void onResponse(Response<MusicSearchResult> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            if (searchCallback != null)
                                searchCallback.onReceiveResult(MusicAPIRequestType.SEARCH, response.body());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
    }

    public void searchSimilarArtists(final String id){
        retrofit.Retrofit retrofit = new retrofit.Retrofit.Builder()
                .baseUrl(MUSICGRAPH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("api_key", API_KEY);

        retrofit.create(MusicGraphAPI.class)
                .getSimilarArtists(id, queryMap)
                .enqueue(new Callback<MusicSearchResult>() {
                    @Override
                    public void onResponse(Response<MusicSearchResult> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Timber.i("Received success on simialr artist search");
                            if (similarCallback != null)
                                similarCallback.onReceiveResult(id, response.body());
                        }
                    }
                    @Override public void onFailure(Throwable t) {
                        Timber.e(t.getMessage());
                    }
                });
    }

    public class SpotifyImageResult {
        public List<SpotifyImage> images;

        public class SpotifyImage {
            int height, width;
            public String url;
        }
    }

    interface SpotifyImageAPI{
        @GET("artists/{spotify_id}")
        Call<SpotifyImageResult> fetchArtistProfileImage(@Path("spotify_id") String spotifyID);
    }

    public class MusicSearchResult {

        public List<Artist> data;

        public class Artist {
            String id;
            public String spotify_image_uri;
            public String name;
            public String decade;
            public String musicbrainz_image_url;
            public String spotify_id;
            public String country_of_origin;
        }
    }

    interface MusicGraphAPI {
        @GET("search")
        Call<MusicSearchResult> exactNameSearch(@QueryMap Map<String, String> options);

        @GET("{id}/similar")
        Call<MusicSearchResult> getSimilarArtists(@Path("id") String id,
                                                  @QueryMap Map<String, String> options);

    }
}
