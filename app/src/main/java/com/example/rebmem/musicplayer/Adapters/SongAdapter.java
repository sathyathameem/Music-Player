package com.example.rebmem.musicplayer.Adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rebmem.musicplayer.Activity.PlayerActivity;
import com.example.rebmem.musicplayer.Database.FavouritesDBOperations;
import com.example.rebmem.musicplayer.Fragments.FavouritesFragment;
import com.example.rebmem.musicplayer.Fragments.SongListFragment;
import com.example.rebmem.musicplayer.Model.ListType;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<SongFile> sFiles;
    private final ListType lType;
    static FavouritesDBOperations favoritesOperations;

    public SongAdapter(Context sContext, ArrayList<SongFile> sFiles, ListType eType){
        this.sFiles = sFiles;
        this.context = sContext;
        this.lType = eType;
    }

    public SongAdapter(Context sContext, ArrayList<SongFile> sFiles){
        this(sContext,sFiles,ListType.ALL_SONGS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.song_name.setText(sFiles.get(position).getTitle());
        byte[] picture = getAlbumPicture(sFiles.get(position).getPath());
        if(picture != null){
            Glide.with(context).asBitmap().load(picture).into(holder.album_art);
        }else{
            Glide.with(context).load(R.drawable.music_note).into(holder.album_art);
        }
        holder.viewSwitcher.setDisplayedChild(lType.ordinal());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position",position).putExtra("songs",sFiles);
                context.startActivity(intent);
            }
        });

        holder.moreOptions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu popupmenu = new PopupMenu(context, v);
                popupmenu.getMenuInflater().inflate(R.menu.options,popupmenu.getMenu());
                popupmenu.show();
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.addFav:
                                Toast.makeText(context,"Add to Favourites clicked", Toast.LENGTH_SHORT).show();
                                SongFile favList = new SongFile(sFiles.get(position).getPath(),
                                                                sFiles.get(position).getTitle(),
                                                                sFiles.get(position).getId(),
                                                                sFiles.get(position).getAlbum(),
                                                                sFiles.get(position).getDuration());
                                favoritesOperations = new FavouritesDBOperations(context);
                                favoritesOperations.addSongFav(favList);
                                ArrayList<SongFile> newList = favoritesOperations.getAllFavorites();
                                new FavouritesFragment().songAdapter = new SongAdapter(context,newList, ListType.FAVOURITE_SONGS);
                                break;
                            case R.id.delete:
                                Toast.makeText(context,"Delete clicked", Toast.LENGTH_SHORT).show();
                                deleteSong(position, v);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + item.getItemId());
                        }
                        return true;
                    }
                });
            }
        });

        holder.deleteFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOption(position);

            }
        });


    }

    private void deleteSong(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(sFiles.get(position).getId()));

        File file = new File(sFiles.get(position).getPath());
        boolean deleted = file.delete();
        if(deleted){
            sFiles.remove(position);
            context.getContentResolver().delete(contentUri,null, null);
            sFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,sFiles.size());
            Snackbar.make(v,"Song Deleted ",Snackbar.LENGTH_LONG).show();
        }else {
            //may be file in sd card and API level is 19 and above
            Snackbar.make(v,"Can't delete the song ",Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return sFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView song_name;
        ImageView album_art, moreOptions,deleteFavourite;
        ViewSwitcher viewSwitcher;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            song_name.setSelected(true);
            album_art = itemView.findViewById(R.id.song_image);
            moreOptions = itemView.findViewById(R.id.more_options);
            deleteFavourite = itemView.findViewById(R.id.delete_favourites);
            viewSwitcher = itemView.findViewById(R.id.switchImage);

        }
    }

    private byte[] getAlbumPicture(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] picture = retriever.getEmbeddedPicture();
        retriever.release();
        return picture;
  }
    //working fine.. It is removing from the list as well - deleteFavourites
    private void deleteOption(int position) {
        //showDialog(sFiles.get(position).getPath(), position);
        Log.e("position &&&&&&&&&&&&&&&&&&&&", position+"");
        Log.e("position &&&&&&&&&&&&&&&&&&&&", sFiles.get(position).getPath()+"");
        favoritesOperations = new FavouritesDBOperations(context);
        if(sFiles.get(position).getPath() != null){
            favoritesOperations.removeSong(sFiles.get(position).getPath());
            sFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,sFiles.size());
            notifyDataSetChanged();
            Toast.makeText(context, "Favourite Song deleted", Toast.LENGTH_SHORT).show();
        }


    }
    //Yes and No Button is not showing
    private void showDialog(final String index, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete ?")
                .setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoritesOperations.removeSong(index);
                        sFiles.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,sFiles.size());
                        notifyDataSetChanged();
                        Toast.makeText(context, "Favourite Song deleted", Toast.LENGTH_SHORT).show();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
