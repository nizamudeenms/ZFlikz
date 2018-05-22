package com.zyta.zflikz.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;
    final String SQL_CREATE_POPULAR_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.POPULAR_MOVIE_TABLE + " (" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_OVERVIEW + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_POSTER_URL + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_TITLE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_FAVORITE + " CHAR NOT NULL DEFAULT 'N', " +
            " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    final String SQL_CREATE_TOP_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.TOP_MOVIE_TABLE + " (" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_OVERVIEW + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_POSTER_URL + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_TITLE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_FAVORITE + " CHAR NOT NULL DEFAULT 'N', " +
            " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


    final String SQL_CREATE_FAV_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.FAV_MOVIE_TABLE + " (" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_OVERVIEW + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_POSTER_URL + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_TITLE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_FAVORITE + " CHAR NOT NULL DEFAULT 'N', " +
            " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);
        Log.i("Table Created", "POPULAR_MOVIE_TABLE TableCreated");

        db.execSQL(SQL_CREATE_TOP_MOVIE_TABLE);
        Log.i("Table Created", "TOP_MOVIE TableCreated");

        db.execSQL(SQL_CREATE_FAV_MOVIE_TABLE);
        Log.i("Table Created", "FAV_MOVIE TableCreated");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.POPULAR_MOVIE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TOP_MOVIE_TABLE);
        onCreate(db);
        Log.i("DB Dropped", "DB Dropped");
    }
}

