package com.example.bryan.myapplication;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by bryan on 8/5/17.
 */

public interface ProductInterface {
    // asynchronously with a callback
    @GET("/api/items/{upc}")
    Call<Product> getProduct(@Path("upc") String barcode);
}
