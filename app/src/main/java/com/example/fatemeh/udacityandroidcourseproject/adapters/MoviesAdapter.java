package com.example.fatemeh.udacityandroidcourseproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.Views.PosterImageView;
import com.example.fatemeh.udacityandroidcourseproject.activities.MainActivity;
import com.example.fatemeh.udacityandroidcourseproject.activities.MovieDetailsActivity;
import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.utilities.APIEndpoints;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fatemeh on 25/12/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ItemViewHolder>{

    private List<DiscoverMovieResponseResult> mMovies;

    private final Context mContext;
    private final MainActivity mMainActivity;

    public MoviesAdapter(Context context, List<DiscoverMovieResponseResult> movies ) {
        this.mContext = context;
        this.mMainActivity = (MainActivity) context;
        this.mMovies = movies;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_movie, parent, false);

        return new ItemViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final DiscoverMovieResponseResult movie = mMovies.get(position);

        holder.movieLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMainActivity.mHaveTwoFragment) {
                    mMainActivity.setDetailsFragment(movie);
                    mMainActivity.setTitle(movie.getTitle());

                } else {
                    Intent intent = new Intent(mMainActivity, MovieDetailsActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MovieDetailsActivity.MOVIE_PARAM, movie);
                    intent.putExtras(bundle);
                    mMainActivity.startActivity(intent);
                }
            }
        });

        String url = APIEndpoints.POSTER_PATH_URL + movie.getPosterPath();
        Picasso.with(mContext)
                .load(url)
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected RelativeLayout movieLayout;
        protected PosterImageView posterImageView;
        protected TextView titleTextView;

        ItemViewHolder(View view) {
            super(view);

            movieLayout = (RelativeLayout) view.findViewById(R.id.movie);
            posterImageView = (PosterImageView) view.findViewById(R.id.poster);
            titleTextView = (TextView) view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void updateData(List<DiscoverMovieResponseResult> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }
}
