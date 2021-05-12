package com.example.galmusic.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import com.example.galmusic.R;
import com.example.galmusic.loader.SongListLoader;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private SonglistFragment songlistFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        SongFragmentAdapter adapter = new SongFragmentAdapter(getChildFragmentManager());

        songlistFragment = new SonglistFragment();

        adapter.addFragment(songlistFragment, "songs");
        adapter.addFragment(new AlbumlistFragment(), "albums");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);


        return rootView;
    }

    public void updateRecycleView() {
        /*MainFragment fragment = (MainFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.content_fragment);
        fragment.updateRecycleView();*/
        songlistFragment.updateRecycleView();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    static class SongFragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public SongFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                break;
            case R.id.action_settings:

                break;
            case R.id.action_search:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                SearchFragment fragment = new SearchFragment();

                transaction.replace(R.id.content_fragment, fragment)
                        .addToBackStack("fragment_search")
                        .setReorderingAllowed(true)
                        .commit();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }*/
}