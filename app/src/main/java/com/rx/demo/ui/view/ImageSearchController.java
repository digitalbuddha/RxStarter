package com.rx.demo.ui.view;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.rx.demo.commander.ImagesStore;
import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.ImageResponse;
import com.rx.demo.model.Result;
import com.rx.demo.util.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ImageSearchController {
    private final Queue<Result> qe = new ConcurrentLinkedQueue<>();

    @Inject
    ImagesStore store;

    @Inject
    SubscriptionManager subscriptions;

    private ImageSearchView view;


    private PublishSubject<Object> rowsNeededObservable = PublishSubject.create();

    public void takeView(ImageSearchView imageSearchView) {
        view = imageSearchView;
        initQueue();
        addOnScrollListener();
        subscribeToSearchTerm();
        rowsNeededObservable.debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> addRows());
    }

    private void subscribeToSearchTerm() {
        rxSearchTerm()
                .doOnNext(results -> clearCardsLayout())
                .observeOn(Schedulers.io())
                .flatMap(s -> firstImages(new ImageRequest(s)))
                .subscribe();
    }

    protected void clearCardsLayout() {
        view.cardsLayout.removeAllViews();
        clearImageQueue();
    }

    void initQueue() {
        subscriptions.addSubscription(store.onNextObservable()
                .observeOn(Schedulers.io())
                .flatMap(this::streamOfImages)
                .doOnNext(this::addToQueue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rowsNeededObservable));
    }

    public void addRows() {
        if (isLastRowVisible()) {
            Log.e(this.getClass().getSimpleName(), "last row is visible on screen, load next row");
            displayNextRow();
            displayNextRow();
        }
    }


    void addToQueue(Result t1) {
        getQe().add(t1);
    }


    public boolean isLastRowVisible() {
        Rect scrollBounds = new Rect();
        view.cardsLayout.getHitRect(scrollBounds);
        View lastRow = view.cardsLayout.getChildAt(view.cardsLayout.getChildCount() - 1);
        return lastRow == null || lastRow.getLocalVisibleRect(scrollBounds);
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
        getQe().clear();
    }

    public Observable<ImageResponse> firstImages(ImageRequest imageRequest) {
        return store.fetchImageResults(imageRequest);
    }

    public Queue<Result> getQe() {
        return qe;
    }


    private void addOnScrollListener() {
        view.cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (isLastRowVisible()) {
                if (getQe().size() != 0) {
                    rowsNeededObservable.onNext(new Object());
                }
            }
        });
    }


    protected void displayNextRow() {
        List<Result> images = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (getQe().size() > 0)
                images.add(getQe().remove());
        }
        view.addRow(images);

    }


    public LinearLayout newRow() {
        LinearLayout row = new LinearLayout(view.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(params);
        return row;
    }

    public void dropView() {
        view = null;
        subscriptions.unsubscribeAll();
    }
}