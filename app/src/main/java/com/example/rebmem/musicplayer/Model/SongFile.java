package com.example.rebmem.musicplayer.Model;

import java.io.Serializable;

/**
 * This class is a value object design pattern
 * This class hold the required attributes for every song.
 * It has setter and getter methods to set and get the value.
 * The object can also be created using the constructor with all the
 * values to be set are passed as parameters
 * This class implements Serializable interface to convert the conversion of a
 * Java object into a static stream (sequence) of bytes, which we can then save to a database
 * or transfer over a network
 *
 * @author sathya.thameem
 **/
public class SongFile implements Serializable {
    //Properties of the song
    private String path;
    private String title;
    private String id;
    private String album;
    private String duration;

    //Constructor
    public SongFile(String path, String title, String id, String album, String duration) {
        this.path = path;
        this.title = title;
        this.id = id;
        this.album = album;
        this.duration = duration;
    }

    //Empty Constructor
    public SongFile() {
    }

    // Getter and Setter methods
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
