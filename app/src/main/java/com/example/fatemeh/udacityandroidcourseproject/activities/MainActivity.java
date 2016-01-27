package com.example.fatemeh.udacityandroidcourseproject.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.adapters.MoviesAdapter;
import com.example.fatemeh.udacityandroidcourseproject.db.MovieContract;
import com.example.fatemeh.udacityandroidcourseproject.fragments.MovieDetailsFragment;
import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.sync.MovieSyncAdapter;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity
        implements  LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SELECTED_MOVIE_PARAM = "selectedMovie";

    public static final int MOVIE_LOADER = 0;

    private MoviesAdapter mMoviesAdapter;

    public boolean mHaveTwoFragment;

    private DiscoverMovieResponseResult mSelectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView moviesRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        setMoviesRecyclerVIew(moviesRecyclerView);

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        if (findViewById(R.id.movie_details_fragment) != null) {
            mHaveTwoFragment = true;
        }

        if (savedInstanceState == null) {
            MovieSyncAdapter.syncImmediately(this);
        }

        if (savedInstanceState != null) {
            mSelectedMovie = savedInstanceState.getParcelable(SELECTED_MOVIE_PARAM);
        }
    }

    private void updateAdapter(List<DiscoverMovieResponseResult> movies) {
        mMoviesAdapter.updateData(movies);

        if (mHaveTwoFragment && mSelectedMovie != null) {
            setDetailsFragment(mSelectedMovie);
            setTitle(mSelectedMovie.getTitle());
            return;
        }

        if (mHaveTwoFragment && movies.size() > 0) {
            setDetailsFragment(movies.get(0));
            setTitle(movies.get(0).getTitle());
        }
    }


    private void setMoviesRecyclerVIew(RecyclerView moviesRecyclerView) {
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mMoviesAdapter = new MoviesAdapter(this, new ArrayList<DiscoverMovieResponseResult>());
        moviesRecyclerView.setAdapter(mMoviesAdapter);
    }

    public void setDetailsFragment(DiscoverMovieResponseResult movie) {
        mSelectedMovie = movie;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(movie);
        fragment.setRetainInstance(true);

        fragmentTransaction.replace(R.id.movie_details_fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_blank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder =  getSortOrder();

        final int NUMBER_OF_MOVIES = 20;

        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder + " LIMIT " + NUMBER_OF_MOVIES);
    }

    private String getSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sortOrderSetting = prefs.getString(
                getString(R.string.pref_movie_sort_key),
                getString(R.string.pref_movie_sort_default));

        String sort;

        if (sortOrderSetting.contains(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)) {
            sort = MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " DESC";

        } else if (sortOrderSetting.contains(MovieContract.MovieEntry.COLUMN_POPULARITY)) {
            sort = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            sort = MovieContract.MovieEntry.COLUMN_FAVORITE + " DESC";
        }

        return sort;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("MOVIE_FROM_CURSOR", cursor.getCount() + "");

        cursor.moveToFirst();

        List<DiscoverMovieResponseResult> movies = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            DiscoverMovieResponseResult movie = new DiscoverMovieResponseResult();

            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE)));

            movies.add(movie);

            cursor.moveToNext();
        }

        updateAdapter(movies);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SELECTED_MOVIE_PARAM, mSelectedMovie);
    }
}
