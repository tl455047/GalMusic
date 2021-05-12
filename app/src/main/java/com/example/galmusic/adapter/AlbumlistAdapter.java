package com.example.galmusic.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
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
import com.example.galmusic.model.Album;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AlbumlistAdapter extends RecyclerView.Adapter<AlbumlistAdapter.ViewHolder> {
    protected List<Album> albumlist;
    protected MediaControllerCompat mController;
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
    public AlbumlistAdapter(List<Album> albumlist, FragmentActivity activity) {
        this.albumlist = albumlist;
        mController = MediaControllerCompat.getMediaController(activity);
        //setHasStableIds(true);
        this.activity = activity;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public AlbumlistAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_song, viewGroup, false);

        return new AlbumlistAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AlbumlistAdapter.ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitleView().setText(albumlist.get(position).getName());
        viewHolder.getNumOfSongView().setText(Integer.toString(albumlist.get(position).getNumOfSong()) + "");

        ImageLoader.getInstance().displayImage(albumlist.get(position).getArtUri().toString(),
                viewHolder.getAlbumArtView(), new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());


        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*Bundle bundle = new Bundle();
                bundle.putInt("queueItemId", viewHolder.getAdapterPosition());
                selected_position = viewHolder.getAdapterPosition();
                mController.getTransportControls().playFromMediaId(songlist.get(viewHolder.getAdapterPosition()).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), bundle);*/
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                AlbumDetailFragment fragment = new AlbumDetailFragment();

                Bundle bundle = new Bundle();
                bundle.putLong("albumId", albumlist.get(viewHolder.getAdapterPosition()).getId());
                fragment.setArguments(bundle);

                transaction.replace(R.id.content_fragment, fragment)
                        .addToBackStack("fragment_albumlistetail")
                        .setReorderingAllowed(true)
                        .commit();
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return albumlist.size();
    }
}
