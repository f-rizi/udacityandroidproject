package com.example.fatemeh.udacityandroidcourseproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fatemeh on 12/01/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                        MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL, " +
                        MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0" +
                        " );";

        final String SQL_CREATE_REVIEW_TABLE =
                " CREATE TABLE " + MovieContract.ReviewEntity.TABLE_NAME + " (" +
                        MovieContract.ReviewEntity._ID + "INTEGER PRIMARY KEY, " +
                        MovieContract.ReviewEntity.COLUMN_AUTHOR + " TEXT, " +
                        MovieContract.ReviewEntity.COLUMN_CONTENT + " TEXT, " +
                        MovieContract.ReviewEntity.COLUMN_URL + " TEXT, " +
                        MovieContract.ReviewEntity.COLUMN_MOVIE_ID + " INTEGER, " +
                        " FOREIGN KEY (" + MovieContract.ReviewEntity.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") " +
                        " );";


        final String SQL_CREATE_VIDEO_TABLE =
                " CREATE TABLE " + MovieContract.VideoEntity.TABLE_NAME + " (" +
                        MovieContract.VideoEntity.COLUMN_KEY + " TEXT, " +
                        MovieContract.VideoEntity.COLUMN_NAME + " TEXT, " +
                        MovieContract.VideoEntity.COLUMN_MOVIE_ID + " INTEGER, " +
                        " FOREIGN KEY (" + MovieContract.VideoEntity.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") " +
                        " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntity.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.VideoEntity.TABLE_NAME);
        onCreate(db);
    }
}
