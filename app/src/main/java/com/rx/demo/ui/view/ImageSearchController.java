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

public class ImageSearchController implements ViewPresenter {
    @Inject
    Queue<Result> que;

    @Inject
    ImagesStore store;

    @Inject
    SubscriptionManager subs;

    @Inject
    PublishSubject<Object> rowBus;

    private ImageSearchView view;


    @Override
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
                .doOnError(throwable -> {
                    throw new RuntimeException(throwable);
                })
                .subscribe();
    }

    protected void clearResults() {
        view.cardsLayout.removeAllViews();
        clearImageQueue();
    }

    void initQueue() {
        subs.addSubscription(store.onNextObservable()
                .observeOn(Schedulers.io())
                .filter(imageResponse -> imageResponse.getResponseData()!=null)
                .flatMap(this::streamOfImages)
                .doOnNext(this::addToQueue)
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(rowBus));
    }

   private void addToQueue(Result t1) {
        que.add(t1);
    }

    private Observable<String> rxSearchTerm() {
        return WidgetObservable.text(view.getSearchView())
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    private Observable<Result> streamOfImages(ImageResponse imageResponse) {
        return Observable.from(imageResponse.getResponseData().getResults());
    }

    public void clearImageQueue() {
        que.clear();
    }

    private Observable<ImageResponse> firstImages(ImageRequest imageRequest) {
        return store.fetchImageResults(imageRequest);
    }


    public boolean isLastRowVisible() {
        if(que.size() == 0) return false;
        Rect scrollBounds = new Rect();
        view.cardsLayout.getHitRect(scrollBounds);
        View lastRow = view.cardsLayout.getChildAt(view.cardsLayout.getChildCount() - 1);
        return lastRow == null || lastRow.getLocalVisibleRect(scrollBounds);
    }

    private void addOnScrollListener() {
        view.cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (isLastRowVisible()) {
                if (que.size() != 0) {
                    rowBus.onNext(new Object());
                }
            }
        });
    }

    @Override
    public void dropView() {
        view = null;
        subs.unsubscribeAll();
    }
}