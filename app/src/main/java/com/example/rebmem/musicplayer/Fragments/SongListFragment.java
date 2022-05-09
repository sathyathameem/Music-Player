package com.example.rebmem.musicplayer.Fragments;

import static com.example.rebmem.musicplayer.Activity.MainActivity.songFiles;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rebmem.musicplayer.Adapters.SongAdapter;
import com.example.rebmem.musicplayer.R;

/**
 * This SongsListFragment is a subclass of Fragment class
 * which is a piece of an activity for the listing all songs
 * in the main activity (It can be referred as subactivity )
 **/
public class SongListFragment extends Fragment {

    //The Song Adapter has the data set to show in the recycler view
    public static SongAdapter songAdapter;
    //Recycler view to display the songs from the list
    static RecyclerView recyclerView;

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

    /**
     * The system calls this callback when it's time for the fragment to draw its user interface for the first time
     * This will inflate the fragment_song_list xml
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    /**
     * Activity and fragment instance have been created as well as the view hierarchy of the activity.
     * At this point, the recycler view is accessed with the findViewById() method.
     * setContent method is called where the content from the all song list is obtained.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.songsRecyclerView);
        setContent();
    }

    //sets the content (all songs) dataset to the song adapter which is set in the recycler view to be showed as a list
    private void setContent() {
        if (!(songFiles.size() < 1)) {
            songAdapter = new SongAdapter(getContext(), songFiles);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //This method is called during search operation where the new search list will be shown in the All songs fragment
    public void refreshSearchLoad() {
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

}