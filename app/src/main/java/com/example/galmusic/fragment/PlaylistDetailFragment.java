package com.example.galmusic.fragment;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galmusic.R;
import com.example.galmusic.adapter.AlbumDetailAdapter;
import com.example.galmusic.adapter.PlaylistDetailAdapter;
import com.example.galmusic.loader.AlbumDetailLoader;
import com.example.galmusic.loader.PlayListLoader;
import com.example.galmusic.loader.PlaylistDetailLoader;
import com.google.android.material.tabs.TabLayout;

public class PlaylistDetailFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private PlaylistDetailAdapter mAdapter;
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
        View rootView = inflater.inflate(R.layout.fragment_albumdetail, container, false);

        setHasOptionsMenu(true);
/*
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(0);
        ab.setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //add splitline
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        Bundle bundle = this.getArguments();
        mAdapter = new PlaylistDetailAdapter(PlaylistDetailLoader.getPlayList(getActivity(), bundle.getLong("playlistId")), getActivity(), bundle.getLong("playlistId"));
        mRecyclerView.setAdapter(mAdapter);

        mController = MediaControllerCompat.getMediaController(getActivity());

        mController.registerCallback(mMediaControllerCallback);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mController != null)
            mController.unregisterCallback(mMediaControllerCallback);
        //   System.out.println("onDestroy");
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_settings:

                return true;
            case R.id.action_search:

                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}