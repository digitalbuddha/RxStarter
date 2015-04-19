package com.rx.demo.dao;

import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Page;
import com.rx.demo.rest.ImagesApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class ImageDao extends RxDao<ImageRequest, ImageResponse> {
    @Inject
    ImagesApi api;

    @Override
    public ImageResponse load(ImageRequest request) throws Exception {
        return api.getPage(request.getSearchTerm(), request.getOffset());
    }

    public Observable<ImageResponse> fetchImageResults(ImageRequest request) {
        return super.get(request)
                .flatMap(imageResponse -> Observable.from(imageResponse.getResponseData().getCursor().getPages()))
                .observeOn(Schedulers.newThread())
                .flatMap((Page page) -> get(new ImageRequest(request.getSearchTerm(), page.getStart())));
    }
}
