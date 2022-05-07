package com.example.rebmem.musicplayer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rebmem.musicplayer.Model.SongFile;

import java.util.ArrayList;

/**
 * This class contains CRUD Operations  (Create, Read, Update, Delete) on
 * the favourites playlist where a song can be inserted to the playlist,
 * remove the song from the playlist and get all the songs from the favourites table
 * where the favourites playlist is stored.
 * @author sathya.thameem
 * **/


public class FavouritesDBOperations {
    public static final String TAG = "Favorites Database";
    //Declare the helper class and database
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;
    //Declare all the table columns
    private static final String[] allColumns = {
            FavouritesDBHelper.COLUMN_ID,
            FavouritesDBHelper.COLUMN_TITLE,
            FavouritesDBHelper.COLUMN_ALBUM,
            FavouritesDBHelper.COLUMN_PATH,
            FavouritesDBHelper.COLUMN_DURATION
    };
    //Constructor - where we create dbHelper object
    public FavouritesDBOperations(Context context) {
        dbHelper = new FavouritesDBHelper(context);
    }

    /**
     * This method is used to create and/or open the database for writing
     * */
    public void open() {
        Log.i(TAG, " Database Opened");
        database = dbHelper.getWritableDatabase();
    }

    /**
     * This method is used to close the database
     **/
    public void close() {
        Log.i(TAG, "Database Closed");
        dbHelper.close();
    }

    /**
     * This method is used to insert a song to the Favourites table of values
     * ContentValues object is created in this method to create an empty set of values
     * where the data from the Song object is put for each column before inserting into
     * the favourites table.
     * @param objSong
     **/
    public void addSongFav(SongFile objSong) {
        open();
        ContentValues values = new ContentValues();
        values.put(FavouritesDBHelper.COLUMN_TITLE, objSong.getTitle());
        values.put(FavouritesDBHelper.COLUMN_ALBUM, objSong.getAlbum());
        values.put(FavouritesDBHelper.COLUMN_PATH, objSong.getPath());
        values.put(FavouritesDBHelper.COLUMN_DURATION, objSong.getDuration());
        database.insertWithOnConflict(FavouritesDBHelper.TABLE_FAVOURITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }

    /**
     * This method fetches all the records from the favourites table
     * @return ArrayList<SongFile>
     * **/
    public ArrayList<SongFile> getAllFavourites() {
        open();
        Cursor cursor = database.query(FavouritesDBHelper.TABLE_FAVOURITES, allColumns,
                null, null, null, null, null);
        ArrayList<SongFile> favSongs = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                        SongFile song = new SongFile(cursor.getString(cursor.getColumnIndex(FavouritesDBHelper.COLUMN_PATH))
                        , cursor.getString(cursor.getColumnIndex(FavouritesDBHelper.COLUMN_TITLE))
                        , cursor.getString(cursor.getColumnIndex(FavouritesDBHelper.COLUMN_ID))
                         ,cursor.getString(cursor.getColumnIndex(FavouritesDBHelper.COLUMN_ALBUM))
                        ,cursor.getString(cursor.getColumnIndex(FavouritesDBHelper.COLUMN_DURATION)));
                favSongs.add(song);
            }
        }
        close();
        return favSongs;
    }

    /**
     * This method deletes the song that is selected to be deleted
     * @param songPath
     * **/
    public void removeSong(String songPath) {
        open();
        String whereClause =
                FavouritesDBHelper.COLUMN_PATH + "=?";
        String[] whereArgs = new String[]{songPath};

        database.delete(FavouritesDBHelper.TABLE_FAVOURITES, whereClause, whereArgs);
        close();
    }

}
