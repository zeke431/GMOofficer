package com.example.bryan.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by bryan on 8/31/17.
 */

public interface UserInterface {
    // asynchronously with a callback
    @GET("/api/user/{user_id}")
    Call<Product> getUser(@Path("user_id") String user_id);

    @POST("api/user")
    Call<User> createUser(@Body User user);

    @POST("api/users/login")
    Call<String> loginUser(@Body String email, String password);

}
