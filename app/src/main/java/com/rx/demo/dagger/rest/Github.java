package com.rx.demo.dagger.rest;

import com.rx.demo.dagger.model.User;

import java.util.ArrayList;

import retrofit.http.GET;
/**
 * Created by MikeN on 8/16/14.
 */
public interface Github {

    @GET("/users")
    rx.Observable<ArrayList<User>> users();
}
