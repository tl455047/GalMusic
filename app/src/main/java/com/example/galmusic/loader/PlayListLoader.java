package com.example.galmusic.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.example.galmusic.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlayListLoader {
    public static final List<Playlist> getPlayList(Context context) {

        List<Playlist> playList = new ArrayList<Playlist>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Playlists.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        }
        //select column
        String[] projection = new String[] {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME,
        };
        //select condition
        //String selection = MediaStore.Audio.Media.DURATION +
        //       " >= ?";
        //String[] selectionArgs = new String[] {
        //        String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))};
        String sortOrder = MediaStore.Audio.Playlists.NAME + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                //selection,
                null,
                //selectionArgs,
                null,
                //sortOrder,
                null
        )) {
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);

            while (cursor.moveToNext()) {

                String name = cursor.getString(nameColumn);
                long id = cursor.getLong(idColumn);

                //Uri songCover = Uri.parse("content://media/external/audio/albumart");
                //Uri uriSongCover = ContentUris.withAppendedId(songCover, id);
                System.out.println("name: " + name);
                System.out.println("id:" + id);
                //System.out.println("numofSong: " + cursor.getCount());
                int numberOfSong = getPlaylistNumOfSong(context, id);
                //System.out.println("display name: " + displayName);
                playList.add(new Playlist(name, id, numberOfSong, null));
            }
        }

        return playList;
    }
    private static int getPlaylistNumOfSong(Context context, long playlistId) {

        Uri collection;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            } else {
            //collection = MediaStore.Audio.Playlists.Members.EXTERNAL_CONTENT_URI;
        }*/
        collection = MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, playlistId);

        //select column
        String[] projection = new String[] {
                MediaStore.Audio.Playlists._ID,
        };

        String selection = MediaStore.Audio.Playlists._ID +
                       " = ?";

        String[] selectionArgs = new String[] {
                String.valueOf(playlistId)};

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                new String[]{BaseColumns._ID},
                //selection,
                null,
                null,
                null
                //sortOrder,
        )) {
            // Cache column indices.
           // int numberOfSongColumn = cursor.getColumnIndexOrThrow((MediaStore.Audio.Playlists.Members._COUNT));
            while (cursor.moveToNext()) {
                // Get values of columns for a given song.
                //int numberOfSong = cursor.getInt(numberOfSongColumn);

                System.out.println("number of song: " + cursor.getCount());

                return cursor.getCount();
            }
        }
        return 0;
    }
}
