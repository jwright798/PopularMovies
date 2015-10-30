package com.udacity.jeremywright.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Fragment that displays the details about the movie
 */
public class MovieDetailActivityFragment extends Fragment {

    private MovieDO movieDO;
    private String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w500/";

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get the movie object out of the intent
        movieDO = getActivity().getIntent().getExtras().getParcelable("movie");

        //Inflate the view and populate textviews
        View detailView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView titleTextView = (TextView)detailView.findViewById(R.id.movie_title);
        titleTextView.setText(movieDO.getOriginalTitle());

        TextView overviewTextView = (TextView)detailView.findViewById(R.id.overview_text_view);
        overviewTextView.setText(movieDO.getOverview());

        TextView voteAverageTextView = (TextView)detailView.findViewById(R.id.vote_average);
        voteAverageTextView.setText("Rating: "+Double.toString(movieDO.getVoteAverage())+"/10");

        //Convert date to more readable format
        //http://developer.android.com/reference/java/text/SimpleDateFormat.html

        SimpleDateFormat preFormat = new SimpleDateFormat("yyyy-MM-dd");
        String prettyReleaseDate = new String();
        try {
            Date releaseDate = preFormat.parse(movieDO.getReleaseDate());
            SimpleDateFormat newFormat = new SimpleDateFormat("LLLL d yyyy");
            prettyReleaseDate = newFormat.format(releaseDate);

        }
        catch (Exception e){
            Log.v("DetailFragment", e.getMessage());
            prettyReleaseDate = movieDO.getReleaseDate();
        }


        TextView releaseDateTextView = (TextView)detailView.findViewById(R.id.release_date);
        releaseDateTextView.setText(prettyReleaseDate);

        ImageView moviePoster = (ImageView)detailView.findViewById(R.id.movie_poster);
        String posterPath = BASE_POSTER_URL+movieDO.getPosterPath();
        Picasso.with(getActivity()).load(posterPath).into(moviePoster);

        return detailView;
    }
}
