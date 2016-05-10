package com.example.mandeep.galactica.movies;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mandeep.galactica.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import timber.log.Timber;

/**
 * Created by mandeep on 2/4/16.
 */
public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MovieViewHolder> {

    private Context context;
    private MovieResultsPage movieResults;

    private int lastAnimatedItem = -1;

    public MoviesListAdapter(Context context, MovieResultsPage results) {
        this.context = context;
        this.movieResults = results;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.movie_search_result_list_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {

        final MovieDb movie = movieResults.getResults().get(position);

        holder.title.setText(movie.getTitle().trim());

        if (!TextUtils.isEmpty(movie.getPosterPath())) {
            Uri posterUri = new Uri.Builder()
                    .scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t").appendPath("p").appendPath("w342")
                    .appendEncodedPath(movie.getPosterPath()).build();

            Picasso.with(context).load(posterUri).fit()
                    .into(holder.poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            animatePoster(holder);
                            lastAnimatedItem = position;
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        if (MovieInterestsModel.getInstance().isMovieInInterests(movie.getId())) {
            holder.addRemove.setTextColor(Color.GREEN);
        }else {
            holder.addRemove.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        holder.addRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e("onClick: %s", holder.toString());
                if (MovieInterestsModel.getInstance().isMovieInInterests(movie.getId())) {
                    holder.addRemove.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    MovieInterestsModel.getInstance().removeMovieFromInterests(movie.getId());
                }
                else {
                    MovieInterestsModel.getInstance().addMovie(movie);
                    holder.addRemove.setTextColor(Color.GREEN);
                }
            }
        });
    }

    private void animatePoster(MovieViewHolder holder) {

        if (holder.getAdapterPosition() < lastAnimatedItem)
            return;

        holder.poster.setScaleX(0);
        holder.poster.setScaleY(0);

        holder.poster.animate()
                .scaleX(1).scaleY(1)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(1.0f))
                .setStartDelay(300)
                .start();

    }

    @Override
    public int getItemCount() {
        return movieResults.getResults().size();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected ImageView poster;
        protected Button addRemove;

        public MovieViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            poster = (ImageView) v.findViewById(R.id.poster);
            addRemove = (Button) v.findViewById(R.id.addRemove);
        }
    }
}
