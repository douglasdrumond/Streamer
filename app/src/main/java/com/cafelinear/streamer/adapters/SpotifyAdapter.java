package com.cafelinear.streamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafelinear.streamer.R;
import com.cafelinear.streamer.model.SpotifyArtist;

import java.util.ArrayList;

public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.ViewHolder> {

    private static final String LOG_TAG = SpotifyAdapter.class.getSimpleName();

    private final Context mContext;
    private final OnSpotifyClickListener mSpotifyClickListener;

    private ArrayList<SpotifyArtist> mArtists;

    public SpotifyAdapter(Context context, OnSpotifyClickListener spotifyClickListener) {
        mContext = context;
        mSpotifyClickListener = spotifyClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.artist_item, parent, false);
        return new ViewHolder(view, new ViewHolder.ViewHolderClickListener() {

            @Override
            public void onArtistClick(int position) {
                Log.d(LOG_TAG, "onArtistClick " + mArtists.get(position).getName());
                mSpotifyClickListener.onClick(mArtists.get(position).getId(),
                        mArtists.get(position).getName(),
                        mArtists.get(position).getAlbumArtUrl());
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mArtistName.setText(mArtists.get(position).getName());
        String albumArtUrl = mArtists.get(position).getAlbumArtUrl();
        if (!TextUtils.isEmpty(albumArtUrl)) {
            Glide.with(mContext)
                    .load(albumArtUrl)
                    .placeholder(R.drawable.vinyl)
                    .error(R.drawable.vinyl)
                    .into(viewHolder.mAlbumArt);
        }
    }

    @Override
    public int getItemCount() {
        return mArtists != null ? mArtists.size() : 0;
    }

    public void setArtists(ArrayList<SpotifyArtist> artists) {
        mArtists = artists;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView mAlbumArt;
        protected TextView mArtistName;
        private ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolderClickListener listener) {
            super(itemView);
            mListener = listener;
            mAlbumArt = (ImageView) itemView.findViewById(R.id.album_art);
            mArtistName = (TextView) itemView.findViewById(R.id.artist_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onArtistClick(getAdapterPosition());
        }

        public interface ViewHolderClickListener {

            void onArtistClick(int adapterPosition);
        }
    }

    public interface OnSpotifyClickListener {
        void onClick(String id, String name, String albumArtUrl);
    }
}
