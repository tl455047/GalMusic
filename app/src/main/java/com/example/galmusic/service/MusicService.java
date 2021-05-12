package com.example.galmusic.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.galmusic.MainActivity;
import com.example.galmusic.loader.AlbumDetailLoader;
import com.example.galmusic.loader.AlbumListLoader;
import com.example.galmusic.loader.PlaylistDetailLoader;
import com.example.galmusic.loader.SongListLoader;
import com.example.galmusic.loader.SongLoader;
import com.example.galmusic.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.galmusic.R;

public class MusicService extends MediaBrowserServiceCompat {

    int startMode;
    boolean allowRebind;

    private MediaSessionCompat mMediaSession;
    private MediaPlayer mMediaPlayer;

    public static final String CHANNEL_ID = "galmusic_channel_01";

    private NotificationCompat.Builder mNotificationBuilder;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
    //private MediaMetadataCompat.Builder mMediaMetadataBuilder;

    private List<MediaMetadataCompat> mMediAMetadataList;
    private int NowPlayingQueueItemIndex;

    private int mShuffleMode, mRepeatMode;
    private Random rand;
    @Override
    public void onCreate() {

        super.onCreate();

        mMediaSession = new MediaSessionCompat(this,"MusicService");

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setCallback(SessionCallback);
        mMediaSession.setActive(true);

        setSessionToken(mMediaSession.getSessionToken());
        //init mediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(PreparedListener);
        mMediaPlayer.setOnCompletionListener(CompletionListener);

        //init playState
        mPlaybackStateBuilder = buildPlaybackState();
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        mShuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE;
        mMediaSession.setShuffleMode(mShuffleMode);
        mRepeatMode = PlaybackStateCompat.REPEAT_MODE_ALL;
        mMediaSession.setRepeatMode(mRepeatMode);
        //init state
        mMediAMetadataList = SongListLoader.getSongList(MusicService.this);
        //build metadata
        NowPlayingQueueItemIndex = 2;
        mMediaSession.setMetadata(buildMetadata(mMediAMetadataList.get(NowPlayingQueueItemIndex), NowPlayingQueueItemIndex).build());
        //build notfication
        mNotificationBuilder = buildNotification();
        //build queue
        mMediaSession.setQueue(buildMediaQueue(mMediAMetadataList));
        mMediaSession.setQueueTitle("songList");

        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mMediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("media_root_id", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        result.detach();

        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        /*for(int i = 0; i < songList.size(); i++) {
            Bundle extra = new Bundle();

            extra.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songList.get(i).getAlbum());
            extra.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, songList.get(i).getDuration());

            MediaDescriptionCompat desc = new MediaDescriptionCompat.Builder()
                    .setMediaId(Long.toString(songList.get(i).getId()))
                    .setTitle(songList.get(i).getName())
                    .setSubtitle(songList.get(i).getArtist())
                    .setMediaUri(songList.get(i).getUri())
                    .setExtras(extra)
                    .build();

            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

            mediaItems.add(mediaItem);

        }
        */
        //System.out.println("music service song list size: " + songList.size());

        result.sendResult(mediaItems);
    }

    private PlaybackStateCompat.Builder buildPlaybackState() {
        PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0,1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_SEEK_TO);
        return playbackStateBuilder;
    }


    private List<MediaSessionCompat.QueueItem> buildMediaQueue(List<MediaMetadataCompat> mediAMetadataList) {
        List<MediaSessionCompat.QueueItem> queueItemList = new ArrayList<MediaSessionCompat.QueueItem>();

        for(int i = 0; i < mediAMetadataList.size(); i++) {
            MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(mediAMetadataList.get(i).getDescription(), i);
            queueItemList.add(queueItem);
        }
        return queueItemList;
    }

    private MediaMetadataCompat.Builder buildMetadata(MediaMetadataCompat metadata, int queueId) {
        MediaMetadataCompat.Builder mediaMetadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,  metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,  metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,  metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI,  metadata.getString(MediaMetadataCompat.METADATA_KEY_ART_URI))
                .putLong("albumId", metadata.getLong("albumId"))
                .putLong("queueId", queueId);
        return mediaMetadataBuilder;
    }
    private NotificationCompat.Builder buildNotification() {

        CharSequence name = "Galmusic";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(mChannel);

        int playButtonResId = isPlaying()
                ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_white_36dp;

        Intent nowPlayingIntent = new Intent(this, MainActivity.class);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        androidx.media.app.NotificationCompat.MediaStyle style = new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mMediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2, 3);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MusicService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setTicker("Galmusic")
                .setContentIntent(clickIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(style)
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_white_36dp, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                )
                .addAction(new NotificationCompat.Action(playButtonResId, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE))
                )
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_white_36dp, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                );
        return notificationBuilder;
    }

    @SuppressLint("RestrictedApi")
    private void updateNotification() {

        int playButtonResId = isPlaying()
                ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_white_36dp;
        long action = isPlaying()
                ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;

        mNotificationBuilder.mActions.clear();
        mNotificationBuilder
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_white_36dp, "",
                MediaButtonReceiver.buildMediaButtonPendingIntent(MusicService.this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
        )
                .addAction(new NotificationCompat.Action(playButtonResId, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(MusicService.this, action))
                )
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_white_36dp, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(MusicService.this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                );

        NotificationManagerCompat.from(this).notify(0, mNotificationBuilder.build());
    }

    @SuppressLint("RestrictedApi")
    private void updateNotificationContent(String title, String artist) {

        int playButtonResId = isPlaying()
                ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_white_36dp;
        long action = isPlaying()
                ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;

        mNotificationBuilder.mActions.clear();
        mNotificationBuilder
                .setContentTitle(title)
                .setContentText(artist)
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_white_36dp, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(MusicService.this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                )
                .addAction(new NotificationCompat.Action(playButtonResId, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(MusicService.this, action))
                )
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_white_36dp, "",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(MusicService.this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                );

        NotificationManagerCompat.from(this).notify(0, mNotificationBuilder.build());
    }

    private MediaSessionCompat.Callback SessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {

            KeyEvent keyEvent = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            System.out.println(keyEvent);
           // if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                long keyCode = keyEvent.getKeyCode();
                if(keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                    onPause();
                }
                else if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    onPlay();
                }
                else if(keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                    onSkipToPrevious();
                }
                else if(keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                    onSkipToNext();
                }
            //}
            return super.onMediaButtonEvent(mediaButtonIntent);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            mMediaPlayer.start();

            mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(),1.0f);
            mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());

            updateNotification();
        }


        @Override
        public void onPause() {
            super.onPause();
            mMediaPlayer.pause();

            mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(),1.0f);
            mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());

            updateNotification();
        }


        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras){
            super.onPlayFromMediaId(mediaId, extras);

            //NowPlayingQueueItemIndex = getQueueItemIndexFromMediaId();
            NowPlayingQueueItemIndex = getQueueIdFromMediaId(mediaId);
            playSongInQueue();
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
           if("setPlayingQueue".equals(command)) {
               System.out.println("setPlayingQueue");
               System.out.println("queueTitle: " + extras.getString("queueTitle"));
               //load new metadataList
               if(extras.getString("queueTitle").contains("album")) {
                   System.out.println("album");
                   System.out.println("albumId: " + extras.getLong("albumId"));
                   mMediAMetadataList = AlbumDetailLoader.getSongList(MusicService.this, Long.toString(extras.getLong("albumId")));
               }
               else if(extras.getString("queueTitle").contains("songList")) {
                   System.out.println("songList");
                   mMediAMetadataList = SongListLoader.getSongList(MusicService.this);
               }
               else if(extras.getString("queueTitle").contains("playList")) {
                   System.out.println("playlist");
                   mMediAMetadataList = PlaylistDetailLoader.getPlayList(MusicService.this, extras.getLong("playlistId"));
               }

               //set new queue
               mMediaSession.setQueue(buildMediaQueue(mMediAMetadataList));
               mMediaSession.setQueueTitle(extras.getString("queueTitle"));
               System.out.println("size: " + mMediAMetadataList.size());
               //play song
               onPlayFromMediaId(extras.getString("mediaId"), null);
           }
           else if("getCurrentPosition".equals(command)) {

               if(isPlaying())
                    mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(),1.0f);
               else
                   mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(),1.0f);

               mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());

           }
        }
        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            super.onPlayFromSearch(query, extras);

        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);

        }
        @Override
        public void onSeekTo(long pos) {
            mMediaPlayer.seekTo(pos, MediaPlayer.SEEK_PREVIOUS_SYNC);
            if(isPlaying())
                mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), 1.0f);
            else
                mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 1.0f);
            mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            if(repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                mRepeatMode = PlaybackStateCompat.REPEAT_MODE_ALL;
                mMediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
            }
            else if(repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                mRepeatMode = PlaybackStateCompat.REPEAT_MODE_ONE;
                mMediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
            }
            else {
                mRepeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
                mMediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
            }
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                mShuffleMode = PlaybackStateCompat.SHUFFLE_MODE_ALL;
                mMediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
            }
            else {
                mShuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE;
                mMediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            //out of bound
            if(mShuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                NowPlayingQueueItemIndex++;
                if(NowPlayingQueueItemIndex >= mMediAMetadataList.size())
                    NowPlayingQueueItemIndex = 0;
            }
            else {
                NowPlayingQueueItemIndex = rand.nextInt(mMediAMetadataList.size());
            }
            playSongInQueue();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            //out of bound
            if(mShuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                NowPlayingQueueItemIndex--;
                if (NowPlayingQueueItemIndex < 0)
                    NowPlayingQueueItemIndex = mMediAMetadataList.size() - 1;
            }
            else {
                NowPlayingQueueItemIndex = rand.nextInt(mMediAMetadataList.size());
            }
            playSongInQueue();
        }

        @Override
        public void onStop() {

        }

    };

    private MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            mMediaPlayer.start();
            mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(),1.0f);
            mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());

            updateNotification();
        }
    };

    private MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //out of bound
            if(mRepeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                NowPlayingQueueItemIndex++;
                if (NowPlayingQueueItemIndex >= mMediAMetadataList.size())
                    NowPlayingQueueItemIndex = 0;
            }
            else if(mRepeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) {
                NowPlayingQueueItemIndex++;
                //if (NowPlayingQueueItemIndex >= mMediAMetadataList.size())
            }
            System.out.println("repeatMode: " + mRepeatMode);
            playSongInQueue();
        }
    };

    private boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    private void playSongInQueue() {

        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(MusicService.this, mMediAMetadataList.get(NowPlayingQueueItemIndex).getDescription().getMediaUri());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaSession.setMetadata(buildMetadata(mMediAMetadataList.get(NowPlayingQueueItemIndex), NowPlayingQueueItemIndex).build());

       /* mPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_CONNECTING, mMediaPlayer.getCurrentPosition(),1.0f);
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());*/

        updateNotificationContent(mMediAMetadataList.get(NowPlayingQueueItemIndex).getString(MediaMetadataCompat.METADATA_KEY_TITLE), mMediAMetadataList.get(NowPlayingQueueItemIndex).getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        NotificationManagerCompat.from(MusicService.this).notify(0, mNotificationBuilder.build());

    }

    private int getQueueIdFromMediaId(String mediaId) {
        for(int i = 0; i < mMediAMetadataList.size(); i++) {
            if(mMediAMetadataList.get(i).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).equals(mediaId))
                return i;
        }
        return 0;
    }

}