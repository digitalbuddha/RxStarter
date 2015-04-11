package com.rx.demo.rest;

import com.rx.demo.model.ImageResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by MikeN on 8/16/14.
 */
public interface ImagesApi {
    @GET("/images?v=1.0&imgsz=medium")
    ImageResponse getPage(@Query("q") String searchTerm, @Query("start") String offset);

}
