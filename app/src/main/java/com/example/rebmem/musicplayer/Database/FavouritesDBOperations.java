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

public class FavouritesDBOperations {
    public static final String TAG = "Favorites Database";

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            FavouritesDBHelper.COLUMN_ID,
            FavouritesDBHelper.COLUMN_TITLE,
            FavouritesDBHelper.COLUMN_ALBUM,
            FavouritesDBHelper.COLUMN_PATH,
            FavouritesDBHelper.COLUMN_DURATION
    };

    public FavouritesDBOperations(Context context) {

        dbHelper = new FavouritesDBHelper(context);
    }

    public void open() {
        Log.i(TAG, " Database Opened");
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(TAG, "Database Closed");
        dbHelper.close();
    }

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

    public void removeSong(String songPath) {
        open();
        String whereClause =
                FavouritesDBHelper.COLUMN_PATH + "=?";
        String[] whereArgs = new String[]{songPath};

        database.delete(FavouritesDBHelper.TABLE_FAVOURITES, whereClause, whereArgs);
        close();
    }

}
