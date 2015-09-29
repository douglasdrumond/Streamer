package com.cafelinear.streamer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cafelinear.streamer.R;
import com.cafelinear.streamer.fragments.SearchListFragment;

import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends BaseActivity implements SearchListFragment.OnFragmentInteractionListener {

    private static final int TEXT_LENGTH_THRESHOLD = 3;
    private static final long DELAY_IN_MILLIS = 1000;

    private EditText mSearchQuery;
    private Timer mTimerToSend = new Timer();
    private SearchListFragment mSearchListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        configureToolbar();
        configureEditText();

        mSearchListFragment = (SearchListFragment) getFragmentManager().findFragmentById(R.id.fragment_search_list);
    }

    private void configureEditText() {
        mSearchQuery = (EditText) findViewById(R.id.edittext_search_query);
        mSearchQuery.addTextChangedListener(new TextWatcher() {

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
                                    mSearchListFragment.searchArtist(s.toString());
                                }
                            });
                        }
                    }, DELAY_IN_MILLIS);
                }
            }
        });

        mSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {

                    // Hide the keyboard
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchQuery.getWindowToken(), 0);

                    mSearchListFragment.searchArtist(v.getText().toString());
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onArtistSelected(String id, String name, String albumArtUrl) {
        Intent intent = new Intent(this, TopTracksActivity.class);
        intent.putExtra(TopTracksActivity.EXTRA_ARTIST_ID, id);
        intent.putExtra(TopTracksActivity.EXTRA_ARTIST_NAME, name);
        intent.putExtra(TopTracksActivity.EXTRA_ARTIST_ALBUM_ART_URL, albumArtUrl);
        startActivity(intent);
    }
}
