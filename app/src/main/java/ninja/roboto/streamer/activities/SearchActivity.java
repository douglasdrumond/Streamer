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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends AppCompatActivity {
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    private static final int TEXT_LENGTH_THRESHOLD = 3;
    private static final long DELAY_IN_MILLIS = 1000;

    private Timer mTimerToSend = new Timer();

    private SpotifyAdapter mAdapter;

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
    }

    private void configureEditText() {
        final EditText searchQuery = (EditText) findViewById(R.id.edittext_search_query);
        searchQuery.addTextChangedListener(
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

        searchQuery.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {

                            // Hide the keyboard
                            InputMethodManager imm
                                    = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);

                            searchArtist(v.getText().toString());
                            return true;
                        }

                        return false;
                    }
                }
        );
    }

    private void searchArtist(String artistQueryString) {

        // List of artists found
        final ArrayList<String[]> artists = new ArrayList<>();

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        spotifyService.searchArtists(artistQueryString, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {

                int size = artistsPager.artists.items.size();
                for (int i = 0; i < size; i++) {
                    String[] current = new String[2];
                    Artist artist = artistsPager.artists.items.get(i);
                    current[0] = artist.name;
                    if (artist.images != null && artist.images.size() > 0) {
                        Image albumArt = artist.images.get(0);
                        current[1] = albumArt.url;
                    }
                    artists.add(current);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!artists.isEmpty()) {
                            mAdapter.setArtists(artists);
                        } else {
                            Toast.makeText(SearchActivity.this, R.string.could_not_find_artist, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, "failure " + error.getMessage());
                Toast.makeText(SearchActivity.this, R.string.artist_lookup_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions (we know getSupportActionBar ≠ null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
