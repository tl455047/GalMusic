package com.example.galmusic.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import com.example.galmusic.model.Song;


public class SongLoader {

    public static Song getSong(Context context, String id_) {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        //select column
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
        };
        //select condition
        String selection = MediaStore.Audio.Media._ID +
                " = ?";
        String[] selectionArgs = new String[]{ id_ };
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        Song song = new Song();

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            while (cursor.moveToNext()) {
                // Get values of columns for a given song.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                String album = cursor.getString(albumColumn);
                String artist = cursor.getString(artistColumn);
                long albumId = cursor.getLong(albumIdColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                Uri songCover = Uri.parse("content://media/external/audio/albumart");
                Uri uriSongCover = ContentUris.withAppendedId(songCover, albumId);

                song = new Song(id, contentUri, name, duration, album, artist, uriSongCover);
                //System.out.println("song: " + song.getName());
            }
        }

        return song;
    }
}
