package com.digitalbuddha.daggerdemo.rest

import com.digitalbuddha.daggerdemo.model.Repo
import groovy.transform.CompileStatic;

/**
 * Created by MikeN on 8/16/14.
 */
import retrofit.http.GET
import retrofit.http.Path
import rx.Observable

@CompileStatic
public interface Github {
    @GET("/users/{userName}/repos")
    Observable<List<Repo>> repos(@Path("userName") String user)
}
