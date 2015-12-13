package com.udacity.jeremywright.popularmovies.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.udacity.jeremywright.popularmovies.dataobjects.MovieDO;

import java.util.ArrayList;

/**
 * Created by jeremywright on 12/13/15.
 * Used tutorial from http://hmkcode.com/android-simple-sqlite-database-tutorial/
 */
public class MovieSQLiteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION =1;
    private static final String DB_NAME = "FavoritesDB";

    private static final String TABLE_FAVS = "favorites";

    //Column names
    private static final String ID_KEY = "id";
    private static final String ORIGINAL_TITLE_KEY = "original_title";
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String OVERVIEW_KEY = "overview";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String POPULARITY_KEY = "popularity";
    private static final String VOTE_AVG_KEY = "vote_average";

    private static final String[] COLUMNS = {ID_KEY, ORIGINAL_TITLE_KEY,POSTER_PATH_KEY,OVERVIEW_KEY,RELEASE_DATE_KEY,POPULARITY_KEY,VOTE_AVG_KEY};

    public MovieSQLiteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAV_TABLE = "CREATE TABLE favorites ( "+
                "id INTEGER PRIMARY KEY, "+
                "original_title STRING, "+
                "poster_path STRING, "+
                "overview STRING, "+
                "release_date STRING, "+
                "popularity REAL, "+
                "vote_average REAL )";
        db.execSQL(CREATE_FAV_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favorites");

        this.onCreate(db);
    }

    public void addMovie(MovieDO movie){
        Log.v("addMovie", movie.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_KEY, movie.getMovieID());
        values.put(ORIGINAL_TITLE_KEY, movie.getOriginalTitle());
        values.put(POSTER_PATH_KEY, movie.getPosterPath());
        values.put(OVERVIEW_KEY, movie.getOverview());
        values.put(RELEASE_DATE_KEY, movie.getReleaseDate());
        values.put(POPULARITY_KEY, movie.getPopularity());
        values.put(VOTE_AVG_KEY, movie.getVoteAverage());

        db.insert(TABLE_FAVS,
                null,
                values);
        db.close();
    }

    //checks if the movie is currently in the favoritesdb
    public boolean isMovieFavorite(int id){
        boolean isFavorite = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_FAVS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        //check results, only way I could get the boolean to work correctly
        if (cursor != null) {
            if (cursor.moveToFirst()){
                MovieDO movie = new MovieDO();
                movie.setMovieID(Integer.parseInt(cursor.getString(0)));
                isFavorite = true;
            }
            else {
                isFavorite = false;
            }
        }
        return isFavorite;
    }

    // Get All Movies
    public ArrayList<MovieDO> getAllMovies() {
        ArrayList<MovieDO> movies = new ArrayList<MovieDO>();

        String query = "SELECT  * FROM " + TABLE_FAVS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        MovieDO movie = null;
        if (cursor.moveToFirst()) {
            do {
                movie = new MovieDO();
                movie.setMovieID(Integer.parseInt(cursor.getString(0)));
                movie.setOriginalTitle(cursor.getString(1));
                movie.setPosterPath(cursor.getString(2));
                movie.setOverview(cursor.getString(3));
                movie.setReleaseDate(cursor.getString(4));
                movie.setPopularity(cursor.getDouble(5));
                movie.setVoteAverage(cursor.getDouble(6));
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        Log.v("getAllMovies()", movies.toString());

        return movies;
    }

    // Deleting a movie
    public void deleteMovie(MovieDO movieDO) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVS,
                ID_KEY+" = ?",
                new String[] { String.valueOf(movieDO.getMovieID()) });
        db.close();

        Log.v("deleteMovie", movieDO.toString());

    }

}

