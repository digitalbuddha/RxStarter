package com.rx.demo.ui.view;

import android.graphics.Rect;
import android.view.View;

import com.rx.demo.commander.ImagesStore;
import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Result;
import com.rx.demo.util.SubscriptionManager;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ImageSearchController {
    @Inject Queue<Result> qe;

    @Inject
    ImagesStore store;

    @Inject
    SubscriptionManager subscriptions;

    private ImageSearchView view;

    @Inject PublishSubject<Object> rowBus;

    public void takeView(ImageSearchView imageSearchView) {
        view = imageSearchView;
        initQueue();
        addOnScrollListener();
        subscribeToSearchTerm();

    }

    private void subscribeToSearchTerm() {
        rxSearchTerm()
                .doOnNext(results -> clearResults())
                .observeOn(Schedulers.io())
                .flatMap(s -> firstImages(new ImageRequest(s)))
                .subscribe();
    }

    protected void clearResults() {
        view.cardsLayout.removeAllViews();
        clearImageQueue();
    }

    void initQueue() {
        subscriptions.addSubscription(store.onNextObservable()
                .observeOn(Schedulers.io())
                .flatMap(this::streamOfImages)
                .doOnNext(this::addToQueue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rowBus));
    }

    void addToQueue(Result t1) {
        qe.add(t1);
    }

    public Observable<String> rxSearchTerm() {
        return WidgetObservable.text(view.getSearchView())
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    Observable<Result> streamOfImages(ImageResponse imageResponse) {
        return Observable.from(imageResponse.getResponseData().getResults());
    }

    public void clearImageQueue() {
        qe.clear();
    }

    public Observable<ImageResponse> firstImages(ImageRequest imageRequest) {
        return store.fetchImageResults(imageRequest);
    }


    public boolean isLastRowVisible() {
        Rect scrollBounds = new Rect();
        view.cardsLayout.getHitRect(scrollBounds);
        View lastRow = view.cardsLayout.getChildAt(view.cardsLayout.getChildCount() - 1);
        return lastRow == null || lastRow.getLocalVisibleRect(scrollBounds);
    }

    private void addOnScrollListener() {
        view.cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (isLastRowVisible()) {
                if (qe.size() != 0) {
                    rowBus.onNext(new Object());
                }
            }
        });
    }

    public void dropView() {
        view = null;
        subscriptions.unsubscribeAll();
    }
}