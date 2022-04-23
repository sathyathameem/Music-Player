package com.example.rebmem.musicplayer.Adapters;

import static com.example.rebmem.musicplayer.Fragments.FavouritesFragment.favouriteSongs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rebmem.musicplayer.Fragments.FavouritesFragment;
import com.example.rebmem.musicplayer.Fragments.SongListFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;

    private String title[] = {"All SONGS", "FAVORITES"};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //return fragments.get(position);
        switch(position){
            case 0: return new SongListFragment();
            case 1: return new FavouritesFragment();
            default: return null;
        }
    }

    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragments(Fragment fragment, String title){
        fragments.add(fragment);
        titles.add(title);
    }



}
