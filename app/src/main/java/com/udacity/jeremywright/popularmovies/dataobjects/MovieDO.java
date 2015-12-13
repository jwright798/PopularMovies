package com.udacity.jeremywright.popularmovies.dataobjects;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeremywright on 10/30/15.
 */
public class MovieDO implements Parcelable{

    private int movieID;
    private String posterPath;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private double popularity;
    private double voteAverage;

    public MovieDO(JSONObject object) throws JSONException{
        this.movieID = object.getInt("id");
        this.posterPath = object.getString("poster_path");
        this.originalTitle = object.getString("original_title");
        this.overview = object.getString("overview");
        this.popularity = object.getDouble("popularity");
        this.voteAverage = object.getDouble("vote_average");
        this.releaseDate = object.getString("release_date");
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieID);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeDouble(popularity);
        dest.writeString(releaseDate);
    }

    public MovieDO(Parcel in){
        this.movieID = in.readInt();
        this.posterPath = in.readString();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.popularity = in.readDouble();
        this.releaseDate = in.readString();
    }

    public MovieDO() {
        super();
    }

    public static final Parcelable.Creator<MovieDO> CREATOR
            = new Parcelable.Creator<MovieDO>() {
        public MovieDO createFromParcel(Parcel in) {
            return new MovieDO(in);
        }

        public MovieDO[] newArray(int size) {
            return new MovieDO[size];
        }
    };

    @Override
    public String toString() {
        return "MovieDO{" +
                "movieID=" + movieID +
                ", posterPath='" + posterPath + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", popularity=" + popularity +
                ", voteAverage=" + voteAverage +
                '}';
    }
}
