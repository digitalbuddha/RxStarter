package com.rx.demo.commander;

import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Page;
import com.rx.demo.model.Result;
import com.rx.demo.rest.ImagesApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Nakhimovich on 3/25/15.
 */
@Singleton
public class ImagesStore extends RxStore<ImageRequest, ImageResponse> {
    @Inject
    ImagesApi api;

    @Override
    public ImageResponse load(ImageRequest request) throws Exception {
        return api.getPage(request.getSearchTerm(), request.getOffset());
    }

    public Observable<Result> getFirstPage(ImageRequest request) {
        return super.get(request)
                .doOnNext(imageResponse -> cacheAllPages(imageResponse.getResponseData().getCursor().getPages(), request.getSearchTerm()))
                .flatMap(imageStream());
    }

    private Func1<ImageResponse, Observable<? extends Result>> imageStream() {
        return imageResponse -> Observable.from(imageResponse.getResponseData().getResults());
    }

    private void cacheAllPages(List<Page> pages, String searchTerm) {
        for (Page page : pages) {
            updateCache(new ImageRequest(searchTerm, page.getStart()));
        }
    }
}
