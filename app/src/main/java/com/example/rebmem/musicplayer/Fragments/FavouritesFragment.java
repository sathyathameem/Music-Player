package com.example.rebmem.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rebmem.musicplayer.Adapters.SongAdapter;
import com.example.rebmem.musicplayer.Database.FavouritesDBOperations;
import com.example.rebmem.musicplayer.Model.ListType;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;

import java.util.ArrayList;

/**
 * This FavouritesFragment is a subclass of Fragment class
 * which is a piece of an activity for the Favourites playlist
 * in the main activity (It can be referred as subactivity )
 * **/
public class FavouritesFragment extends Fragment{

    public FavouritesFragment() {
        // Required empty public constructor
    }

    //DB Operations object
    private static FavouritesDBOperations favouritesDBOperations;

    //Favourite play list
    public static ArrayList<SongFile> favouriteSongs;

    //Recycler view to display the songs from the list
    static RecyclerView recyclerView;

    //The Song Adapter has the data set to show in the recycler view
    public static SongAdapter songAdapter;

    //The system calls this method when creating the fragment.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * The fragment instance is associated with an activity instance.
     * FavouritesDBOperations reference is obtained which is used in the fragment for further
     * initialization work
    **/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        favouritesDBOperations = new FavouritesDBOperations(context);
    }

    /**
     * The system calls this callback when it's time for the fragment to draw its user interface for the first time
     * This will inflate the fragment_favourites xml
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    /**
     * Activity and fragment instance have been created as well as the view hierarchy of the activity.
     * At this point, the recycler view is accessed with the findViewById() method.
     * setContent method is called where the content from the favourites table is obtained.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.favouritesRecyclerView);
        setContent();
    }

    /**
     * This method is called to set the content in the Favourites fragement
     * The content is obtained by querying all the songs for this favourites playlist
     * to be displayed , it is set in the song adapter which is placed in the recycler view of
     * this fragment class
     */
    private void setContent() {
        favouriteSongs = favouritesDBOperations.getAllFavourites();
        songAdapter = new SongAdapter(getContext(),favouriteSongs, ListType.FAVOURITE_SONGS);
        recyclerView.setAdapter(songAdapter);
        songAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
    }

    //Fragment becomes active.
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This method is called when the Favourites tab is selected to refresh the playlist with the updated songs.
     * The static songAdapter which is updated in the calling object is set here in the recycler view and it is refreshed
     */
    public void refreshLoad(){
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

    }
}