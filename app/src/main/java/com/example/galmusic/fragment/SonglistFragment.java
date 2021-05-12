package com.example.galmusic.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galmusic.MainActivity;
import com.example.galmusic.NowPlayingActivity;
import com.example.galmusic.R;
import com.example.galmusic.adapter.SonglistAdapter;
import com.example.galmusic.loader.SongListLoader;
import com.example.galmusic.loader.SongLoader;
import com.example.galmusic.model.Song;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Songlist fragment.
 * Showing song list by recyclerview.
 */
public class SonglistFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private SonglistAdapter mAdapter;
    private MediaControllerCompat mController;
    private View rootView;

    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            //System.out.println("onPlaybackStateChanged");
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //System.out.println("onMetadataChanged");
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_songlist, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //add splitline
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        System.out.println("onCreateView");
        return rootView;
    }
    public void updateRecycleView() {

        mAdapter = new SonglistAdapter(SongListLoader.getSongList(getActivity()), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mController = MediaControllerCompat.getMediaController(getActivity());

        mController.registerCallback(mMediaControllerCallback);

        System.out.println("updateRecycleView");
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");
        if(MediaControllerCompat.getMediaController(getActivity()) != null) {
            updateRecycleView();
        }
        //mController = MediaControllerCompat.getMediaController(getActivity());
          //mController.registerCallback(mMediaControllerCallback);

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
       // mController.unregisterCallback(mMediaControllerCallback);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        if(mController != null)
            mController.unregisterCallback(mMediaControllerCallback);
     //   System.out.println("onDestroy");
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
