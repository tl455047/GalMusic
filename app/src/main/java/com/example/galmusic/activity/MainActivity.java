package com.example.galmusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.galmusic.activity.SearchActivity;
import com.example.galmusic.fragment.MainFragment;
import com.example.galmusic.fragment.NowPlayingFragment;
import com.example.galmusic.fragment.PlaylistFragment;
import com.example.galmusic.fragment.SearchFragment;
import com.example.galmusic.loader.SongLoader;
import com.example.galmusic.model.Song;
import com.example.galmusic.service.MusicService;
import com.google.android.material.navigation.NavigationView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;

import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 0;

    private MediaBrowserCompat mBrowser;

    private MediaControllerCompat mController;

    private ImageView playPauseView, nextView, previousView;

    private TextView titleView, artistView;

    private ImageView albumArtView;

    private ProgressBar mProgressBar;

    private Handler mHandler;

    private NavigationView navigationView;

    private DrawerLayout mDrawerLayout;

    private ImageView navAlbumArtView;
    private TextView navTitleView, navArtistView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            checkPermission();
        }


        connectToService();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MainFragment fragment = new MainFragment();

        transaction.replace(R.id.content_fragment, fragment, "mainFragment");
        transaction.commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //connectToService();
      /*  if(mBrowser != null && mBrowser.isConnected()) {
            MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_fragment);
            fragment.updateRecycleView();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mController != null)
            mController.unregisterCallback(mMediaControllerCallback);
        mBrowser.disconnect();
    }

    private void setNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        setNavigationHeader();

        setNavigationIcons();

        setDrawerContent();

        navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
    }

    private void setNavigationHeader() {
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        navAlbumArtView = (ImageView) header.findViewById(R.id.album_art);
        navTitleView = (TextView) header.findViewById(R.id.song_title);
        navArtistView = (TextView) header.findViewById(R.id.song_artist);

        navTitleView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        navArtistView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        ImageLoader.getInstance().displayImage(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                navAlbumArtView, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());


    }
    private void setNavigationIcons() {
        navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
        navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
        navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
        navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);
        navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
        navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);

    }

    private void setDrawerContent() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        navigatePosition(menuItem);
                        return true;

                    }
                });
    }
    private void navigatePosition(final MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        Fragment currentFragment;
        FragmentTransaction transaction;
        switch (menuItem.getItemId()) {

            case R.id.nav_library:
                navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_fragment);
                if(currentFragment instanceof MainFragment)
                    break;
                MainFragment mainFragment = new MainFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_fragment, mainFragment).commit();
                break;
            case R.id.nav_playlists:
                navigationView.getMenu().findItem(R.id.nav_playlists).setChecked(true);
                transaction = getSupportFragmentManager().beginTransaction();
                PlaylistFragment playlistFragment = new PlaylistFragment();
                transaction.replace(R.id.content_fragment, playlistFragment).commit();
                break;
            case R.id.nav_nowplaying:
                if(mBrowser != null && mBrowser.isConnected()) {
                    startActivity(new Intent(MainActivity.this, com.example.galmusic.NowPlayingActivity.class));
                }
                break;
            case R.id.nav_queue:
                navigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
                break;
            case R.id.nav_settings:
                navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                break;
            case R.id.nav_about:
                navigationView.getMenu().findItem(R.id.nav_about).setChecked(true);
                break;
        }

    }

    private void updateNavigationViewHeader() {

        navTitleView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        navArtistView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        ImageLoader.getInstance().displayImage(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                navAlbumArtView, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

    }

   /* @Override
    protected void onPause() {
        super.onPause();
        mController.unregisterCallback(mMediaControllerCallback);
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
    private Boolean isNavigationViewOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you spesetHasOptionsMenu(true)cify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_fragment);
                if(currentFragment instanceof MainFragment || currentFragment instanceof PlaylistFragment)
                    mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:

                break;
            case R.id.action_search:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                SearchFragment fragment = new SearchFragment();
                transaction.replace(R.id.content_fragment, fragment)
                        .addToBackStack("fragment_search")
                        .setReorderingAllowed(true)
                        .commit();
             /*   Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);*/
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
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

                            mController = new MediaControllerCompat(MainActivity.this, mBrowser.getSessionToken());

                            //mController.registerCallback(mControllerCallback);

                            MediaControllerCompat.setMediaController(MainActivity.this, mController);

                            setController();

                            setNavigationView();

                            mController.registerCallback(mMediaControllerCallback);

                            MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_fragment);
                            fragment.updateRecycleView();

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }


                    }
                }

            };

    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {

            //System.out.println("onPlaybackStateChanged");
            updateControllerPlayback();
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //System.out.println("onMetadataChanged");
            // mRecyclerView.getAdapter().notifyDataSetChanged();
            updateControllerMetaData();
            updateNavigationViewHeader();
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




    /**
     * Ask for READ_EXTERNAL_STORAGE permission
     */
    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                   // connectToService();
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.

            }
            //ask for permission
            else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            checkPermission();
        }
    }

    private void setController() {


        setControllerButton();

        setSongMetadata();

        setSeekBar();

        View view = (View)findViewById(R.id.controller_bar);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, com.example.galmusic.NowPlayingActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setControllerButton() {

        playPauseView = (ImageView)findViewById(R.id.image_playpause);
        playPauseView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
                    mController.getTransportControls().pause();
                else if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
                    mController.getTransportControls().play();
                else if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE)
                    mController.getTransportControls().playFromMediaId(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), null);

            }
        });

        nextView = (ImageView)findViewById(R.id.image_next);
        nextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.getTransportControls().skipToNext();
            }
        });

        previousView = (ImageView)findViewById(R.id.image_prev);
        previousView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.getTransportControls().skipToPrevious();
            }
        });
    }

    private void setSongMetadata() {
        titleView = (TextView)findViewById(R.id.title);
        titleView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        artistView = (TextView)findViewById(R.id.artist);
        artistView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        albumArtView = (ImageView)this.findViewById(R.id.album_art);
        ImageLoader.getInstance().displayImage(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                albumArtView, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

    }

    private void setSeekBar() {
        mProgressBar = (ProgressBar) findViewById(R.id.song_progress_normal);

        mProgressBar.setMax((int)mController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

        mProgressBar.setProgress((int) mController.getPlaybackState().getPosition());

        mHandler = new Handler(Looper.getMainLooper());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    mController.sendCommand("getCurrentPosition", null, null);
                }
                mHandler.postDelayed(this, 100);
            }
        });
    }
    private void updateControllerPlayback() {
        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
            playPauseView.setImageResource(R.drawable.ic_pause_white_36dp);

        else if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
            playPauseView.setImageResource(R.drawable.ic_play_white_36dp);

        mProgressBar.setProgress((int) mController.getPlaybackState().getPosition());

    }

    private void updateControllerMetaData() {

        titleView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        artistView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        ImageLoader.getInstance().displayImage(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                albumArtView, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

    }
}