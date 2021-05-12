package com.example.galmusic.adapter;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galmusic.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.List;

/**
 * Song list adapter for recyclerView .
 *
 */
public class SonglistAdapter extends RecyclerView.Adapter<SonglistAdapter.ViewHolder> {

    protected List<MediaMetadataCompat> songlist;
    protected MediaControllerCompat mController;
    protected FragmentActivity activity;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView artistView;
        private final ImageView albumArtView;
        private final ImageView popUpMenuView;
       // private MusicVisualizer visualizer;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            titleView = (TextView) view.findViewById(R.id.song_title);
            artistView = (TextView) view.findViewById(R.id.song_artist);
            albumArtView = (ImageView) view.findViewById(R.id.albumArt);
            popUpMenuView = (ImageView) view.findViewById(R.id.popup_menu);
            //visualizer = (MusicVisualizer)view.findViewById(R.id.visualizer);
        }

        public TextView getTitleView() {
            return titleView;
        }

        public TextView getArtistView() {
            return artistView;
        }

        public ImageView getAlbumArtView() {  return albumArtView; }

        public ImageView getPopUpMenuView() { return popUpMenuView; }
    }

    /**
     * Initialize the songlist.
     *
     */
    public SonglistAdapter(List<MediaMetadataCompat> songlist, FragmentActivity activity) {
        this.songlist = songlist;
        mController = MediaControllerCompat.getMediaController(activity);
        this.activity = activity;
        //setHasStableIds(true);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public SonglistAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_song, viewGroup, false);



        return new SonglistAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitleView().setText(songlist.get(position).getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        viewHolder.getArtistView().setText(songlist.get(position).getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

        ImageLoader.getInstance().displayImage(songlist.get(position).getString(MediaMetadataCompat.METADATA_KEY_ART_URI),
                viewHolder.getAlbumArtView(), new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true).build());

        if(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).equals(songlist.get(position).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
            viewHolder.getTitleView().setTextColor(Color.BLUE);
        }
        else {
            viewHolder.getTitleView().setTextColor(Color.BLACK);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selected_position = viewHolder.getAdapterPosition();
                Bundle bundle = new Bundle();
                String queueTitle = "songList";
                if(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).equals(songlist.get(selected_position).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
                    //same song, change play or pause
                    if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
                        mController.getTransportControls().pause();
                    else if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
                        mController.getTransportControls().play();
                    else if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE)
                        mController.getTransportControls().playFromMediaId(mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), null);
                }
                else if(mController.getQueueTitle().equals(queueTitle)) {
                    //same queue, no need to update queue
                    mController.getTransportControls().playFromMediaId(songlist.get(selected_position).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), bundle);
                    //System.out.println("song id: " + songlist.get(selected_position).getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                }
                else {
                    //update queue, then play song
                    bundle.putString("queueTitle", queueTitle);
                    bundle.putString("mediaId", songlist.get(selected_position).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
                    mController.sendCommand("setPlayingQueue", bundle, null);
                }
            }
        });

        setOnPopupMenuListener(viewHolder, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return songlist.size();
    }

    private void setOnPopupMenuListener(ViewHolder viewHolder, final int position) {

        viewHolder.getPopUpMenuView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(activity, v);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_remove_playlist:

                                break;
                            case R.id.popup_song_play:

                                break;
                            case R.id.popup_song_play_next:

                                break;
                            case R.id.popup_song_goto_album:

                                break;
                            case R.id.popup_song_addto_queue:

                                break;
                            case R.id.popup_song_addto_playlist:

                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });
    }
}