package com.example.bryan.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bryan on 8/31/17.
 */

public class User implements Parcelable{

    private String token;

    User(String token){
        this.token = token;
    }

    @Override
    public int describeContents() {
        //super.describeContents();
        return 0;
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(this.name);
    }

    protected User(Parcel in) {
        //this.name = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
