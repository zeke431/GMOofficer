package com.example.bryan.myapplication;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bryan on 8/5/17.
 */

public class Product implements Parcelable {

    public String name;
    public String upc;
    public float price;

    Product(String name, String upc, float price){
        this.name = name;
        this.upc = upc;
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.upc);
        dest.writeFloat(this.price);
    }

    protected Product(Parcel in) {
        this.name = in.readString();
        this.upc = in.readString();
        this.price = in.readFloat();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
