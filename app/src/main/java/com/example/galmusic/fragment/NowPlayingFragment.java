package com.example.galmusic.fragment;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.example.galmusic.NowPlayingActivity;
import com.example.galmusic.R;
import com.example.galmusic.adapter.AlbumDetailAdapter;
import com.example.galmusic.loader.AlbumDetailLoader;
import com.example.galmusic.loader.SongLoader;
import com.example.galmusic.model.Song;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class NowPlayingFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AlbumDetailAdapter mAdapter;
    private MediaControllerCompat mController;

    private Handler mHandler;

    private TextView titleView, artistView;

    private TextView durationView, elapsedTimeView;

    private ImageView albumArtView;
    private ImageView shuffleView, repeatView;
    private SeekBar mSeekBar;

    private ImageView playPauseView, nextView, previousView;

    private View rootView;

    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
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
        @Override
        public void onShuffleModeChanged(int shuffleMode) {
            updateShuffleState();
        }
        @Override
        public void onRepeatModeChanged(int repeatMode) {
            updateRepeatState();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        setHasOptionsMenu(true);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        //mController.registerCallback(mMediaControllerCallback);


        return rootView;
    }

    public void setMediaController() {

        mController = MediaControllerCompat.getMediaController(getActivity());

        mController.registerCallback(mMediaControllerCallback);

        setController(rootView);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");

        //mController = MediaControllerCompat.getMediaController(getActivity());
      /*  if(mController != null)
            mController.registerCallback(mMediaControllerCallback);
*/
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");

    }
    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop");


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        if(mController != null)
            mController.unregisterCallback(mMediaControllerCallback);
    }

    private String getTimeFromDuration(long duration) {
        long secs = duration / 1000;
        long hours = secs / 3600;
        secs = secs % 3600;
        long mins = secs / 60;
        secs = secs % 60;

        if(hours == 0) {
            return String.format("%02d:%02d", mins, secs);
        }
        else {
            return String.format("%02d:%02d:%02d", hours, mins, secs);
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_nowplaying, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
                return true;
            case R.id.action_settings:

                return true;
          /*  case R.id.action_search:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                SearchFragment fragment = new SearchFragment();

                transaction.replace(R.id.content_fragment, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("fragment_search")
                        .commit();
                return true;*/
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void setController(View rootView) {

      //  System.out.println("setController");

        setControllerButton(rootView);

        setSongMetadata(rootView);

        setSeekBar(rootView);

        setShuffleState(rootView);

        setRepeatState(rootView);
    }
    private void setControllerButton(View rootView) {
        //set play pause button
        playPauseView = (ImageView)rootView.findViewById(R.id.image_playpause);
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
        //set next button
        nextView = (ImageView)rootView.findViewById(R.id.next);
        nextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.getTransportControls().skipToNext();
            }
        });
        //set previous button
        previousView = (ImageView)rootView.findViewById(R.id.previous);
        previousView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.getTransportControls().skipToPrevious();
            }
        });
    }
    private void setSongMetadata(View rootView) {
        //set duration
        durationView = (TextView)rootView.findViewById(R.id.song_duration);
        durationView.setText(getTimeFromDuration(mController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));

        elapsedTimeView = (TextView)rootView.findViewById(R.id.song_elapsed_time);
        elapsedTimeView.setText(getTimeFromDuration(mController.getPlaybackState().getPosition()));
        //set title
        titleView = (TextView)rootView.findViewById(R.id.song_title);
        titleView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        //set artist
        artistView = (TextView)rootView.findViewById(R.id.song_artist);
        artistView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        //set album art
        albumArtView = (ImageView)rootView.findViewById(R.id.album_art);

        ImageLoader.getInstance().displayImage(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                albumArtView, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());
    }
    private void setSeekBar(View rootView) {
        //set seekBar max
        mSeekBar = rootView.findViewById(R.id.song_progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mController.getTransportControls().seekTo(seekBar.getProgress());
            }
        });
        mSeekBar.setMax((int)mController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

        mHandler = new Handler(Looper.getMainLooper());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    mController.sendCommand("getCurrentPosition", null, null);
                }
                mHandler.postDelayed(this, 100);
            }
        });
    }
    private void setShuffleState(View rootView) {
        shuffleView = (ImageView) rootView.findViewById(R.id.shuffle);
        shuffleView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mController.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    mController.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                else
                    mController.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
            }
        });
        updateShuffleState();
    }

    private void setRepeatState(View rootView) {
        repeatView = (ImageView) rootView.findViewById(R.id.repeat);
        repeatView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL)
                    mController.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                else if(mController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ONE)
                    mController.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                else
                    mController.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
            }
        });
        updateRepeatState();
    }
    private void updateControllerPlayback() {

        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
            playPauseView.setImageResource(R.drawable.ic_pause_white_36dp);
        else if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
            playPauseView.setImageResource(R.drawable.ic_play_white_36dp);
        //set elapsedTime
        elapsedTimeView.setText(getTimeFromDuration(mController.getPlaybackState().getPosition()));
        //set seekBar progress
        mSeekBar.setProgress((int) mController.getPlaybackState().getPosition());
    }

    private void updateControllerMetaData() {

        titleView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        artistView.setText(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        mSeekBar.setMax((int)mController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        
        durationView.setText(getTimeFromDuration(mController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));

        ImageLoader.getInstance().displayImage(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                albumArtView, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());
    }

    private void updateShuffleState() {

        MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getContext())
                .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                .setSizeDp(30);

        if (mController.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_ALL)
            builder.setColor(Color.BLUE);
        else
            builder.setColor(Color.WHITE);

        shuffleView.setImageDrawable(builder.build());

    }

    private void updateRepeatState() {

        MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                .setSizeDp(30);

        if(mController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL) {
            builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            builder.setColor(Color.BLUE);
        }
        else if(mController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ONE) {
            builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
            builder.setColor(Color.BLUE);
        }
        else {
            builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            builder.setColor(Color.WHITE);
        }

        repeatView.setImageDrawable(builder.build());

    }
}