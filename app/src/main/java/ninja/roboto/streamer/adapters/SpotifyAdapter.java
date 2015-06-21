package ninja.roboto.streamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ninja.roboto.streamer.R;
import ninja.roboto.streamer.activities.SearchActivity;

public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.ViewHolder> {

    private static final int ARTIST_NAME_INDEX = 0;
    private static final int ALBUM_ART_URL_INDEX = 1;
    private final Context mContext;

    private ArrayList<String[]> mArtists; // each item: [0] artist name, [1] album art URL

    public SpotifyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.artist_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mArtistName.setText(mArtists.get(position)[ARTIST_NAME_INDEX]);
        Glide.with(mContext)
                .load(mArtists.get(position)[ALBUM_ART_URL_INDEX])
                .into(viewHolder.mAlbumArt);
    }

    @Override
    public int getItemCount() {
        return mArtists != null ? mArtists.size() : 0;
    }

    public void setArtists(ArrayList<String[]> artists) {
        mArtists = artists;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView mAlbumArt;
        protected TextView mArtistName;

        public ViewHolder(View itemView) {
            super(itemView);
            mAlbumArt = (ImageView) itemView.findViewById(R.id.album_art);
            mArtistName = (TextView) itemView.findViewById(R.id.artist_name);
        }
    }
}
