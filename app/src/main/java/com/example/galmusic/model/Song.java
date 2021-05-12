package com.example.galmusic.model;

import android.net.Uri;

/**
 * Container for information about each song.
 *
 */
public class Song {

    private final Uri uri;
    private final String name;
    private final int duration;
    private final String album;
    private final String artist;
    private final long id;
    private final Uri albumArtUri;
    public Song(long id, Uri uri, String name, int duration, String album, String artist, Uri albumArtUri) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.album = album;
        this.artist = artist;
        this.id = id;
        this.albumArtUri = albumArtUri;
    }

    public Song() {
        uri = null;
        name = "";
        duration = 0;
        album = "";
        artist = "";
        id = 0;
        albumArtUri = null;
    }

    public String getName() {
        return this.name;
    }

    public Uri getUri() {
        return this.uri;
    }

    public int getDuration() {
        return this.duration;
    }

    /*public int getSize() {
        return this.size;
    }*/

    public String getAlbum() {
        return this.album;
    }

    public String getArtist() {
        return this.artist;
    }

    public long getId() {  return id; }

    public Uri getAlbumArtUri() { return albumArtUri; }
}