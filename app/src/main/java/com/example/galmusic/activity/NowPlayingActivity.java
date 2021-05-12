package com.example.galmusic;

import android.content.ComponentName;
import android.os.Bundle;

import com.example.galmusic.fragment.MainFragment;
import com.example.galmusic.fragment.SearchFragment;
import com.example.galmusic.service.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import com.example.galmusic.R;

import com.example.galmusic.fragment.NowPlayingFragment;

import java.util.List;

public class NowPlayingActivity extends AppCompatActivity {

    private MediaBrowserCompat mBrowser;

    private MediaControllerCompat mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        NowPlayingFragment fragment = new NowPlayingFragment();

        transaction.replace(R.id.content_fragment, fragment)
                .addToBackStack("fragment_nowPlaying")
                .setReorderingAllowed(true)
                .commit();

        connectToService();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrowser.disconnect();
    }

    private void connectToService() {

        mBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                BrowserConnectionCallback,
                null
        );

        mBrowser.connect();
    }

    private final MediaBrowserCompat.ConnectionCallback BrowserConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback(){
                @Override
                public void onConnected() {

                    if (mBrowser.isConnected()) {

                        String mediaId = mBrowser.getRoot();

                        mBrowser.unsubscribe(mediaId);

                        mBrowser.subscribe(mediaId, mSubscriptionCallback);

                        try {

                            mController = new MediaControllerCompat(NowPlayingActivity.this, mBrowser.getSessionToken());

                            //mController.registerCallback(mControllerCallback);

                            MediaControllerCompat.setMediaController(NowPlayingActivity.this, mController);


                            //setController();

                           /* mController.registerCallback(new MediaControllerCompat.Callback() {
                                @Override
                                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                                    System.out.println("onPlaybackStateChanged");
                                    updateControllerPlayback();
                                }

                                @Override
                                public void onMetadataChanged(MediaMetadataCompat metadata) {
                                    System.out.println("onMetadataChanged");
                                    updateControllerMetaData();
                                }
                            });*/

                            NowPlayingFragment fragment = (NowPlayingFragment) getSupportFragmentManager().findFragmentById(R.id.content_fragment);
                            fragment.setMediaController();

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }


                    }
                }

            };
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback(){
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    //  songList = new ArrayList<>();
                  /*  for (MediaBrowserCompat.MediaItem item:children){


                        songList.add(new Song(Long.parseLong(item.getMediaId()),
                                        item.getDescription().getMediaUri(),
                                        item.getDescription().getTitle().toString(),
                                        item.getDescription().getExtras().getInt(MediaMetadataCompat.METADATA_KEY_DURATION),
                                        item.getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_ALBUM),
                                        item.getDescription().getSubtitle().toString()
                                )
                        );


                    }

                    System.out.println("media controller size: " + songList.size());


                    */
                   /* FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    SonglistFragment fragment = new SonglistFragment();

                    transaction.replace(R.id.songlist_fragment, fragment);
                    transaction.commit();
*/

                }
            };
}