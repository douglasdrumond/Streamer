package ninja.roboto.streamer.adapters;

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

import java.util.ArrayList;

import ninja.roboto.streamer.R;
import ninja.roboto.streamer.model.SpotifyArtist;

public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.ViewHolder> {

    private static final String LOG_TAG = SpotifyAdapter.class.getSimpleName();

    private final Context mContext;

    private ArrayList<SpotifyArtist> mArtists;

    public SpotifyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.artist_item, viewGroup, false);
        return new ViewHolder(view, new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onArtistClick(int position) {
                // TODO: 21/06/15 start activity for this artist
                Log.d(LOG_TAG, "onArtistClick " + mArtists.get(position).getName());
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
        private ViewHolderClickListener mListener;
        protected ImageView mAlbumArt;
        protected TextView mArtistName;

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
