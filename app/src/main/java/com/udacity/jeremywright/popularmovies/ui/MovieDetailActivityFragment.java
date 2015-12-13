package com.udacity.jeremywright.popularmovies.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.jeremywright.popularmovies.dataobjects.MovieDO;
import com.udacity.jeremywright.popularmovies.MovieServiceHelper;
import com.udacity.jeremywright.popularmovies.R;
import com.udacity.jeremywright.popularmovies.dataobjects.ReviewDO;
import com.udacity.jeremywright.popularmovies.dataobjects.TrailerDO;
import com.udacity.jeremywright.popularmovies.sql.MovieSQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Fragment that displays the details about the movie
 */
public class MovieDetailActivityFragment extends Fragment implements MovieServiceHelper.MovieServiceDelegate {

    private MovieServiceHelper detailHelper = new MovieServiceHelper();
    private MovieDO movieDO;
    private String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w500/";
    private ArrayList<ReviewDO> reviewList = new ArrayList<ReviewDO>();
    private LinearLayout reviewsLayout;
    private LinearLayout trailersLayout;
    private MovieSQLiteHelper db;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set delegate for service helper
        detailHelper.setMovieDelegate(this);
        db =  new MovieSQLiteHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get the movie object out of the intent or arguments (in case of tablet)
        Bundle args = getArguments();

        if (args != null){
            movieDO = args.getParcelable("movie");
        }
        else {
            movieDO = getActivity().getIntent().getExtras().getParcelable("movie");
        }

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


        //Picasso snippit from implementation guide
        ImageView moviePoster = (ImageView)detailView.findViewById(R.id.movie_poster);
        String posterPath = BASE_POSTER_URL+movieDO.getPosterPath();
        Picasso.with(getActivity()).load(posterPath).into(moviePoster);

        final ImageView favoritesImageButton = (ImageView)detailView.findViewById(R.id.favorites_image);

        //favorites logic
        favoritesImageButton.setImageResource(android.R.drawable.star_big_off);
        if (db.isMovieFavorite(movieDO.getMovieID())){
            favoritesImageButton.setImageResource(android.R.drawable.star_big_on);
        }

        favoritesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (db.isMovieFavorite(movieDO.getMovieID())) {
                    db.deleteMovie(movieDO);
                    favoritesImageButton.setImageResource(android.R.drawable.star_big_off);
                    Toast.makeText(getActivity(),"Favorite removed", Toast.LENGTH_SHORT).show();
                }else{
                    db.addMovie(movieDO);
                    favoritesImageButton.setImageResource(android.R.drawable.star_big_on);
                    Toast.makeText(getActivity(),"Favorite added", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //get reference to trailers and reviews layouts (will fill later)
        trailersLayout = (LinearLayout) detailView.findViewById(R.id.trailer_layout);

        reviewsLayout = (LinearLayout) detailView.findViewById(R.id.reviews_layout);

        //make the call for reviews
        detailHelper.getReviews(Integer.toString(movieDO.getMovieID()),getString(R.string.API_KEY));

        return detailView;
    }

    //unused for detail view
    @Override
    public void movieResponse(ArrayList<MovieDO> movies) {

    }

    @Override
    public void trailerResponse(ArrayList<TrailerDO> trailers) {
        //get the trailers

        if (trailers != null && trailers.size() > 0){

            for (int i = 0; i<trailers.size(); i++) {

                final TrailerDO trailer = trailers.get(i);

                RelativeLayout trailerItemLayout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.trailer_layout_item, null);

                TextView nameTextView = (TextView)trailerItemLayout.findViewById(R.id.trailer_name);

                nameTextView.setText(trailer.getName());

                trailerItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //launch youtube in app (try first) and do generic web afterwards
                        //http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                        try{
                            Intent nativeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+trailer.getKey()));
                            nativeIntent.putExtra("VIDEO_ID", trailer.getKey());
                            startActivity(nativeIntent);

                        }catch (ActivityNotFoundException e){
                            Intent genericIntent = new Intent(Intent.ACTION_VIEW);
                            genericIntent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                            startActivity(genericIntent);
                        }
                    }
                });

                trailersLayout.addView(trailerItemLayout);
            }
        }
        else{
            RelativeLayout trailerItemLayout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.trailer_layout_item, null);

            ImageView playButton = (ImageView) trailerItemLayout.findViewById(R.id.trailer_play_button);
            playButton.setVisibility(View.GONE);
            TextView nameTextView = (TextView)trailerItemLayout.findViewById(R.id.trailer_name);

            nameTextView.setText("No trailers available");

            trailersLayout.addView(trailerItemLayout);
        }
    }

    //going to add the list of reviews we got back to the reviewslist by inflating a review view and adding it

    @Override
    public void reviewResponse(ArrayList<ReviewDO> reviews) {

        //get the reviews
        reviewList = reviews;
        if (reviews != null && reviews.size() > 0){
            //dynamically add reviews to layout
            for (int i = 0; i<reviews.size(); i++) {

                ReviewDO review = reviews.get(i);

                LinearLayout reviewLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.review_layout, null);
                TextView nameTextView = (TextView) reviewLayout.findViewById(R.id.reviewer_name);
                TextView contentTextView = (TextView) reviewLayout.findViewById(R.id.content);

                nameTextView.setText(review.getAuthor());
                contentTextView.setText(review.getContent());

                reviewsLayout.addView(reviewLayout);
            }
        }
        else{
            LinearLayout reviewLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.review_layout, null);
            TextView nameTextView = (TextView) reviewLayout.findViewById(R.id.reviewer_name);
            TextView contentTextView = (TextView) reviewLayout.findViewById(R.id.content);

            nameTextView.setText("No reviews available");
            contentTextView.setVisibility(View.GONE);

            reviewsLayout.addView(reviewLayout);
        }

        //make the calls to get the trailers (chose to do sequential calls vs dual calls)
        detailHelper.getTrailers(Integer.toString(movieDO.getMovieID()),getString(R.string.API_KEY));
    }
}
