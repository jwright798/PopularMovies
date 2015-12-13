package com.udacity.jeremywright.popularmovies.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeremywright on 12/12/15.
 */
public class TrailerDO implements Parcelable{

    private String name;
    private String key;

    public TrailerDO(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.key = object.getString("key");

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
    }

    public TrailerDO(Parcel in){
        this.name = in.readString();
        this.key = in.readString();

    }
    public static final Parcelable.Creator<TrailerDO> CREATOR
            = new Parcelable.Creator<TrailerDO>() {
        public TrailerDO createFromParcel(Parcel in) {
            return new TrailerDO(in);
        }

        public TrailerDO[] newArray(int size) {
            return new TrailerDO[size];
        }
    };
}
