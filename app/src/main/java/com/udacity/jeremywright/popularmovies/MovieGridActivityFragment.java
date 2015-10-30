package com.udacity.jeremywright.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Fragment that contains a gridview to display movies
 */
public class MovieGridActivityFragment extends Fragment {

    ArrayList<MovieDO> movieDisplayList;
    MovieGridAdapter adapter;
    String sortType;

    public MovieGridActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        //Don't make an unneccessary api call
        if (!oldSortType.equals(sortType)){
            FetchMoviesTask task = new FetchMoviesTask();
            task.execute(sortType);
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
                Intent intent = new Intent(getActivity(),MovieDetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });


        //Friendly reminder to those pulling down my code
        if (getString(R.string.API_KEY).isEmpty()){
            Toast.makeText(getActivity(), "API KEY IS EMPTY", Toast.LENGTH_LONG).show();
        }

        //Initial sort type
        sortType = "popularity.desc";
        FetchMoviesTask task = new FetchMoviesTask();
        task.execute(sortType);

        return rootView;
    }



    //using much of the same logic and structure from the Sunshine app, no use in reinventing the wheel
    public class FetchMoviesTask extends AsyncTask<String, ArrayList<MovieDO>, ArrayList<MovieDO>> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private ArrayList<MovieDO> getMovieDataFromJSON(String movieString)
                throws JSONException {

            ArrayList<MovieDO> movieList = new ArrayList<MovieDO>();

            JSONObject movieJSON = new JSONObject(movieString);
            JSONArray movieArray = movieJSON.getJSONArray("results");


            //Populate the arrayList by creating movie objects from the JSONObjects
            //Makes it a lot easier than trying to get values repeatidly out of JSONObjects
            for(int i=0; i<movieArray.length();i++){
                JSONObject movieObject = movieArray.getJSONObject(i);

                MovieDO movie = new MovieDO(movieObject);

                movieList.add(movie);

            }

            return movieList;

        }

        //Method from my Sunshine app
        @Override
        protected ArrayList<MovieDO> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieString = null;

            try {

                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter("sort_by", params[0])
                        .appendQueryParameter("api_key", getString(R.string.API_KEY))
                        .build();
                Log.v(LOG_TAG, builtUri.toString());

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));


                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    //+1 that did make it easier
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieString = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                return getMovieDataFromJSON(movieString);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }

            return  null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDO> movieList) {
            if (movieList != null){
                adapter.clear();
                adapter.addAll(movieList);
                movieDisplayList = movieList;
            }
        }
    }
}
