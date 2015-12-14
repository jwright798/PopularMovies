package com.udacity.jeremywright.popularmovies.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

        if (savedInstanceState != null){
            sortType = savedInstanceState.getString("sort_type");
            movieDisplayList = savedInstanceState.getParcelableArrayList("movies");
        }
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
            MovieGridCallback callback = (MovieGridCallback)getActivity();
            callback.sortTypeChanged();
        } else if(!oldSortType.equals(sortType) && sortType.equalsIgnoreCase("Favorites")){
            sortByFavorites();
            MovieGridCallback callback = (MovieGridCallback)getActivity();
            callback.sortTypeChanged();

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
        if (sortType == null || sortType.isEmpty()) {
            sortType = "popularity.desc";
            serviceHelper.getMovieData(sortType, getString(R.string.API_KEY));
        }
        else if (sortType.equalsIgnoreCase("popularity.desc") || sortType.equalsIgnoreCase("vote_average.desc")){
            serviceHelper.getMovieData(sortType, getString(R.string.API_KEY));
        }
        else if (sortType.equalsIgnoreCase("Favorites")){
            sortByFavorites();
        }

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

    //needed to add callback for tablets
    public interface MovieGridCallback{
        public void movieClicked(MovieDO movie);
        public void sortTypeChanged();
    }

    public void sortByFavorites(){
        MovieSQLiteHelper db = new MovieSQLiteHelper(getActivity());
        ArrayList<MovieDO> movieList = db.getAllMovies();
        adapter.clear();
        adapter.addAll(movieList);
        movieDisplayList = movieList;

        //show a dialog if there are no favorites
        if(movieList.size() == 0){
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setTitle("Alert");
            dialog.setMessage("Movies you favorite will show up here.");
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("movieList", movieDisplayList);
        outState.putString("sort_type", sortType);

    }
}
