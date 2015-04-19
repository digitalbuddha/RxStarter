package com.rx.demo.ui.view.presenter;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.rx.demo.dao.ImageDao;
import com.rx.demo.di.annotation.HistoryViewBus;
import com.rx.demo.di.annotation.ImageViewBus;
import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Result;
import com.rx.demo.ui.view.SearchView;
import com.rx.demo.util.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Singleton
public class ImageSearchPresenter implements IViewPresenter {
    @Inject
    Queue<Result> que;

    @Inject
    ImageDao dao;

    @Inject
    SubscriptionManager subs;

    @Inject
    @ImageViewBus
    PublishSubject<Object> imagesBus;

    @Inject
    @HistoryViewBus
    PublishSubject<String> historyViewBus;

    private SearchView view;

    /**
     * Binds to view
     * Initializes subscriptions
     * Adds an on Scroll listner to load more images
     *
     * @param view view with search term and image results
     */
    @Override
    public void takeView(View view) {
        if (view == null) return;
        this.view = (SearchView) view;
        initSubs();
        addOnScrollListener();
    }

    private Observable<String> searchTermObservable() {
        return WidgetObservable.text(view.getSearchView())
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnNext(results -> clearResults())
                .doOnNext(getHistoryViewBus()::onNext);
    }
    /**
     * when a new search occurs
     * clear results and queue
     * emit new search terms to history view bus
     * request images
     * subscribe with imagesBus
     * <p>
     * images become available -> emit to imagesBus
     * <p>
     * on emit to imagesBus -> try to add new rows
     * <p>
     * NOTE: imagesBus drops events followed by another event within 300ms
     */
    void initSubs() {
        searchTermObservable()
                .observeOn(Schedulers.io())
                .flatMap(s -> dao.fetchImageResults(new ImageRequest(s)))
                .flatMap(this::streamOfImages)
                .doOnNext(this::addToQueue)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    throw new RuntimeException(throwable);
                })
                .subscribe(imagesBus);

        imagesBus.debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> addRows());
    }



    /**
     * If scrolled to last row, emit new item to imagesBus
     */
    private void addOnScrollListener() {
        view.cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (isLastRowVisible() && que.size() != 0) {
                imagesBus.onNext(que.peek());
            }
        });
    }

    /**
     * if last row of images is visible, create 2 more rows
     */
    public void addRows() {
        if (isLastRowVisible()) {
            Log.e(this.getClass().getSimpleName(), "last row is visible on screen, load next rows");
            displayNextRow();
            displayNextRow();
        }
    }


    /**
     * gets images from queue and binds to newly created views
     */
    protected void displayNextRow() {
        List<Result> images = getImageFromQueue();
        if (images.size() > 0) view.addRow(images);
    }


    /**
     * clear queue and view of previous results
     */
    public void clearResults() {
        view.cardsLayout.removeAllViews();
        clearImageQueue();
    }


    private void addToQueue(Result t1) {
        que.add(t1);
    }

    /**
     * subscribes to search view
     * transforms the onTextChange event into the search String
     * filters any blank searches
     * emits only the last emitted value when no search for 1 second
     *
     * @return Observable containing the search term
     */
    public Observable<String> rxSearchTerm() {
        return WidgetObservable.text(view.getSearchView())
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
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

    public void clearImageQueue() {
        que.clear();
    }

    /**
     * kicks of network request for search term
     * as well as caching all addl pages
     *
     * @param imageRequest search term
     * @return Observable Image response containing first page of image results
     */
    private Observable<ImageResponse> firstImages(ImageRequest imageRequest) {
        return dao.fetchImageResults(imageRequest);
    }

    /**
     * determines whether the last row of images is visible on the screen
     *
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

    public void changeSearchTerm(String s) {
        view.updateSearchView(s);
    }

    public PublishSubject<String> getHistoryViewBus() {
        return historyViewBus;
    }

}