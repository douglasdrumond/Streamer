package com.cafelinear.streamer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cafelinear.streamer.R;
import com.cafelinear.streamer.adapters.SpotifyAdapter;
import com.cafelinear.streamer.model.SpotifyArtist;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchListFragment extends Fragment {

    private static final String ARTISTS_SAVE_KEY = "ARTISTS_SAVE_KEY";
    private static final String LOG_TAG = SearchListFragment.class.getSimpleName();


    private SpotifyAdapter mAdapter;

    // List of artists found
    private ArrayList<SpotifyArtist> mArtists;

    private Activity mHostActivity;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search_list, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mHostActivity, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SpotifyAdapter(mHostActivity, new SpotifyAdapter.OnSpotifyClickListener() {
            @Override
            public void onClick(String id, String name, String albumArtUrl) {
                mListener.onArtistSelected(id, name, albumArtUrl);
            }
        });
        recyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mArtists = savedInstanceState.getParcelableArrayList(ARTISTS_SAVE_KEY);
            mAdapter.setArtists(mArtists);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mArtists != null) {
            outState.putParcelableArrayList(ARTISTS_SAVE_KEY, mArtists);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mHostActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mHostActivity = null;
    }

    // TODO: 21/06/15 move to a Service, now it has a potentially long call tied to an Activity
    public void searchArtist(final String artistQueryString) {

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

                mHostActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!mArtists.isEmpty()) {
                            mAdapter.setArtists(mArtists);
                        } else {
                            Toast.makeText(mHostActivity, R.string.could_not_find_artist, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, "failure " + error.getMessage());
                mHostActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mHostActivity, R.string.artist_lookup_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onArtistSelected(String id, String name, String albumArtUrl);
    }
}
