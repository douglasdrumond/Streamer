package ninja.roboto.streamer.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import ninja.roboto.streamer.R;
import ninja.roboto.streamer.adapters.SpotifyAdapter;
import ninja.roboto.streamer.model.SpotifyArtist;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends BaseActivity {
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    private static final int TEXT_LENGTH_THRESHOLD = 3;
    private static final long DELAY_IN_MILLIS = 1000;
    private static final String ARTISTS_SAVE_KEY = "ARTISTS_SAVE_KEY";

    private Timer mTimerToSend = new Timer();

    private SpotifyAdapter mAdapter;

    // List of artists found
    private ArrayList<SpotifyArtist> mArtists;

    private EditText mSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        configureToolbar();
        configureEditText();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SpotifyAdapter(this);
        recyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mArtists = savedInstanceState.getParcelableArrayList(ARTISTS_SAVE_KEY);
            mAdapter.setArtists(mArtists);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mArtists != null) {
            outState.putParcelableArrayList(ARTISTS_SAVE_KEY, mArtists);
        }
        super.onSaveInstanceState(outState);
    }

    private void configureEditText() {
        mSearchQuery = (EditText) findViewById(R.id.edittext_search_query);
        mSearchQuery.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Unused method
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Avoid searching if user is still typing
                        if (mTimerToSend != null) {
                            mTimerToSend.cancel();
                        }
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {

                        // TODO: only do this search on-the-fly on wifi
                        // if on mobile network, prefer waiting for ime_action_search

                        // is it big enough to start a search?
                        if (s.length() >= TEXT_LENGTH_THRESHOLD) {

                            mTimerToSend = new Timer();
                            mTimerToSend.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchArtist(s.toString());
                                        }
                                    });

                                }

                            }, DELAY_IN_MILLIS);
                        }
                    }
                });

        mSearchQuery.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {

                            // Hide the keyboard
                            InputMethodManager imm
                                    = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mSearchQuery.getWindowToken(), 0);

                            searchArtist(v.getText().toString());
                            return true;
                        }

                        return false;
                    }
                }
        );
    }

    // TODO: 21/06/15 move to a Service, now it has a potentially long call tied to an Activity
    private void searchArtist(final String artistQueryString) {

        mArtists = new ArrayList<>();

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        spotifyService.searchArtists(artistQueryString, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {

                int size = artistsPager.artists.items.size();
                for (int i = 0; i < size; i++) {
                    Artist artist = artistsPager.artists.items.get(i);
                    String albumArtUrl = null;
                    if (artist.images != null && artist.images.size() > 0) {
                        Image albumArt = artist.images.get(0);
                        albumArtUrl = albumArt.url;
                    }

                    SpotifyArtist current = new SpotifyArtist(artist.id, artist.name, albumArtUrl);
                    mArtists.add(current);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mArtists.isEmpty()) {
                            mAdapter.setArtists(mArtists);
                        } else {
                            Toast.makeText(SearchActivity.this, R.string.could_not_find_artist, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SearchActivity.this, R.string.artist_lookup_error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
