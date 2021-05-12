package com.example.galmusic.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.example.galmusic.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListLoader {
    public static final List<Album> getAlbumList(Context context) {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.

        List<Album> albumList = new ArrayList<Album>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        }
        //select column
        String[] projection = new String[] {
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums._ID,
        };
        //select condition
        //String selection = MediaStore.Audio.Media.DURATION +
        //       " >= ?";
        //String[] selectionArgs = new String[] {
        //        String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))};
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                //selection,
                null,
                //selectionArgs,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            int numberOfSongColumn = cursor.getColumnIndexOrThrow((MediaStore.Audio.Albums.NUMBER_OF_SONGS));
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
            while (cursor.moveToNext()) {
                // Get values of columns for a given song.
                String album = cursor.getString(albumColumn);
                int numberOfSong = cursor.getInt(numberOfSongColumn);
                long id = cursor.getLong(idColumn);

                Uri songCover = Uri.parse("content://media/external/audio/albumart");
                Uri uriSongCover = ContentUris.withAppendedId(songCover, id);
                //System.out.println(album);
                //System.out.println("album id:" + id);
                //System.out.println("number of song: " + numberOfSong);
                albumList.add(new Album(id, album, numberOfSong, uriSongCover));
            }
        }

        return albumList;
    }
}
