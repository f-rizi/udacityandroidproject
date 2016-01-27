package com.example.fatemeh.udacityandroidcourseproject.fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.Views.ExpandableTextView;
import com.example.fatemeh.udacityandroidcourseproject.Views.MovieDetailCardLayout;
import com.example.fatemeh.udacityandroidcourseproject.Views.MovieDetailInfoLayout;
import com.example.fatemeh.udacityandroidcourseproject.adapters.MovieReviewsAdapter;
import com.example.fatemeh.udacityandroidcourseproject.adapters.MovieVideosAdapter;
import com.example.fatemeh.udacityandroidcourseproject.db.MovieContract;
import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.models.ReviewResponse;
import com.example.fatemeh.udacityandroidcourseproject.models.ReviewResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.models.VideoResponse;
import com.example.fatemeh.udacityandroidcourseproject.models.VideoResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.utilities.APIEndpoints;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MovieDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String MOVIE_PARAM = "movie";
    private static final String REVIEWS_PARAM = "reviews";
    private static final String VIDEOS_PARAM = "videos";

    private int mMovieId;

    private boolean mGetFromDB;

    private Context mContext;

    private DiscoverMovieResponseResult mMovie;
    private List<ReviewResponseResult> mReviewResponseResults;
    private List<VideoResponseResult> mVideoResponseResults;

    private ImageView mPosterImageView;

    private MovieDetailInfoLayout mReleasedDateLayout;
    private MovieDetailInfoLayout mRuntimeLayout;
    private MovieDetailInfoLayout mBudgetLayout;
    private MovieDetailInfoLayout mLanguageLayout;
    private MovieDetailInfoLayout mVoteAverageLayout;
    private MovieDetailCardLayout mReviewsHeaderLayout;
    private MovieDetailCardLayout mVideosHeaderLayout;

    private Button mAddToFavoriteButton;

    private RecyclerView mVideoRecyclerView;
    private MovieVideosAdapter mVideosAdapter;

    private RecyclerView mReviewsRecyclerView;
    private MovieReviewsAdapter mReviewsAdapter;

    private ExpandableTextView mOverviewTextView;

    private ProgressDialog mProgressDialog;

    private boolean mIsVideosLoadingOver;
    private boolean mIsReviewsLoadingOver;

    public static MovieDetailsFragment newInstance(DiscoverMovieResponseResult movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_PARAM, movie);

        fragment.setArguments(bundle);

        return fragment;
    }

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(MOVIE_PARAM);
        }

        if (savedInstanceState != null) {
            mReviewResponseResults = savedInstanceState.getParcelableArrayList(REVIEWS_PARAM);
            mVideoResponseResults = savedInstanceState.getParcelableArrayList(VIDEOS_PARAM);
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        mAddToFavoriteButton = (Button) view.findViewById(R.id.add_to_favorite);
        mAddToFavoriteButton.setOnClickListener(this);

        mReleasedDateLayout = (MovieDetailInfoLayout) view.findViewById(R.id.released_date);
        mRuntimeLayout = (MovieDetailInfoLayout) view.findViewById(R.id.runtime);
        mBudgetLayout = (MovieDetailInfoLayout) view.findViewById(R.id.budget);
        mLanguageLayout = (MovieDetailInfoLayout) view.findViewById(R.id.language);
        mVoteAverageLayout = (MovieDetailInfoLayout) view.findViewById(R.id.vote_average);

        mOverviewTextView = (ExpandableTextView) view.findViewById(R.id.overview_expandable_text_view);

        mReviewsHeaderLayout = (MovieDetailCardLayout) view.findViewById(R.id.reviews_header);
        mVideosHeaderLayout = (MovieDetailCardLayout) view.findViewById(R.id.videos_header);

        mVideoRecyclerView = (RecyclerView) view.findViewById(R.id.videos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideoRecyclerView.setLayoutManager(layoutManager);
        mVideosAdapter = new MovieVideosAdapter(getActivity(), new ArrayList<VideoResponseResult>());
        mVideoRecyclerView.setAdapter(mVideosAdapter);

        mReviewsRecyclerView = (RecyclerView) view.findViewById(R.id.reviews_recycle_view);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mReviewsRecyclerView.setLayoutManager(layoutManager2);
        mReviewsAdapter = new MovieReviewsAdapter(getActivity(), new ArrayList<ReviewResponseResult>());
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        mPosterImageView = (ImageView) view.findViewById(R.id.poster);

        mMovieId = mMovie.getId();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String movieId = mMovieId + "";

        setDetails();

        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIEndpoints.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        APIEndpoints apiEndpoints =
                retrofit.create(APIEndpoints.class);

        getVideos(apiEndpoints, movieId, savedInstanceState);
        getReviews(apiEndpoints, movieId, savedInstanceState);
    }

    private void getVideos(APIEndpoints apiEndpoints, String movieId, Bundle savedInstanceState) {
        if (mVideoResponseResults != null) {
            mIsVideosLoadingOver = true;
            setVideos(mVideoResponseResults);
            handleProgressBar();

        } else if (mGetFromDB) {
            mIsVideosLoadingOver = true;
            handleProgressBar();
            Cursor cursor = getActivity().getContentResolver().query(
                    MovieContract.VideoEntity.CONTENT_URI,
                    null,
                    MovieContract.VideoEntity.COLUMN_MOVIE_ID + " = ?",
                    new String[]{mMovieId + ""},
                    null
            );

            mVideoResponseResults = new ArrayList<>();

            while (cursor.moveToNext()) {
                VideoResponseResult video = new VideoResponseResult();
                video.setKey(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntity.COLUMN_KEY)));
                video.setName(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntity.COLUMN_NAME)));

                mVideoResponseResults.add(video);
            }

            cursor.close();

            setVideos(mVideoResponseResults);

        } else if (savedInstanceState == null && mGetFromDB == false) {
            Call<VideoResponse> videoResponseCall = apiEndpoints.getMovieVideoResponse(movieId, APIEndpoints.API);
            videoResponseCall.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Response<VideoResponse> response, Retrofit retrofit) {
                    mIsVideosLoadingOver = true;
                    handleProgressBar();
                    mVideoResponseResults = response.body().getResults();

                    setVideos(mVideoResponseResults);
                }

                @Override
                public void onFailure(Throwable t) {
                    mIsVideosLoadingOver = true;
                    handleProgressBar();
                }
            });
        }
    }

    private void getReviews(APIEndpoints apiEndpoints, String movieId, Bundle savedInstanceState) {
        if (mReviewResponseResults != null) {
            mIsReviewsLoadingOver = true;
            setReviews(mReviewResponseResults);
            handleProgressBar();

        } else if (mGetFromDB) {
            mIsReviewsLoadingOver = true;
            handleProgressBar();
            Cursor cursor = getActivity().getContentResolver().query(
                    MovieContract.ReviewEntity.CONTENT_URI,
                    null,
                    MovieContract.ReviewEntity.COLUMN_MOVIE_ID + " = ?",
                    new String[]{mMovieId + ""},
                    null
            );

            mReviewResponseResults = new ArrayList<>();

            while (cursor.moveToNext()) {
                ReviewResponseResult review = new ReviewResponseResult();
                review.setAuthor(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntity.COLUMN_AUTHOR)));
                review.setContent(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntity.COLUMN_CONTENT)));
                review.setUrl(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntity.COLUMN_URL)));

                mReviewResponseResults.add(review);
            }

            cursor.close();

            setReviews(mReviewResponseResults);

        } else if (savedInstanceState == null && mGetFromDB == false) {
            Call<ReviewResponse> reviewResponseCall = apiEndpoints.getMovieReviewResponse(movieId, APIEndpoints.API);
            reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Response<ReviewResponse> response, Retrofit retrofit) {
                    mIsReviewsLoadingOver = true;
                    handleProgressBar();
                    mReviewResponseResults = response.body().getResults();
                    setReviews(mReviewResponseResults);
                }

                @Override
                public void onFailure(Throwable t) {

                    mIsReviewsLoadingOver = true;
                    handleProgressBar();
                }
            });
        }

    }

    private void handleProgressBar() {
        if (mIsReviewsLoadingOver && mIsVideosLoadingOver) {
            mProgressDialog.dismiss();
        }
    }

    private void setPosterImage(String posterPath) {
        String url = APIEndpoints.POSTER_H_PATH_URL + posterPath;

        Picasso.with(getActivity()).load(url).into(mPosterImageView);
    }

    private void setOverview(String overview) {
        if (!TextUtils.isEmpty(overview)) {
            mOverviewTextView.setText(overview);
        } else {
            mOverviewTextView.setText("No Overview available");
        }
    }

    private void setDetails() {
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovieId + ""},
                null
        );

        cursor.moveToFirst();

        if (cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) != 0) {
            mGetFromDB = true;
        }

        cursor.close();

        if (mGetFromDB) {
            mAddToFavoriteButton.setText(getString(R.string.remove_from_favorite));
        } else {
            mAddToFavoriteButton.setText(getString(R.string.add_to_favorite));
        }

        setPosterImage(mMovie.getPosterPath());
        setOverview(mMovie.getOverview());

        if (!TextUtils.isEmpty(mMovie.getReleaseDate())) {
            mReleasedDateLayout.setVisibility(View.VISIBLE);
            mReleasedDateLayout.setContentText(mMovie.getReleaseDate());
        }

        if (mMovie.getVoteCount() > 0) {
            mRuntimeLayout.setVisibility(View.VISIBLE);
            mRuntimeLayout.setContentText(mMovie.getVoteCount() + "");
        }

        if (mMovie.getVoteAverage() > 0) {
            mBudgetLayout.setVisibility(View.VISIBLE);
            mBudgetLayout.setContentText(mMovie.getVoteAverage() + "");
        }

        if (!TextUtils.isEmpty(mMovie.getOriginalLanguage())) {
            mLanguageLayout.setVisibility(View.VISIBLE);
            mLanguageLayout.setContentText(mMovie.getOriginalLanguage());
        }

        if (mMovie.getPopularity() > 0) {
            mVoteAverageLayout.setVisibility(View.VISIBLE);
            mVoteAverageLayout.setContentText(mMovie.getPopularity() + "");
        }
    }

    private void setVideos(List<VideoResponseResult> results) {
        mVideosHeaderLayout.setInfoVisibility(true);
        mVideosHeaderLayout.setInfoText(getResources().getQuantityString(R.plurals.videos, results.size(), results.size()));
        mVideosAdapter.updateData(results);
    }

    private void setReviews(List<ReviewResponseResult> results) {
        mReviewsHeaderLayout.setInfoVisibility(true);
        mReviewsHeaderLayout.setInfoText(getResources().getQuantityString(R.plurals.reviews, results.size(), results.size()));
        mReviewsAdapter.updateData(results);
    }

    @Override
    public void onClick(View v) {
        int favoriteValue;

        if (!mGetFromDB) {
            mAddToFavoriteButton.setText(getString(R.string.remove_from_favorite));
            favoriteValue = 1;
            insertReviewsToDB();
            insertVideosToDB();
            mGetFromDB = true;

        } else {
            mAddToFavoriteButton.setText(getString(R.string.add_to_favorite));
            favoriteValue = 0;
            removeReviewsFromDB();
            removeVideosFromDB();
            mGetFromDB = true;
        }

        updateMovieInMovieTable(favoriteValue);
    }

    private void updateMovieInMovieTable(int favoriteValue) {
        ContentValues addFavorite = new ContentValues();
        addFavorite.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favoriteValue);

        int updatedRows = getActivity().getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                addFavorite,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovieId + ""}
        );

        if (updatedRows > 0) {
            Log.d("DB ACTION", updatedRows + " rows are updated in movies table");
        }
    }

    private void insertReviewsToDB() {
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();

        for (int i = 0; i < mReviewResponseResults.size(); i++) {
            ReviewResponseResult review = mReviewResponseResults.get(i);
            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.ReviewEntity.COLUMN_AUTHOR, review.getAuthor());
            contentValues.put(MovieContract.ReviewEntity.COLUMN_CONTENT, review.getContent());
            contentValues.put(MovieContract.ReviewEntity.COLUMN_URL, review.getUrl());
            contentValues.put(MovieContract.ReviewEntity.COLUMN_MOVIE_ID, mMovieId);

            contentValuesArrayList.add(contentValues);
        }

        ContentValues[] values = new ContentValues[contentValuesArrayList.size()];
        contentValuesArrayList.toArray(values);

        int insertedReviews = getContext().getContentResolver().bulkInsert(
                MovieContract.ReviewEntity.CONTENT_URI,
                values);

        Log.d("DB ACTION", insertedReviews + "reviews inserted into REVIEW Table");
    }

    private void insertVideosToDB() {
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();

        for (int i = 0; i < mVideoResponseResults.size(); i++) {
            VideoResponseResult video = mVideoResponseResults.get(i);
            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.VideoEntity.COLUMN_NAME, video.getName());
            contentValues.put(MovieContract.VideoEntity.COLUMN_KEY, video.getKey());
            contentValues.put(MovieContract.VideoEntity.COLUMN_MOVIE_ID, mMovieId);

            contentValuesArrayList.add(contentValues);
        }

        ContentValues[] values = new ContentValues[contentValuesArrayList.size()];
        contentValuesArrayList.toArray(values);

        int insertedVideos = getContext().getContentResolver().bulkInsert(
                MovieContract.VideoEntity.CONTENT_URI,
                values);

        Log.d("DB ACTION", insertedVideos + "videos inserted into Video Table");
    }

    private void removeReviewsFromDB() {
        int removedReviews = getActivity().getContentResolver().delete(
                MovieContract.ReviewEntity.CONTENT_URI,
                MovieContract.ReviewEntity.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovieId + ""}
        );

        Log.d("DB ACTION", removedReviews + "reviews deleted from REVIEW Table");
    }

    private void removeVideosFromDB() {
        int deletedVideos = getActivity().getContentResolver().delete(
                MovieContract.VideoEntity.CONTENT_URI,
                MovieContract.VideoEntity.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovieId + ""}
        );

        Log.d("DB ACTION", deletedVideos + "videos deleted from Video Table");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(REVIEWS_PARAM, (ArrayList<? extends Parcelable>) mReviewResponseResults);
        outState.putParcelableArrayList(VIDEOS_PARAM, (ArrayList<? extends Parcelable>) mVideoResponseResults);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mReviewResponseResults = savedInstanceState.getParcelableArrayList(REVIEWS_PARAM);
            mVideoResponseResults = savedInstanceState.getParcelableArrayList(VIDEOS_PARAM);
        }
    }
}
