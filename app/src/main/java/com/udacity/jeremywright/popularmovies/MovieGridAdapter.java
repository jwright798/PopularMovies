package com.udacity.jeremywright.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jeremywright on 10/30/15.
 *
 */
public class MovieGridAdapter extends ArrayAdapter<MovieDO> {

    //I test on a Note 5 and Moto G, so this size worked perfectly for me.
    private String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w780/";

    public MovieGridAdapter(Context context, List<MovieDO> objects) {

        super(context, 0, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDO movie = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_grid_item, parent, false);
        }

        //Picasso was definitely the way to go here...
        ImageView posterImageView = (ImageView)convertView.findViewById(R.id.grid_image_view);
        String posterPath = BASE_POSTER_URL+movie.getPosterPath();
        Picasso.with(getContext()).load(posterPath).into(posterImageView);

        return convertView;
    }
}
