package com.rx.demo.dao;

import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Page;
import com.rx.demo.model.Result;
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

    public Observable<Result> fetchImageResults(ImageRequest request) {
        return super.get(request)
                .flatMap(imageResponse -> Observable.from(imageResponse.getResponseData().getCursor().getPages()))
                .observeOn(Schedulers.newThread())
                .concatMap((Page page) -> get(new ImageRequest(request.getSearchTerm(), page.getStart())))
                .concatMap(this::streamOfImages);


    }


    /**
     * creates observable that emits stream of images from a single image response
     *
     * @param imageResponse
     * @return Observable that emit individual image results
     */
    private Observable<Result> streamOfImages(ImageResponse imageResponse) {
        return Observable.from(imageResponse.getResponseData().getResults());
    }

}
