package com.example.rebmem.musicplayer.Model;

import java.io.Serializable;

public class SongFile implements Serializable {
    private String path;
    private String title;

    private String id;
    private String album;
    private String duration;

    public SongFile(String path, String title, String id, String album, String duration) {
        this.path = path;
        this.title = title;
        this.id = id;
        this.album = album;
        this.duration = duration;
    }

    public SongFile(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
