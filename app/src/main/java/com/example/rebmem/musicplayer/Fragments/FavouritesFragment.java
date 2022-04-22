package com.example.rebmem.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;

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


public class FavouritesFragment extends Fragment {

    public FavouritesFragment() {
        // Required empty public constructor
    }
    private static FavouritesDBOperations favouritesDBOperations;
    public static ArrayList<SongFile> favouriteSongs;

    RecyclerView recyclerView;
    SongAdapter songAdapter;

    public ArrayList<SongFile> favouritesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerView = view.findViewById(R.id.favouritesRecyclerView);
        recyclerView.setHasFixedSize(true);
        favouriteSongs = getAllFavourites(getContext());
        if(! (favouriteSongs.size() < 1)){
            songAdapter = new SongAdapter(getContext(),favouriteSongs, ListType.FAVOURITE_SONGS);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        }else{
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    public static ArrayList<SongFile> getAllFavourites(Context context) {
        favouriteSongs = new ArrayList<>();
        favouritesDBOperations = new FavouritesDBOperations(context);
        favouriteSongs = favouritesDBOperations.getAllFavorites();
        return favouriteSongs;
    }

}