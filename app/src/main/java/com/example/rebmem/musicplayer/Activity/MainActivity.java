package com.example.rebmem.musicplayer.Activity;

import static android.content.SharedPreferences.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;

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

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_CODE = 1;
    public static ArrayList<SongFile> songFiles;
    private TabLayout tabLayout;
    private  ViewPager viewPager;
    private static SongListFragment songFragment = new SongListFragment();
    private static FavouritesFragment favouritesFragment = new FavouritesFragment();
    private String SORT_PREFERENCE = "SortOrder";

    //Shared preferences
    static boolean shuffleBoolean = false, repeatBoolean = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runtimePermission();
    }

    public void initViewPager(){
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(songFragment, "Songs");
        viewPagerAdapter.addFragments(favouritesFragment, "Favourites");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
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

    //Using Dexter Library
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

    public ArrayList<SongFile> getAllSongs(Context context) {
           SharedPreferences preferences = getSharedPreferences(SORT_PREFERENCE,MODE_PRIVATE);
           String sortOrder = preferences.getString("sort", "sortByDate");
            ArrayList<SongFile> tempSongsList = new ArrayList<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String orderBy = null;
            switch (sortOrder){
                case "sortByName":
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

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(SORT_PREFERENCE,MODE_PRIVATE).edit();
        switch( item.getItemId()) {
            case R.id.byName:
                editor.putString("sort", "sortByName");
                this.recreate();
              break;

            case R.id.byDate:
                editor.putString("sort", "sortByDate");
                this.recreate();
                break;
            case R.id.bySize:
                editor.putString("sort", "sortBySize");
                this.recreate();
                break;
        }


       return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        Log.e("on STOP " , " am I closing?");
        closeOptionsMenu();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.e("on Destroy " , " am I closing?");
        closeOptionsMenu();
        super.onDestroy();

    }
}
