package com.cafelinear.streamer.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.cafelinear.streamer.activities.TopTracksActivity;
import com.cafelinear.streamer.model.SpotifyArtist;

import java.util.ArrayList;

public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.ViewHolder> {

    private static final String LOG_TAG = SpotifyAdapter.class.getSimpleName();

    private final Context mContext;

    private ArrayList<SpotifyArtist> mArtists;

    public SpotifyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.artist_item, parent, false);
        return new ViewHolder(view, new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onArtistClick(int position) {
                // TODO: 21/06/15 start activity for this artist
                Log.d(LOG_TAG, "onArtistClick " + mArtists.get(position).getName());
                Intent intent = new Intent(mContext, TopTracksActivity.class);
                intent.putExtra(TopTracksActivity.EXTRA_ARTIST_ID, mArtists.get(position).getId());
                intent.putExtra(TopTracksActivity.EXTRA_ARTIST_NAME, mArtists.get(position).getName());
                intent.putExtra(TopTracksActivity.EXTRA_ARTIST_ALBUM_ART_URL, mArtists.get(position).getAlbumArtUrl());
                mContext.startActivity(intent);
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
}
