package com.udacity.jeremywright.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.jeremywright.popularmovies.dataobjects.MovieDO;
import com.udacity.jeremywright.popularmovies.dataobjects.ReviewDO;
import com.udacity.jeremywright.popularmovies.dataobjects.TrailerDO;

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
 * Created by jeremywright on 12/12/15.
 */
public class MovieServiceHelper {

    //use a delegate to get callbacks from the service
    private MovieServiceDelegate movieDelegate;
    private final String LOG_TAG = MovieServiceHelper.class.getSimpleName();


    public void setMovieDelegate(MovieServiceDelegate className){
        movieDelegate = className;
    }


    //This call gets the list of movies for the main list
    public void getMovieData(String sortType, String apiKey){
        //just in case
        if(movieDelegate != null){
            AsyncTask movieDataTask = new AsyncTask() {

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

                @Override
                protected Object doInBackground(Object[] params) {
                    // These two need to be declared outside the try/catch
                    // so that they can be closed in the finally block
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

                    // Will contain the raw JSON response as a string.
                    String movieString = null;

                    try {

                        final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                        String sortType = (String) params[0];
                        String apiKey = (String)params[1];

                        Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter("sort_by",sortType)
                                .appendQueryParameter("api_key", apiKey )
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
                protected void onPostExecute(Object responseString) {
                    if (movieDelegate!= null) {
                        movieDelegate.movieResponse((ArrayList<MovieDO>) responseString);
                    }
                }
            };
            Object[] sortTypeParam = new Object[2];
            sortTypeParam[0] = sortType;
            sortTypeParam[1] = apiKey;
            movieDataTask.execute(sortTypeParam);
        }
    }

    //service calls to get the Reviews
    public void getReviews(String movieID, String apiKey){
        //just in case
        if(movieDelegate != null){
            AsyncTask movieDataTask = new AsyncTask() {

                private ArrayList<ReviewDO> getReviewsFromJSON(String resultsString)
                        throws JSONException {

                    ArrayList<ReviewDO> reviewsList = new ArrayList<ReviewDO>();

                    JSONObject reviewJSON = new JSONObject(resultsString);
                    JSONArray reviewsArray = reviewJSON.getJSONArray("results");


                    //Populate the arrayList by creating movie objects from the JSONObjects
                    //Makes it a lot easier than trying to get values repeatidly out of JSONObjects
                    for(int i=0; i<reviewsArray.length();i++){
                        JSONObject reviewObject = reviewsArray.getJSONObject(i);

                        ReviewDO review = new ReviewDO(reviewObject);

                        reviewsList.add(review);

                    }

                    return reviewsList;

                }

                @Override
                protected Object doInBackground(Object[] params) {
                    // These two need to be declared outside the try/catch
                    // so that they can be closed in the finally block
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

                    // Will contain the raw JSON response as a string.
                    String reviewString = null;

                    try {

                        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                        String movieId = (String) params[0];
                        String apiKey = (String) params[1];

                        String reviewURL = BASE_URL + movieId + "/reviews?api_key="+apiKey;
                        Log.v(LOG_TAG, reviewURL);

                        URL url = new URL(reviewURL);
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
                        reviewString = buffer.toString();
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
                        return getReviewsFromJSON(reviewString);
                    }catch (JSONException e){
                        Log.e(LOG_TAG, e.getMessage(),e);
                        e.printStackTrace();
                    }

                    return  null;
                }

                @Override
                protected void onPostExecute(Object responseString) {
                    if (movieDelegate!= null) {
                        movieDelegate.reviewResponse((ArrayList<ReviewDO>) responseString);
                    }
                }
            };
            Object[] reviewsParams = new Object[2];
            reviewsParams[0] = movieID;
            reviewsParams[1] = apiKey;
            movieDataTask.execute(reviewsParams);
        }
    }

    //Service call to get Trailers
    public void getTrailers(String movieID, String apiKey){
        //just in case
        if(movieDelegate != null){
            AsyncTask movieDataTask = new AsyncTask() {

                private ArrayList<TrailerDO> getTrailersFromJSON(String resultsString)
                        throws JSONException {

                    ArrayList<TrailerDO> trailersList = new ArrayList<TrailerDO>();

                    JSONObject trailerJSON = new JSONObject(resultsString);
                    JSONArray trailerArray = trailerJSON.getJSONArray("results");


                    //Populate the arrayList by creating movie objects from the JSONObjects
                    //Makes it a lot easier than trying to get values repeatidly out of JSONObjects
                    for(int i=0; i<trailerArray.length();i++){
                        JSONObject trailerObject = trailerArray.getJSONObject(i);

                        TrailerDO trailer = new TrailerDO(trailerObject);

                        trailersList.add(trailer);

                    }

                    return trailersList;

                }

                @Override
                protected Object doInBackground(Object[] params) {
                    // These two need to be declared outside the try/catch
                    // so that they can be closed in the finally block
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

                    // Will contain the raw JSON response as a string.
                    String trailerString = null;

                    try {

                        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                        String movieId = (String) params[0];
                        String apiKey = (String) params[1];

                        String reviewURL = BASE_URL + movieId + "/videos?api_key="+apiKey;
                        Log.v(LOG_TAG, reviewURL);

                        URL url = new URL(reviewURL);
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
                        trailerString = buffer.toString();
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
                        return getTrailersFromJSON(trailerString);
                    }catch (JSONException e){
                        Log.e(LOG_TAG, e.getMessage(),e);
                        e.printStackTrace();
                    }

                    return  null;
                }

                @Override
                protected void onPostExecute(Object responseString) {
                    if (movieDelegate!= null) {
                        movieDelegate.trailerResponse((ArrayList<TrailerDO>) responseString);
                    }
                }
            };
            Object[] trailerParams = new Object[2];
            trailerParams[0] = movieID;
            trailerParams[1] = apiKey;
            movieDataTask.execute(trailerParams);
        }
    }

    //Delegate for callbacks
    public interface MovieServiceDelegate {
        public void movieResponse(ArrayList<MovieDO> movies);
        public void trailerResponse(ArrayList<TrailerDO> jsonResponse);
        public void reviewResponse(ArrayList<ReviewDO> jsonResponse);
    }
}
