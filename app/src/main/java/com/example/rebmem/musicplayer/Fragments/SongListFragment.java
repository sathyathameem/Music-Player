package com.example.rebmem.musicplayer.Fragments;

import static com.example.rebmem.musicplayer.Activity.MainActivity.songFiles;

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

public class SongListFragment extends Fragment {

    static RecyclerView recyclerView;
    public static SongAdapter songAdapter;

    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.songsRecyclerView);
        setContent();
    }

    private void setContent() {
        if(! (songFiles.size() < 1)){
            songAdapter = new SongAdapter(getContext(),songFiles);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void refreshSearchLoad(){
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
    }

}