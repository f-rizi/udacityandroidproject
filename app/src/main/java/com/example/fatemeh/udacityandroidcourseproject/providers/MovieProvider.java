package com.example.fatemeh.udacityandroidcourseproject.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.fatemeh.udacityandroidcourseproject.db.MovieContract;
import com.example.fatemeh.udacityandroidcourseproject.db.MovieDbHelper;

public class MovieProvider extends ContentProvider {

    private MovieDbHelper mMovieDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE_LIST = 100;
    static final int MOVIE_ITEM_BY_MOVIE_ID = 101;

    static final int REVIEW_LIST = 200;
    static final int REVIEW_ITEM_BY_MOVIE_ID = 201;

    static final int VIDEO_LIST = 300;
    static final int VIDEO_ITEM_BY_MOVIE_ID = 301;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_LIST);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_ITEM_BY_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW_LIST);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", REVIEW_ITEM_BY_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEO_LIST);
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/#", VIDEO_ITEM_BY_MOVIE_ID);

        return matcher;
    }

    public MovieProvider() {
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);


        switch (match) {
            case MOVIE_LIST:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM_BY_MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW_LIST:
                return MovieContract.ReviewEntity.CONTENT_TYPE;
            case REVIEW_ITEM_BY_MOVIE_ID:
                return MovieContract.ReviewEntity.CONTENT_ITEM_TYPE;
            case VIDEO_LIST:
                return MovieContract.VideoEntity.CONTENT_TYPE;
            case VIDEO_ITEM_BY_MOVIE_ID:
                return MovieContract.VideoEntity.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;
        long id;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_LIST:
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MOVIE_ITEM_BY_MOVIE_ID:
                id = MovieContract.MovieEntry.getIdFromUri(uri);

                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {Long.toString(id)},
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEW_LIST:
                cursor = db.query(MovieContract.ReviewEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEW_ITEM_BY_MOVIE_ID:
                id = MovieContract.ReviewEntity.getIdFromUri(uri);
                cursor = db.query(MovieContract.ReviewEntity.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntity.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder);
                break;

            case VIDEO_LIST:
                cursor = db.query(MovieContract.VideoEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case VIDEO_ITEM_BY_MOVIE_ID:
                id = MovieContract.VideoEntity.getIdFromUri(uri);
                cursor = db.query(MovieContract.VideoEntity.TABLE_NAME,
                        projection,
                        MovieContract.VideoEntity.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        long insertedID;
        Uri insertedUri;

        switch (match) {
            case MOVIE_LIST:
                insertedID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                if (insertedID > 0) {
                    insertedUri = MovieContract.MovieEntry.buildMovieUri(insertedID);
                } else {
                    throw new SQLException("Failed to insert row into MOVIE");
                }
                break;

            case REVIEW_LIST:
                insertedID = db.insert(MovieContract.ReviewEntity.TABLE_NAME, null, values);

                if (insertedID > 0) {
                    insertedUri = MovieContract.ReviewEntity.buildReviewUri(insertedID);
                } else {
                    throw new SQLException("Failed to insert row into REVIEW");
                }
                break;

            case VIDEO_LIST:
                insertedID = db.insert(MovieContract.VideoEntity.TABLE_NAME, null, values);

                if (insertedID > 0) {
                    insertedUri = MovieContract.VideoEntity.buildVideoUri(insertedID);
                } else {
                    throw new SQLException("Failed to insert row into VIDEO");
                }
                break;
            default:
                throw new UnsupportedOperationException("unknown uri" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return insertedUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int rowsUpdated;

        switch (match) {
            case MOVIE_LIST:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case REVIEW_LIST:
                rowsUpdated = db.update(MovieContract.ReviewEntity.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case VIDEO_LIST:
                rowsUpdated = db.update(MovieContract.VideoEntity.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri" + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int rowsDeleted;

        if ( null == selection ) {
            selection = "1";
        }

        switch (match) {
            case MOVIE_LIST:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case REVIEW_LIST:
                rowsDeleted = db.delete(MovieContract.ReviewEntity.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case VIDEO_LIST:
                rowsDeleted = db.delete(MovieContract.VideoEntity.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri" + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_LIST: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case REVIEW_LIST: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.ReviewEntity.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case VIDEO_LIST: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.VideoEntity.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
