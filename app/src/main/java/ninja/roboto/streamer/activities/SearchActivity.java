package ninja.roboto.streamer.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ninja.roboto.streamer.R;
import ninja.roboto.streamer.adapters.SpotifyAdapter;

public class SearchActivity extends AppCompatActivity {

    private static final int TEXT_LENGTH_THRESHOLD = 3;
    private static final long DELAY_IN_MILLIS = 300;
    private Timer mTimerToSend = new Timer();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        configureToolbar();
        configureEditText();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SpotifyAdapter();
        mRecyclerView.setAdapter(mAdapter);
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

                        // TODO: only do this on wifi, if mobile network, prefer waiting for ime_action_search
                        // is it big enough to start a search?
                        if (s.length() >= TEXT_LENGTH_THRESHOLD) {

                            mTimerToSend = new Timer();
                            mTimerToSend.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO: 6/17/15 search
                                            Toast.makeText(SearchActivity.this, "Gonna search " + s.toString(), Toast.LENGTH_SHORT).show();
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

                            // TODO: 6/17/15 search
                            Toast.makeText(SearchActivity.this, "Gonna search " + v.getText().toString(), Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        return false;
                    }
                }
        );
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions (we know getSupportActionBar ≠ null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
