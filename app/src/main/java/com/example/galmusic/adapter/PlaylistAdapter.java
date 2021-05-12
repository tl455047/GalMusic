package com.example.galmusic.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galmusic.R;
import com.example.galmusic.fragment.AlbumDetailFragment;
import com.example.galmusic.fragment.PlaylistDetailFragment;
import com.example.galmusic.model.Playlist;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    protected List<Playlist> playlist;
    protected FragmentActivity activity;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView numOfSongView;
        private final ImageView albumArtView;
        // private MusicVisualizer visualizer;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            titleView = (TextView) view.findViewById(R.id.song_title);
            numOfSongView = (TextView) view.findViewById(R.id.song_artist);
            albumArtView = (ImageView) view.findViewById(R.id.albumArt);
            //visualizer = (MusicVisualizer)view.findViewById(R.id.visualizer);
        }

        public TextView getTitleView() {
            return titleView;
        }

        public TextView getNumOfSongView() {
            return numOfSongView;
        }

        public ImageView getAlbumArtView() {  return albumArtView; }
    }

    /**
     * Initialize the songlist.
     *
     */
    public PlaylistAdapter(List<Playlist> playlist, FragmentActivity activity) {
        this.playlist = playlist;
        this.activity = activity;
        //setHasStableIds(true);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_song, viewGroup, false);

        return new PlaylistAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PlaylistAdapter.ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitleView().setText(playlist.get(position).getName());
        viewHolder.getNumOfSongView().setText(Integer.toString(playlist.get(position).getNumOfSong()));

        /*ImageLoader.getInstance().displayImage(playlist.get(position),
                viewHolder.getAlbumArtView(), new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());*/

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                PlaylistDetailFragment fragment = new PlaylistDetailFragment();

                Bundle bundle = new Bundle();
                bundle.putLong("playlistId", playlist.get(viewHolder.getAdapterPosition()).getId());
                fragment.setArguments(bundle);

                transaction.replace(R.id.content_fragment, fragment)
                        .addToBackStack("fragment_playlistdetail")
                        .setReorderingAllowed(true)
                        .commit();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return playlist.size();
    }
}
