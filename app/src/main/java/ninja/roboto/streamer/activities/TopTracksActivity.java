package ninja.roboto.streamer.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import ninja.roboto.streamer.GlideConfiguration;
import ninja.roboto.streamer.R;
import ninja.roboto.streamer.adapters.TopTracksAdapter;
import ninja.roboto.streamer.model.SpotifyTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopTracksActivity extends BaseActivity {

    public static final String EXTRA_ARTIST_ID = "EXTRA_ARTIST_ID";
    public static final String EXTRA_ARTIST_NAME = "EXTRA_ARTIST_NAME";
    public static final String EXTRA_ARTIST_ALBUM_ART_URL = "EXTRA_ARTIST_ALBUM_ART_URL";

    private static final String LOG_TAG = TopTracksActivity.class.getSimpleName();
    private static final String TRACKS_SAVE_KEY = "TRACKS_SAVE_KEY";

    private TopTracksAdapter mAdapter;
    private ArrayList<SpotifyTrack> mTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        configureToolbar();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_top_tracks);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ImageView toolbarImage = (ImageView) findViewById(R.id.toolbarImage);

        mAdapter = new TopTracksAdapter(this);
        recyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String trackId = extras.getString(EXTRA_ARTIST_ID);
            String trackName = extras.getString(EXTRA_ARTIST_NAME);
            String albumArtUrl = extras.getString(EXTRA_ARTIST_ALBUM_ART_URL);
            Glide.with(this).load(albumArtUrl).placeholder(R.drawable.vinyl).error(R.drawable.vinyl).into(toolbarImage);
            //noinspection ConstantConditions (we know it's not null)
            getSupportActionBar().setTitle(trackName);

            if (savedInstanceState == null) {
                searchTopTracks(trackId);
            } else {
                mTracks = savedInstanceState.getParcelableArrayList(TRACKS_SAVE_KEY);
                mAdapter.setTracks(mTracks);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACKS_SAVE_KEY, mTracks);
    }

    // TODO: 21/06/15 move to a Service, now it has a potentially long call tied to an Activity
    private void searchTopTracks(final String artistId) {

        mTracks = new ArrayList<>();

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        HashMap<String, Object> queryMap = new HashMap<>();
        queryMap.put("country", "BR");

        spotifyService.getArtistTopTrack(artistId, queryMap, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                int size = tracks.tracks.size();
                for (int i = 0; i < size; i++) {
                    Track track = tracks.tracks.get(i);
                    String albumArtUrl = null;
                    if (track.album.images != null && track.album.images.size() > 0) {
                        Image albumArt = track.album.images.get(0);
                        albumArtUrl = albumArt.url;
                    }
                    SpotifyTrack current = new SpotifyTrack(track.id, track.name, albumArtUrl);
                    mTracks.add(current);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mTracks.isEmpty()) {
                            mAdapter.setTracks(mTracks);
                        } else {
                            Toast.makeText(TopTracksActivity.this, R.string.no_top_tracks, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, "failure " + error.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TopTracksActivity.this, R.string.top_tracks_lookup_error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }
        });
    }
}
