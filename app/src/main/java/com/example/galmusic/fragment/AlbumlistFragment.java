package com.example.galmusic.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.galmusic.R;
import com.example.galmusic.adapter.AlbumlistAdapter;
import com.example.galmusic.adapter.SonglistAdapter;
import com.example.galmusic.loader.AlbumListLoader;
import com.example.galmusic.loader.SongListLoader;
import com.example.galmusic.loader.SongLoader;
import com.example.galmusic.model.Song;

public class AlbumlistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AlbumlistAdapter mAdapter;
    private MediaControllerCompat mController;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_songlist, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //add splitline
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mAdapter = new AlbumlistAdapter(AlbumListLoader.getAlbumList(getActivity()), getActivity());
        mRecyclerView.setAdapter(mAdapter);


        mController = MediaControllerCompat.getMediaController(getActivity());

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}