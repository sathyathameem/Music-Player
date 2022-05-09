package com.example.rebmem.musicplayer.Adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebmem.musicplayer.Activity.PlayerActivity;
import com.example.rebmem.musicplayer.Database.FavouritesDBOperations;
import com.example.rebmem.musicplayer.Fragments.FavouritesFragment;
import com.example.rebmem.musicplayer.Fragments.SongListFragment;
import com.example.rebmem.musicplayer.Model.ListType;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

/**
 * This customised Adaptor class that extends RecyclerView.Adapter
 * that provide a binding from an app-specific data set to views that are displayed
 * within a RecyclerView
 *
 * @author sathya.thameem
 **/

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    static FavouritesDBOperations favoritesOperations;
    //Create objects for all the classes to be used in this class
    private final Context context;
    private final ArrayList<SongFile> sFiles;
    private final ListType lType;
    private ArrayList<SongFile> searchedFiles;

    /**
     * Constructor with ListType (Songs list or playlist(Favourites)
     *
     * @param sContext
     * @param sFiles
     * @param eType
     */
    public SongAdapter(Context sContext, ArrayList<SongFile> sFiles, ListType eType) {
        this.sFiles = sFiles;
        this.context = sContext;
        this.lType = eType;
    }

    //Constructor to set the default ALL_SONGS
    public SongAdapter(Context sContext, ArrayList<SongFile> sFiles) {
        this(sContext, sFiles, ListType.ALL_SONGS);
    }

    /**
     * This method is called when the RecyclerView needs a new RecyclerView.ViewHolder
     * This can be songsRecyclerView or favouritesRecyclerView
     * This new ViewHolder is constructed with a new View that represents each individual song
     * It is inflated from the song_items.xml layout file
     */

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_items, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * This overriding method is called by RecyclerView to display the Song data at the specified position
     * The Song item has the album cover, title and a context menu for every position.
     * Each row (Song) is clickable and navigates to player activity for playback
     * The song can also be added to favourites playlist by clicking on add Favourites context menu.
     * Song can be deleted (Doesn't work in this API) from the storage
     * In Favourites playlist song list, every row as a delete icon to delete the song from the Favourites playlist
     **/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //sets the title of the song in that specific position
        holder.song_name.setText(sFiles.get(position).getTitle());
        //sets the album picture or default image for the song at that position
        byte[] picture = getAlbumPicture(sFiles.get(position).getPath());
        if (picture != null) {
            Glide.with(context).asBitmap().load(picture).into(holder.album_art);
        } else {
            Glide.with(context).load(R.drawable.music_note).into(holder.album_art);
        }
        //sets the ordinal of the enumeration constant that is shown
        holder.viewSwitcher.setDisplayedChild(lType.ordinal());

        //Interface definition for a callback to be invoked when a when the item view (song) is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the item view is clicked where the destination player activity is set
             * in the intent. The position and the songs list are put in the intent and the player
             * activity is started.
             * **/
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position", position).putExtra("songs", sFiles);
                context.startActivity(intent);
            }
        });
        //Interface definition for a callback to be invoked when a when the more option menu for each song is clicked
        holder.moreOptions.setOnClickListener(new View.OnClickListener() {
            /**
             * When the more options menu is clicked, options.xml is inflated to show the
             * menu options and shows the popup menu.
             * When Add Favourites menu item is clicked,
             *          the song in that position is added to the favourites playlist
             * When Delete menu item is clicked,
             *          the song in that position should be deleted from the list and storage
             * **/
            @Override
            public void onClick(View v) {
                PopupMenu popupmenu = new PopupMenu(context, v);
                popupmenu.getMenuInflater().inflate(R.menu.options, popupmenu.getMenu());
                popupmenu.show();
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addFav:
                                addToFavourites(position, context);
                                break;
                            case R.id.delete:
                                Toast.makeText(context, "Delete clicked", Toast.LENGTH_SHORT).show();
                                deleteSong(position, v);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + item.getItemId());
                        }
                        return true;
                    }
                });
            }
        });
        /**
         *  This option is available for the playlist (Favourites) fragment
         *  and on click of that delete icon at that song position, deletes the song
         *  from the playlist
         * **/
        holder.deleteFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOption(position);

            }
        });
    }

    /**
     * This method builds the song object with the song properties
     * and adds to the playlist favourites list.
     * Inserts the song object to the favourites table
     * Gets the updated favourites from the Database table and sets in the song adapter
     * in Favourites fragments
     **/
    private void addToFavourites(int position, Context context) {
        Toast.makeText(context, "Song added to Favourites playlist", Toast.LENGTH_SHORT).show();
        SongFile favList = new SongFile(sFiles.get(position).getPath(),
                sFiles.get(position).getTitle(),
                sFiles.get(position).getId(),
                sFiles.get(position).getAlbum(),
                sFiles.get(position).getDuration());
        favoritesOperations = new FavouritesDBOperations(context);
        favoritesOperations.addSongFav(favList);
        ArrayList<SongFile> newList = favoritesOperations.getAllFavourites();
        FavouritesFragment.songAdapter = new SongAdapter(context, newList, ListType.FAVOURITE_SONGS);

    }

    /**
     * This method is used to delete the song from the song list and from the storage.
     * This method is unused as the song cannot be deleted as the API level is 19 and above
     **/
    private void deleteSong(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(sFiles.get(position).getId()));

        File file = new File(sFiles.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            sFiles.remove(position);
            context.getContentResolver().delete(contentUri, null, null);
            sFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, sFiles.size());
            Snackbar.make(v, "Song Deleted ", Snackbar.LENGTH_LONG).show();
        } else {
            //may be file in sd card and API level is 19 and above
            Snackbar.make(v, "Can't delete the song ", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return sFiles.size();
    }

    //Get the album cover picture
    private byte[] getAlbumPicture(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] picture = retriever.getEmbeddedPicture();
        retriever.release();
        return picture;
    }

    /**
     * This method is called when delete option is clicked to remove the favourites list
     * Calls the delete operation on the favourites table
     * And removes the song from the favourites playlist
     * notify the data set changed
     **/
    private void deleteOption(int position) {
        //showDialog(sFiles.get(position).getPath(), position);
        favoritesOperations = new FavouritesDBOperations(context);
        if (sFiles.get(position).getPath() != null) {
            favoritesOperations.removeSong(sFiles.get(position).getPath());
            sFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, sFiles.size());
            notifyDataSetChanged();
            Toast.makeText(context, "Favourite Song deleted", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This method is called to update the list with the result obtained from
     * the search query and sets the updated list in the song adapter
     **/
    public void updateSearchList(ArrayList<SongFile> searchedList) {
        searchedFiles = new ArrayList<>();
        searchedFiles.addAll(searchedList);
        SongListFragment.songAdapter = new SongAdapter(context, searchedFiles);
    }

    /**
     * Inner class that extends recyclerviewViewHolder which sets the item view on the Recycler View
     * Each individual element in the list is defined by a view holder object.
     **/
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView song_name;
        ImageView album_art, moreOptions, deleteFavourite;
        ViewSwitcher viewSwitcher;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            song_name.setSelected(true);
            album_art = itemView.findViewById(R.id.song_image);
            moreOptions = itemView.findViewById(R.id.more_options);
            deleteFavourite = itemView.findViewById(R.id.delete_favourites);
            viewSwitcher = itemView.findViewById(R.id.switchImage);

        }
    }

}
