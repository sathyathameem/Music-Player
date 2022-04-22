package com.example.rebmem.musicplayer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavouritesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritesList.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_FAVOURITES      = "favorites";
    public static final String COLUMN_ID             = "songID";
    public static final String COLUMN_TITLE          = "title";
    public static final String COLUMN_ALBUM          = "subtitle";
    public static final String COLUMN_PATH           = "songpath";
    public static final String COLUMN_DURATION       = "duration";



    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_FAVOURITES + " (" + COLUMN_ID
            + " INTEGER, " + COLUMN_TITLE + " TEXT, " + COLUMN_ALBUM
            + " TEXT, " + COLUMN_DURATION
            + " TEXT, " +COLUMN_PATH + " TEXT PRIMARY KEY " + ")";

    public FavouritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL(TABLE_CREATE);

    }
}
