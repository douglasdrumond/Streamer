package com.cafelinear.streamer.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cafelinear.streamer.R;

public class BaseActivity extends AppCompatActivity {

    protected void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions (we know getSupportActionBar ≠ null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
