package com.example.rebmem.musicplayer.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.rebmem.musicplayer.Activity.MainActivity;
import com.example.rebmem.musicplayer.Adapters.SongAdapter;
import com.example.rebmem.musicplayer.Database.FavouritesDBOperations;
import com.example.rebmem.musicplayer.Model.ListType;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;

import java.util.ArrayList;
import java.util.Objects;


public class FavouritesFragment extends Fragment{

    public FavouritesFragment() {
        // Required empty public constructor
    }
    private static FavouritesDBOperations favouritesDBOperations;
    public static ArrayList<SongFile> favouriteSongs;

    static RecyclerView recyclerView;
    public static SongAdapter songAdapter;

    public ArrayList<SongFile> favouritesList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        favouritesDBOperations = new FavouritesDBOperations(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.favouritesRecyclerView);
        setContent();
    }

    private void setContent() {
        favouriteSongs = favouritesDBOperations.getAllFavorites();
        songAdapter = new SongAdapter(getContext(),favouriteSongs, ListType.FAVOURITE_SONGS);
        recyclerView.setAdapter(songAdapter);
        songAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public static ArrayList<SongFile> getAllFavourites(Context context) {
        favouriteSongs = new ArrayList<>();
        favouriteSongs = favouritesDBOperations.getAllFavorites();
        return favouriteSongs;
    }

    public void refreshLoad(){
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

    }
}