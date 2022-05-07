package com.example.rebmem.musicplayer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rebmem.musicplayer.Adapters.ViewPagerAdapter;
import com.example.rebmem.musicplayer.Fragments.FavouritesFragment;
import com.example.rebmem.musicplayer.Fragments.SongListFragment;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>This is the MainActivity Launcher class.
 * This activity is presented to the user as full screen window
 * This activity shows two tabs (Using Tablayout that provides horizontal layout to display tabs)
 * This has two tabs
 *  - Songs list tab
 *  - Playlist tab
 * This Tab Layout is used in integration with ViewPager so that the user can flip left and right through the tabs
 * The ViewPagerAdaptor will generate the content that the ViewPager shows
 * The options menu having search and sort functionality is added
 *
 * {@inheritDoc}
 * </p>
 *
 * @author Sathya Thameem
 **/
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //Variables used are declared here
    public static ArrayList<SongFile> songFiles;
    private TabLayout tabLayout;
    private  ViewPager viewPager;
    private static SongListFragment songFragment = new SongListFragment();
    private static FavouritesFragment favouritesFragment = new FavouritesFragment();
    private String SORT_PREFERENCE = "SortOrder";

    //Shared preferences
    static boolean shuffleBoolean = false, repeatBoolean = false;

   /**
    * This overridden method of an activity's lifecycle is called
    * when the activity is created. In this method the view is set up
    * after obtaining the necessary permission
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runtimePermission();
    }

    /**
     * This method is to request app permissions for
     *  - Reading external storage for Music files
     *  - Record Audio ( This is required for setting up the Visualizer during the audio playback
     *  This opens up a dialog box that has Allow and Deny buttons. In order to proceed using this
     *  app, the app should be granted permission.
     *  Using Dexter which is an Android library that simplifies the process of requesting permissions at runtime.
     *  Dexter frees our permission code from our activities and lets us write the logic anywhere.
     *  Once the permissions are granted,
     *  Songs are fetched from the local storage and the content for the viewpager is set up.
     *
     * **/
    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        songFiles = getAllSongs(getApplicationContext());
                        initViewPager();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * This method will get the view pager and tab layout view identified by the resource id from the activity_main.xml
     * Two fragments are added to the ViewPagerAdaptor which is set in ViewPager which is set to tab layout
     * - Songs list fragment
     * - playlist fragment
     * The view pager enables the user to switch between the tabs and shows the relevant data that is set up in the fragments
     * **/
    public void initViewPager(){
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(songFragment, "Songs");
        viewPagerAdapter.addFragments(favouritesFragment, "Favourites");
        viewPager.setAdapter(viewPagerAdapter);
        //Callback interface for responding to adapter changes.
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            /**
             * Position 0 is Songs list
             * Position 1 is Play list created during runtime
             * When the position 1( Favourites playlist tab is selected, it has to be refreshed
             * to show the song that is added during runtime
             * **/
            @Override
            public void onPageSelected(int position) {
                if (position == 1){
                   favouritesFragment.refreshLoad();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * This method fetches all the songs (Audio) from the Media store in the device.
     * The songs fetched will be listed sorted by date as default
     * The Uri specifies the path of the audio files stored device
     * All the audio files are queried and returned as a Cursor object.
     * The Cursor class allows interaction with a dataset returned from a the above query.
     * The dataset values for each audio file is mapped to teh corresponding attributes of a Song Value object
     * which is added to the songs list
     * This method is also called when sorting in different order, the sort order is shared to this method using
     * Shared preferences (Interface for accessing and modifying preference data returned by Context.getSharedPreferences(String, int))
     * @param context
     * @return ArrayList<SongFile>
     * **/
    public ArrayList<SongFile> getAllSongs(Context context) {
           SharedPreferences preferences = getSharedPreferences(SORT_PREFERENCE,MODE_PRIVATE);
           String sortOrder = preferences.getString("sort", "sortByDate");
            ArrayList<SongFile> tempSongsList = new ArrayList<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String orderBy = null;
            switch (sortOrder){
                case "sortByTitle":
                    orderBy = MediaStore.MediaColumns.TITLE + " ASC";
                    break;
                case "sortByDate":
                    orderBy = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                    break;
                case "sortBySize":
                    orderBy = MediaStore.MediaColumns.SIZE + " DESC";
                    break;
            }
            String[] projections = {
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.SIZE
                        };

            Cursor cursor = context.getContentResolver().query(uri, projections,null, null, orderBy);
            if(cursor != null){
                while(cursor.moveToNext()){
                    String album = cursor.getString(0);
                    String title = cursor.getString(1);
                    String duration = cursor.getString(2);
                    String path = cursor.getString(3);
                    String id = cursor.getString(4);
                    Log.e(title + "The Date added ", cursor.getString((5)));
                    Log.e(title + "Size ", cursor.getString((6)));
                    SongFile songFile = new SongFile(path,title,id,album,duration);
                    tempSongsList.add(songFile);
                }
                cursor.close();
            }
            return tempSongsList;
        }

    /**
     * onCreateOptionsMenu() is used to specify the options menu for an activity.
     * In this method, the menu resource (defined in XML) will be inflated into the
     * Menu provided in the callback. After inflating the menu defined by search.xml
     * setting the setOnQueryTextListener on the searchView
     * (Callbacks for changes to the query text)
     * @param menu
     *
     * **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.searchOption);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     * The Songs list is iterated to see if the search text matched the title
     * and adds to the new list and that list is set in the main song fragment
     * displayed to the user
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        String userText = newText.toLowerCase();
        ArrayList<SongFile> searchList = new ArrayList<>();
        for(SongFile song : songFiles){
            if(song.getTitle().toLowerCase().contains(userText)){
                searchList.add(song);
            }
        }
        songFragment.songAdapter.updateSearchList(searchList);
        songFragment.refreshSearchLoad();

        return true;
    }

    /**
     * Called when the menu item is selected
     * The menu item is put in the shared preferences editor
     * and the activity is recreated to display the main songs list with the
     * selected sort order set in the editor which is retrieved in the getAllSongs method.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(SORT_PREFERENCE,MODE_PRIVATE).edit();
        switch( item.getItemId()) {
            case R.id.byTitle:
                editor.putString("sort", "sortByTitle");
                editor.apply();
                recreateActivity();
               break;

            case R.id.byDate:
                editor.putString("sort", "sortByDate");
                editor.apply();
                recreateActivity();
                break;
            case R.id.bySize:
                editor.putString("sort", "sortBySize");
                editor.apply();
                recreateActivity();
                break;
        }


       return super.onOptionsItemSelected(item);
    }

    /**
     * Cause this Activity to be recreated with a new instance.
     * Introduced a delay as the app was crashing when item is selected
     * as the dialog was active when the activity's destroy method is called
     * This delay gives a few minutes for the menu dialog to close before the
     * activity is recreated
     */
    private void recreateActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        }, 1000);
    }

}
