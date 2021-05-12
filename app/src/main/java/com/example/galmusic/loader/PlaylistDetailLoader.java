package com.example.galmusic.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import com.example.galmusic.model.Playlist;
import com.example.galmusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailLoader {
    public static final List<MediaMetadataCompat> getPlayList(Context context, long playlistId) {

        List<MediaMetadataCompat> songlist = new ArrayList<MediaMetadataCompat>();

        Uri collection;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            } else {
            //collection = MediaStore.Audio.Playlists.Members.EXTERNAL_CONTENT_URI;
        }*/
        collection = MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, playlistId);

        //select column
        String[] projection = new String[]{
                MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.DISPLAY_NAME,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
        };

        String selection = MediaStore.Audio.Playlists._ID +
                " = ?";

        String[] selectionArgs = new String[]{
                String.valueOf(playlistId)};

        String sortOrder = MediaStore.Audio.Playlists.Members.PLAY_ORDER + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                //selection,
                null,
                null,
                sortOrder
                //sortOrder,
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID);

            while (cursor.moveToNext()) {
                // Get values of columns for a given song.
                //int numberOfSong = cursor.getInt(numberOfSongColumn);


                String name = cursor.getString(nameColumn);
                long id = cursor.getLong(idColumn);
                int duration = cursor.getInt(durationColumn);
                String album = cursor.getString(albumColumn);
                String artist = cursor.getString(artistColumn);
                long albumId = cursor.getLong(albumColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                Uri songCover = Uri.parse("content://media/external/audio/albumart");
                Uri uriSongCover = ContentUris.withAppendedId(songCover, albumId);
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                System.out.println(name);
                System.out.println(artist);
                System.out.println("id: " + id);
                MediaMetadataCompat.Builder mediaMetadataBuilder = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, Long.toString(id))
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, contentUri.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, uriSongCover.toString());
                //.putLong("albumId", Long.parseLong(albumId));
                songlist.add(getSongBuilder(context, name).build());

            }
        }
        return songlist;
    }

    public static final MediaMetadataCompat.Builder getSongBuilder(Context context, String displayName) {

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
        String selection = MediaStore.Audio.Media.DISPLAY_NAME +
                " = ?";
        String[] selectionArgs = new String[]{ displayName };
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";


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

                MediaMetadataCompat.Builder mediaMetadataBuilder = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, Long.toString(id))
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, contentUri.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, uriSongCover.toString());

               return mediaMetadataBuilder;
                //System.out.println("song: " + song.getName());
            }
        }
        return null;
    }
}
