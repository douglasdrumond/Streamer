package com.cafelinear.streamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafelinear.streamer.R;
import com.cafelinear.streamer.model.SpotifyTrack;

import java.util.ArrayList;

public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.ViewHolder> {

    private static final String LOG_TAG = TopTracksAdapter.class.getSimpleName();
    private ArrayList<SpotifyTrack> mTracks;
    private Context mContext;

    public TopTracksAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.top_tracks_item, parent, false);
        return new ViewHolder(view, new ViewHolder.ViewHolderClickListener() {

            @Override
            public void onTrackClick(int position) {
                // TODO: 21/06/15 start activity playing from this track
                Log.d(LOG_TAG, "onTrackCLick " + mTracks.get(position).getName());
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SpotifyTrack track = mTracks.get(position);
        holder.mTrackName.setText(track.getName());
        if (track.getAlbumArtUrl() != null) {
            Glide.with(mContext)
                    .load(track.getAlbumArtUrl())
                    .error(R.drawable.vinyl)
                    .placeholder(R.drawable.vinyl)
                    .into(holder.mAlbumArt);
        }
    }

    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }

    public void setTracks(ArrayList<SpotifyTrack> tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView mAlbumArt;
        protected TextView mTrackName;
        private ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolderClickListener listener) {
            super(itemView);
            mListener = listener;
            mAlbumArt = (ImageView) itemView.findViewById(R.id.album_art);
            mTrackName = (TextView) itemView.findViewById(R.id.track_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onTrackClick(getAdapterPosition());
        }

        public interface ViewHolderClickListener {

            void onTrackClick(int adapterPosition);
        }
    }
}
