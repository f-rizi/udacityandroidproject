package com.example.fatemeh.udacityandroidcourseproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.fragments.MovieDetailsFragment;
import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponseResult;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_PARAM = "movie";

    private DiscoverMovieResponseResult mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

        if (savedInstanceState == null) {
            setMovieDetailsFragment();
        }
    }

    private void getData() {
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        mMovie = bundle.getParcelable(MOVIE_PARAM);

        setTitle(mMovie.getTitle());
    }


    private void setMovieDetailsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(mMovie);
//        fragmentManager.saveFragmentInstanceState(fragment);

        fragmentTransaction.replace(R.id.movie_details_fragment, fragment);
        fragmentTransaction.commit();
    }
}
