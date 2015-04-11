package com.rx.demo.ui.view;

import android.graphics.Rect;
import android.view.View;

import com.rx.demo.commander.ImagesStore;
import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Result;
import com.rx.demo.util.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ImageSearchPresenter implements IViewPresenter {
    @Inject
    Queue<Result> que;

    @Inject
    ImagesStore store;

    @Inject
    SubscriptionManager subs;

    @Inject
    PublishSubject<Object> rowRequestStream;

    private ImageSearchView view;

    /**
     * Binds to view
     * Initializes subscriptions
     * Adds an on Scroll listner to load more images
     *
     * @param imageSearchView
     */
    @Override
    public void takeView(ImageSearchView imageSearchView) {
        view = imageSearchView;
        initQueue();
        subscribeToSearchTerm();
        addOnScrollListener();



    }

    /**
     * Subscribes to text changes in search view
     * clears the current queue and image results
     * and kicks off request for image results
     */
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

    /**
     * clear queue and view of previous results
     */
    protected void clearResults() {
        view.cardsLayout.removeAllViews();
        clearImageQueue();
    }

    /**
     * subscribe to any incoming image responses
     * map responses to a stream of images
     * add images to the image queue
     * finally request the UI to display each image
     *
     * NOTE:  request is made on main thread
     * everything else is made on IO thread
     */
    void initQueue() {
        subs.add(store.onNextObservable()
                .filter(imageResponse -> imageResponse.getResponseData() != null)
                .flatMap(this::streamOfImages)
                .doOnNext(this::addToQueue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rowRequestStream));
    }

    private void addToQueue(Result t1) {
        que.add(t1);
    }

    /**
     * subscribes to search view
     * transforms the onTextChange event into the search String
     * filters any blank searches
     * emits only the last emitted value when no search for 1 second
     * @return Observable containing the search term
     */
    private Observable<String> rxSearchTerm() {
        return WidgetObservable.text(view.getSearchView())
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    /**
     * creates observable that emits stream of images from a single image response
     * @param imageResponse
     * @return Observable that emit individual image results
     */
    private Observable<Result> streamOfImages(ImageResponse imageResponse) {
        return Observable.from(imageResponse.getResponseData().getResults());
    }

    public void clearImageQueue() {
        que.clear();
    }

    /**
     * kicks of network request for search term
     * as well as caching all addl pages
     * @param imageRequest search term
     * @return Observable Image response containing first page of image results
     */
    private Observable<ImageResponse> firstImages(ImageRequest imageRequest) {
        return store.fetchImageResults(imageRequest);
    }

    /**
     * determines whether the last row of images is visible on the screen
     * @return boolean
     */
    public boolean isLastRowVisible() {
        if (que.size() == 0) return false;
        Rect scrollBounds = new Rect();
        view.cardsLayout.getHitRect(scrollBounds);
        View lastRow = view.cardsLayout.getChildAt(view.cardsLayout.getChildCount() - 1);
        return lastRow == null || lastRow.getLocalVisibleRect(scrollBounds);
    }

    /**
     * If scrolled to last row, requests a new row to be added to View
     */
    private void addOnScrollListener() {
        view.cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (isLastRowVisible()) {
                if (que.size() != 0) {
                    rowRequestStream.onNext(new Object());
                }
            }
        });
    }

    /**
     *
     * @return 3 images from queue
     */
    public List<Result> getImageFromQueue() {
        List<Result> images = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (que.size() > 0)
                images.add(que.remove());
        }
        return images;
    }

    /**
     * unsubscribe when view is destroyed
     */
    @Override
    public void dropView() {
        view = null;
        subs.unsubscribeAll();
    }
}