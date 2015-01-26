package com.digitalbuddha.daggerdemo.rest

import com.digitalbuddha.daggerdemo.model.Repo
import groovy.transform.CompileStatic
import retrofit.http.GET;

/**
 * Created by MikeN on 8/16/14.
 */
import retrofit.http.Path

@CompileStatic
public interface Github {
    @GET("/users/{userName}/repos")
    ArrayList<Repo> repos(@Path("userName") String user)
}
