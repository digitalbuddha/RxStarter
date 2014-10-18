package com.digitalbuddha.daggerdemo.rest;

/**
 * Created by MikeN on 8/16/14.
 */

import com.digitalbuddha.daggerdemo.models.Org;
import com.digitalbuddha.daggerdemo.models.Repo;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

public interface Github {

    @GET("/users/{userName}/repos")
    List<Repo> repos(@Path("userName") String user);

    @GET("orgs/square")
    Org getSquare();
}
