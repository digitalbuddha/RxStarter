package com.rx.demo.commander;

import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Page;
import com.rx.demo.rest.ImagesApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ImagesStore extends RxStore<ImageRequest, ImageResponse> {
    @Inject
    ImagesApi api;

    @Override
    public ImageResponse load(ImageRequest request) throws Exception {
        return api.getPage(request.getSearchTerm(), request.getOffset());
    }

    public Observable<ImageResponse> fetchImageResults(ImageRequest request) {
        return super.get(request)
                .doOnNext(imageResponse ->
                {
                    if(imageResponse.getResponseData()!=null)
                        cacheAllPages(imageResponse.getResponseData().getCursor().getPages(), request.getSearchTerm());
                }
                );
    }



    private void cacheAllPages(List<Page> pages, String searchTerm) {
        //first page already cached
        for (int i = 1; i < pages.size(); i++) {
            updateCache(new ImageRequest(searchTerm, pages.get(i).getStart()));
        }
    }
}
