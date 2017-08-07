package com.example.bryan.myapplication;

import android.media.Image;

/**
 * Created by bryan on 8/5/17.
 */

public class Product {

    private String name;
    private String upc;
    private String description;
    private Image image;

    Product(String name, String upc, String description, Image image){
        this.name = name;
        this.upc = upc;
        this.description = description;
        this.image = image;
    }
}
