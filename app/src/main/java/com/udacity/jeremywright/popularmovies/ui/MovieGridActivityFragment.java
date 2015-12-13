package com.udacity.jeremywright.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.udacity.jeremywright.popularmovies.MovieServiceHelper;
import com.udacity.jeremywright.popularmovies.R;
import com.udacity.jeremywright.popularmovies.dataobjects.MovieDO;
import com.udacity.jeremywright.popularmovies.dataobjects.ReviewDO;
import com.udacity.jeremywright.popularmovies.dataobjects.TrailerDO;
import com.udacity.jeremywright.popularmovies.sql.MovieSQLiteHelper;

import java.util.ArrayList;

/**
 * Fragment that contains a gridview to display movies
 */
public class MovieGridActivityFragment extends Fragment implements MovieServiceHelper.MovieServiceDelegate {

    private MovieServiceHelper serviceHelper = new MovieServiceHelper();
    ArrayList<MovieDO> movieDisplayList;
    MovieGridAdapter adapter;
    String sortType;

    public MovieGridActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        serviceHelper.setMovieDelegate(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grid_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        //Same style as weather app
        String oldSortType = sortType;
        int id = item.getItemId();
        if(id == R.id.sort_popularity){
            sortType = "popularity.desc";
        }
        else if (id == R.id.sort_vote){
            sortType = "vote_average.desc";
        }
        else if (id == R.id.sort_favorites){
            sortType = "Favorites";
        }
        //Don't make an unneccessary api call
        if (!oldSortType.equals(sortType) && !sortType.equalsIgnoreCase("Favorites")){
            serviceHelper.getMovieData(sortType,  getString(R.string.API_KEY));
        } else if(sortType.equalsIgnoreCase("Favorites")){
            MovieSQLiteHelper db = new MovieSQLiteHelper(getActivity());
            //TODO check for empty list
            ArrayList<MovieDO> movieList = db.getAllMovies();
            adapter.clear();
            adapter.addAll(movieList);
            movieDisplayList = movieList;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie_grid, container, false);

        //Set up adapter
        movieDisplayList = new ArrayList<MovieDO>();

        adapter = new MovieGridAdapter(getActivity(),movieDisplayList);

        GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(adapter);

        //Set the onitemclicklistener (would be awesome if RecyclerView had this...)
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Go to the details page
                MovieDO movie = movieDisplayList.get(position);

                MovieGridCallback callback = (MovieGridCallback)getActivity();
                callback.movieClicked(movie);
            }
        });


        //Friendly reminder to those pulling down my code
        if (getString(R.string.API_KEY).isEmpty()){
            Toast.makeText(getActivity(), "API KEY IS EMPTY", Toast.LENGTH_LONG).show();
        }

        //Initial sort type
        sortType = "popularity.desc";
        serviceHelper.getMovieData(sortType, getString(R.string.API_KEY));

        return rootView;
    }

    @Override
    public void movieResponse(ArrayList<MovieDO> movieList) {
        if (movieList != null){
            adapter.clear();
            adapter.addAll(movieList);
            movieDisplayList = movieList;
        }
    }

    @Override
    public void trailerResponse(ArrayList<TrailerDO> jsonResponse) {

    }

    @Override
    public void reviewResponse(ArrayList<ReviewDO> jsonResponse) {

    }

    public interface MovieGridCallback{
        public void movieClicked(MovieDO movie);
    }

}
