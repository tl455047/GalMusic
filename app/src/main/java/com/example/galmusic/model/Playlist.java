package com.example.galmusic.model;

import android.net.Uri;

public class Playlist {
    private String name;
    private long id;
    private int numOfSong;
    private Uri artUri;

    public Playlist(String name, long id, int numOfSong, Uri artUri) {
        this.name = name;
        this.id = id;
        this.numOfSong = numOfSong;
        this.artUri = artUri;
    }

    public Playlist() {
        this.name = "";
        this.id = -1;
        this.numOfSong = 0;
        this.artUri = null;
    }

    public String getName() {
        return name;
    }

    public int getNumOfSong() {
        return numOfSong;
    }

    public long getId() {
        return id;
    }

    public Uri getArtUri() {
        return artUri;
    }
}
