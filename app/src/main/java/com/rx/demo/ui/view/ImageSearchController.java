package com.rx.demo.ui.view;

import android.graphics.Rect;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rx.demo.commander.ImagesStore;
import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Result;
import com.rx.demo.util.SubscriptionManager;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;

@Singleton
public class ImageSearchController {
    private final Queue<Result> qe = new LinkedBlockingQueue<>(100);

    @Inject
    ImagesStore store;

    @Inject
    SubscriptionManager subscriptions;


    void initQueue() {
        subscriptions.addSubscription(store.onNextObservable()
                .observeOn(Schedulers.io())
                .filter(this::notFirstPage)
                .flatMap(this::imageStream)
                .doOnError(throwable -> {
                    Log.e(this.getClass().getSimpleName(), throwable.toString());
                })
                .doOnCompleted(() -> {

                }).subscribe(this::addToQueue));
    }

    void addToQueue(Result t1) {
        getQe().add(t1);
    }

    public boolean isLastCardVisible(ViewGroup cardsLayout) {
        Rect scrollBounds = new Rect();
        cardsLayout.getHitRect(scrollBounds);
        return cardsLayout.getChildAt(cardsLayout.getChildCount() - 1).getLocalVisibleRect(scrollBounds);
    }

    public Observable<String> rxSearchTerm(EditText search) {
        return WidgetObservable.text(search)
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(searchTerm -> searchTerm.length() > 0);
    }

    Observable<Result> imageStream(ImageResponse imageResponse) {
        return Observable.from(imageResponse.getResponseData().getResults());
    }

    boolean notFirstPage(ImageResponse imageResponse) {
        Integer currentPageIndex = imageResponse.getResponseData().getCursor().getCurrentPageIndex();
        return currentPageIndex != 0;
    }


    public void clearImageQueue() {
        getQe().clear();
    }

    public Observable<Result> getFirstPage(ImageRequest imageRequest) {
        return store.getFirstPage(imageRequest);
    }

    public Queue<Result> getQe() {
        return qe;
    }
}