package com.udacity.jeremywright.popularmovies.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeremywright on 12/12/15.
 */
public class ReviewDO implements Parcelable {

    private String author;
    private String content;

    public ReviewDO(JSONObject object) throws JSONException {
        this.author = object.getString("author");
        this.content = object.getString("content");

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public ReviewDO(Parcel in){
        this.author = in.readString();
        this.content = in.readString();

    }
    public static final Parcelable.Creator<ReviewDO> CREATOR
            = new Parcelable.Creator<ReviewDO>() {
        public ReviewDO createFromParcel(Parcel in) {
            return new ReviewDO(in);
        }

        public ReviewDO[] newArray(int size) {
            return new ReviewDO[size];
        }
    };
}
