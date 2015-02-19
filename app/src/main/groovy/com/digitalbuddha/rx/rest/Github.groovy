package com.digitalbuddha.rx.rest

import com.digitalbuddha.rx.model.User
import groovy.transform.CompileStatic
import retrofit.http.GET;

/**
 * Created by MikeN on 8/16/14.
 */
import retrofit.http.Path

@CompileStatic
public interface Github {
    @GET("/users")
    ArrayList<User> repos(@Path("since") double randomOffset)
}
