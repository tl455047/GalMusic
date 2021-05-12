package com.example.galmusic.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galmusic.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

protected List<MediaMetadataCompat> songlist;
protected MediaControllerCompat mController;
protected String matchPattern;
/**
 * Provide a reference to the type of views that you are using
 * (custom ViewHolder).
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleView;
    private final TextView artistView;
    private final ImageView albumArtView;
    // private MusicVisualizer visualizer;

    public ViewHolder(View view) {
        super(view);
        // Define click listener for the ViewHolder's View
        titleView = (TextView) view.findViewById(R.id.song_title);
        artistView = (TextView) view.findViewById(R.id.song_artist);
        albumArtView = (ImageView) view.findViewById(R.id.albumArt);
        //visualizer = (MusicVisualizer)view.findViewById(R.id.visualizer);
    }

    public TextView getTitleView() {
        return titleView;
    }

    public TextView getArtistView() {
        return artistView;
    }

    public ImageView getAlbumArtView() {  return albumArtView; }
}

    /**
     * Initialize the songlist.
     *
     */
    public SearchAdapter(List<MediaMetadataCompat> songlist, FragmentActivity activity) {
        this.songlist = songlist;
        mController = MediaControllerCompat.getMediaController(activity);
        //setHasStableIds(true);
        matchPattern = "";
    }
    public void setSonglist(List<MediaMetadataCompat> songlist) {
        this.songlist = songlist;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_song, viewGroup, false);

        return new SearchAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder viewHolder, final int position) {
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
        else if(songlist.get(position).getString(MediaMetadataCompat.METADATA_KEY_TITLE).contains(matchPattern)) {
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
                }
                else {
                    //update queue, then play song
                    bundle.putString("queueTitle", queueTitle);
                    bundle.putString("mediaId", songlist.get(selected_position).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
                    mController.sendCommand("setPlayingQueue", bundle, null);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return songlist.size();
    }

}
