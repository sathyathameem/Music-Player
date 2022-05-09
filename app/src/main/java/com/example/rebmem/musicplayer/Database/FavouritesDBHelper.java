package com.example.rebmem.musicplayer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;


/**
 * Customized DB Helper class that extends SQLiteOpenHelper
 * This helper class is to manage database creation and version management
 * This helper object is used to create, open, and/or manage a database
 * @author sathya.thameem
 **/
public class FavouritesDBHelper extends SQLiteOpenHelper {
    //Table Name
    public static final String TABLE_FAVOURITES = "favorites";
    //Table Columns
    public static final String COLUMN_ID = "songID";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ALBUM = "subtitle";
    public static final String COLUMN_PATH = "songpath";
    public static final String COLUMN_DURATION = "duration";
    //Database Info
    private static final String DATABASE_NAME = "favoritesList.db";
    private static final int DATABASE_VERSION = 1;
    //Query to create table
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_FAVOURITES + " (" + COLUMN_ID
            + " INTEGER, " + COLUMN_TITLE + " TEXT, " + COLUMN_ALBUM
            + " TEXT, " + COLUMN_DURATION
            + " TEXT, " + COLUMN_PATH + " TEXT PRIMARY KEY " + ")";

    //Constructor
    public FavouritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the FIRST time.
     * If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
     **/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);

    }

    /**
     * Called when the database needs to be upgraded.
     * This method will only be called if a database already exists on disk with the same DATABASE_NAME,
     * but the DATABASE_VERSION is different than the version of the database that exists on disk.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL(TABLE_CREATE);

    }
}
