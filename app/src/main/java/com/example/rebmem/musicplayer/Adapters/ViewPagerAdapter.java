package com.example.rebmem.musicplayer.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rebmem.musicplayer.Fragments.FavouritesFragment;
import com.example.rebmem.musicplayer.Fragments.SongListFragment;

import java.util.ArrayList;

/**
 * This customised class extends the FragmentPagerAdapter
 * that represents each page as a Fragment that is persistently
 * kept in the fragment manager as the user can return to the page
 **/
public class ViewPagerAdapter extends FragmentPagerAdapter {

    // All Songs list and playlist collections
    private final ArrayList<Fragment> fragments;
    private final ArrayList<String> titles;

    //Title shown for the tab
    private final String[] title = {"All SONGS", "FAVOURITES"};

    //Constructor
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

    //Returns the Fragment associated with a specified position.
    @NonNull
    @Override
    public Fragment getItem(int position) {
        //return fragments.get(position);
        switch (position) {
            case 0:
                return new SongListFragment();
            case 1:
                return new FavouritesFragment();
            default:
                return null;
        }
    }

    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    //Returns the number of fragments
    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * This method adds the fragments with the title to the View pager adapter
     **/
    public void addFragments(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }
}
