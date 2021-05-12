package com.example.galmusic.fragment;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.galmusic.R;
import com.example.galmusic.activity.SearchActivity;
import com.example.galmusic.adapter.SearchAdapter;
import com.example.galmusic.adapter.SonglistAdapter;
import com.example.galmusic.loader.SongListLoader;
import com.example.galmusic.loader.SongLoader;
import com.example.galmusic.model.Song;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private MediaControllerCompat mController;
    private SearchView mSearchView;
    private String queryString;

    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            //updateControllerPlayback();
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
            //updateControllerMetaData();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_songlist, container, false);

        setHasOptionsMenu(true);
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      /*  ActionBar actionBar = (ActionBar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
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

        updateRecycleView();
        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);

        mSearchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());

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

                mAdapter.setSonglist(SongListLoader.querySongList(getActivity(), queryString));
                mAdapter.notifyDataSetChanged();

                return true;
            }
        });

        return;
    }

    public void updateRecycleView() {

        mAdapter = new SearchAdapter(SongListLoader.getSongList(getActivity()), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mController = MediaControllerCompat.getMediaController(getActivity());

        mController.registerCallback(mMediaControllerCallback);

        System.out.println("updateRecycleView");
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");
        //mController = MediaControllerCompat.getMediaController(getActivity());
        //mController.registerCallback(mMediaControllerCallback);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mController != null)
            mController.unregisterCallback(mMediaControllerCallback);
        //   System.out.println("onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
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

}
