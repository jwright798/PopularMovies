package com.udacity.jeremywright.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.udacity.jeremywright.popularmovies.R;

public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setTitle("Movie Details");

        if (savedInstanceState == null){
            MovieDetailActivityFragment detailFragment = new MovieDetailActivityFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, detailFragment).commit();
        }

    }

}
