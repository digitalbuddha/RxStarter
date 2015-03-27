package com.rx.demo.rest;

import com.rx.demo.model.UserResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by MikeN on 8/16/14.
 */
public interface Github {
    @GET("/search/users")
    UserResponse users(@Query("q") String name);
}
