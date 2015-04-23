package com.rx.demo.ui.view.presenter;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.rx.demo.dao.ImageDao;
import com.rx.demo.di.annotation.HistoryViewBus;
import com.rx.demo.di.annotation.ImageViewBus;
import com.rx.demo.model.ImageRequest;
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

    /**
     * subscribes to search view
     * transforms the onTextChange event into the search String
     * filters any blank searches
     * emits only the last emitted value when no search for 1 second
     * on a new valid search occurs
     * clear screen and queue
     * emit new search terms to history view bus
     *
     * @return Observable containing the search term
     */
    private Observable<String> searchTermObservable() {
        return WidgetObservable.text(view.getSearchView())
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnNext(results -> clearResults())
                .doOnNext(getHistoryViewBus()::onNext);
    }

    /**
     * on search term change request images
     * subscribe with imagesBus
     * <p>
     * images become available -> emit to imagesBus
     * <p>
     * on emit to imagesBus -> try to add new rows
     * <p>
     * NOTE: imagesBus drops events followed by another event within 300ms
     */
    private void initSubs() {
        searchTermObservable()
                .observeOn(Schedulers.io())
                .flatMap(s -> dao.fetchImageResults(new ImageRequest(s)))
                .doOnNext(this::addToQueue)
                .doOnError(throwable -> {
                    throw new RuntimeException(throwable);
                })
                .subscribe(imagesBus);

        imagesBus.debounce(50, TimeUnit.MILLISECONDS, Schedulers.computation())
                .filter(o -> isLastRowVisible() && que.size() > 3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> displayNextRow());
    }


    /**
     * If scrolled to last row, emit new item to imagesBus
     */
    private void addOnScrollListener() {
        view.cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            imagesBus.onNext(que.peek());
        });
    }

    /**
     * gets images from queue and binds to newly created views
     */
    public void displayNextRow() {
        Log.e(this.getClass().getSimpleName(), "last row is visible on screen, load next rows");
        view.addRow();

    }

    public void drawNewRowIfNeeded() {
        imagesBus.onNext(que.peek());
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


    public void clearImageQueue() {
        que.clear();
    }


    /**
     * determines whether the last row of images is visible on the screen
     *
     * @return boolean
     */
    public boolean isLastRowVisible() {
        if (que.size() == 0 || view == null) {
            return false;
        }
        Rect scrollBounds = new Rect();
        view.cardsLayout.getHitRect(scrollBounds);
        View lastRow = view.cardsLayout.getChildAt(view.cardsLayout.getChildCount() - 1);
        return lastRow == null || lastRow.getLocalVisibleRect(scrollBounds);
    }


    /**
     * @return 3 images from queue
     */
    public List<Result> getImagesFromQueue() {
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