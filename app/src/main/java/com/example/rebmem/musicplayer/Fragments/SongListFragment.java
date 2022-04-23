package com.example.rebmem.musicplayer.Fragments;

import static com.example.rebmem.musicplayer.Activity.MainActivity.songFiles;

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
import com.example.rebmem.musicplayer.R;

public class SongListFragment extends Fragment {

    RecyclerView recyclerView;
    SongAdapter songAdapter;

    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = view.findViewById(R.id.songsRecyclerView);
        //recyclerView.setHasFixedSize(true);
        if(! (songFiles.size() < 1)){
            songAdapter = new SongAdapter(getContext(),songFiles);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        }
        return view;
    }
}