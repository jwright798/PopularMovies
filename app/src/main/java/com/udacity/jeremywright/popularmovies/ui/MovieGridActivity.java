package com.udacity.jeremywright.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.jeremywright.popularmovies.R;
import com.udacity.jeremywright.popularmovies.dataobjects.MovieDO;

public class MovieGridActivity extends ActionBarActivity implements MovieGridActivityFragment.MovieGridCallback {

    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        //check for tablet sw600dp case
        if (findViewById(R.id.detail_container) != null){
            isTablet = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void movieClicked(MovieDO movie) {

        //implementation from Sunshine app
        if (isTablet){
           Bundle args = new Bundle();
            args.putParcelable("movie", movie);
            Fragment detailFragment = new MovieDetailActivityFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, detailFragment, "detail").commit();
        }
        else{
            Intent intent = new Intent(this,MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        }
    }

    //clear detail view (makes changing sort types cleaner)
    @Override
    public void sortTypeChanged() {
        if (isTablet) {
            Fragment detailFragment = getSupportFragmentManager().findFragmentByTag("detail");
            if (detailFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
            }
        }
    }
}
