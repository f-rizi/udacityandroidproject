package com.example.fatemeh.udacityandroidcourseproject.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.activities.MainActivity;
import com.example.fatemeh.udacityandroidcourseproject.db.MovieContract;
import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponse;
import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.utilities.APIEndpoints;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by fatemeh on 21/01/16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    public static final int SYNC_INTERVAL = 60 * 60 * 6;
    private static final int MOVIE_NOTIFICATION_ID = 1001;
    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account account = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));


        if (accountManager.getPassword(account) == null) {

            if (!accountManager.addAccountExplicitly(account, "", null)) {
                return null;
            }

            ContentResolver.addPeriodicSync(account,
                    context.getString(R.string.content_authority),
                    Bundle.EMPTY,
                    SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(account,
                    context.getString(R.string.content_authority),
                    true);

            syncImmediately(context);
        }

        return account;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
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

        getPopularMovies(apiEndpoints);
        getHighRatedMovies(apiEndpoints);
    }

    private void getPopularMovies(APIEndpoints apiEndpoints) {
        String sortOrder = getContext().getResources().getString(R.string.pref_movie_sort_popular_value);

        Call<DiscoverMovieResponse> call = apiEndpoints.getDiscoverMovieResponse(sortOrder, APIEndpoints.API);

        call.enqueue(new Callback<DiscoverMovieResponse>() {
            @Override
            public void onResponse(Response<DiscoverMovieResponse> response, Retrofit retrofit) {
                List<DiscoverMovieResponseResult> movies = response.body().getResults();
                storeMoviesInDB(movies);
                sendNotification();
            }

            @Override
            public void onFailure(Throwable t) {
                String s = t.getLocalizedMessage();
                Log.v("ERROR", s);
            }
        });
    }


    private void getHighRatedMovies(APIEndpoints apiEndpoints) {
        String sortOrder = getContext().getResources().getString(R.string.pref_movie_sort_rated_value);

        Call<DiscoverMovieResponse> call = apiEndpoints.getDiscoverMovieResponse(sortOrder, APIEndpoints.API);

        call.enqueue(new Callback<DiscoverMovieResponse>() {
            @Override
            public void onResponse(Response<DiscoverMovieResponse> response, Retrofit retrofit) {
                List<DiscoverMovieResponseResult> movies = response.body().getResults();
                storeMoviesInDB(movies);
                sendNotification();
            }

            @Override
            public void onFailure(Throwable t) {
                String s = t.getLocalizedMessage();
                Log.v("ERROR", s);
            }
        });
    }

    private void storeMoviesInDB(List<DiscoverMovieResponseResult> movies) {
        ArrayList<ContentValues> contentValuesList = new ArrayList<>();

        for (int i = 0; i < movies.size(); i++) {
            DiscoverMovieResponseResult movie = movies.get(i);


            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());

            contentValuesList.add(contentValues);
        }

        ContentValues[] values = new ContentValues[contentValuesList.size()];
        contentValuesList.toArray(values);

        int items = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);

        Log.d("dbAction", items + " movies inserted to movies table");

    }

    private void sendNotification() {
        Context context = getContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean displayNotifications = prefs.getBoolean(context.getString(R.string.prefs_notification_key), true);

        if (!displayNotifications) {
            return;
        }

        String lastNotificationKey = context.getString(R.string.prefs_notification_last_key);
        long lastSync = prefs.getLong(lastNotificationKey, 0);

        if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {

            int smallIcon = R.mipmap.ic_launcher;
            Bitmap largeIcon = BitmapFactory.decodeResource(
                    getContext().getResources(),
                    R.mipmap.ic_launcher);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(getContext().getString(R.string.app_name))
                    .setContentText(getContext().getString(R.string.prefs_notification_title));

            Intent notificationIntent = new Intent(getContext(), MainActivity.class);


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(MOVIE_NOTIFICATION_ID, builder.build());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastNotificationKey, System.currentTimeMillis());
            editor.commit();
        }
    }
}
