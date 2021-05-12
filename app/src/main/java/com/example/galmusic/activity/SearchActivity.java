package com.example.galmusic.activity;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galmusic.R;
import com.example.galmusic.adapter.SearchAdapter;
import com.example.galmusic.fragment.MainFragment;
import com.example.galmusic.fragment.NowPlayingFragment;
import com.example.galmusic.fragment.SearchFragment;
import com.example.galmusic.loader.SongListLoader;
import com.example.galmusic.model.Song;
import com.example.galmusic.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION = 0;

    private MediaBrowserCompat mBrowser;

    private MediaControllerCompat mController;

    private static List<Song> songList;

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private SearchView mSearchView;
    private String queryString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SearchFragment fragment = new SearchFragment();

        transaction.replace(R.id.content_fragment, fragment)
                .addToBackStack("fragment_search")
                .setReorderingAllowed(true)
                .commit();

        connectToService();
        queryString = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);


        mSearchView = new SearchView(getSupportActionBar().getThemedContext());

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setActionView(mSearchView);

        //mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if(mSearchView == null)
            System.out.println("null");
        //mSearchView.setOnQueryTextListener(getActivity());

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                onQueryTextChange(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if(newText.trim().equals(queryString)) {
                    return true;
                }

                queryString = newText.trim();

                System.out.println(queryString);

                mAdapter.setSonglist(SongListLoader.querySongList(SearchActivity.this, queryString));
                mAdapter.notifyDataSetChanged();

                return true;
            }
        });

        return true;
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

                            mController = new MediaControllerCompat(SearchActivity.this, mBrowser.getSessionToken());

                            //mController.registerCallback(mControllerCallback);

                            MediaControllerCompat.setMediaController(SearchActivity.this, mController);

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

                            SearchFragment fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.content_fragment);
                            fragment.updateRecycleView();

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
                    songList = new ArrayList<>();
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
