package com.rx.demo.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.di.RowContainer;
import com.rx.demo.model.Result;
import com.rx.demo.ui.activity.BaseActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;


public class ImageSearchView extends ScrollView {
    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.cards)
    LinearLayout cardsLayout;
    @Inject
    ImageSearchPresenter controller;
    @Inject
    Handler handler;
    @Inject
    PublishSubject<Object> rowRequestStream;
    @Inject
    @RowContainer
    Provider<LinearLayout> row;


    public ImageSearchView(Context context) {
        this(context, null);
    }

    public ImageSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ((BaseActivity) context).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        subscribeToPresenter();
        controller.takeView(this);

    }

    /**
     * add new rows to the row container based on requests
     * emitted by presenter
     * NOTE:  if a burst of requests, will wait 500ms
     * after the last request  to start adding rows
     */
    private void subscribeToPresenter() {
        rowRequestStream.debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> addRows());
    }

    /**
     * if last row of images is visible, create 2 more rows
     */
    public void addRows() {
        if (controller.isLastRowVisible()) {
            Log.e(this.getClass().getSimpleName(), "last row is visible on screen, load next rows");
            displayNextRow();
            displayNextRow();
        }
    }

    /**
     * gets images from queue and binds to newly created views
     */
    protected void displayNextRow() {
        List<Result> images = controller.getImageFromQueue();
        if (images.size() > 0) addRow(images);
    }


    /**
     * Inflate a new row
     * Bind data to row
     * Add row to container
     * <p>
     * Note:putting a delay before drawing rows
     * prevents jankiness with adding views while scrolling
     *
     * @param images image data
     */
    public void addRow(List<Result> images) {

        for (Result image : images) {
            ImageCard imageCard = (ImageCard) inflate(getContext(), R.layout.user_card, null);
            imageCard.bindUserData(image, imageCard);
            getLastRow().addView(imageCard);
        }
        handler.postDelayed(this::addRows, 200);
    }

    /**
     * if less than 3 images in last row return it
     * else return a newly added linear layout
     *
     * @return ViewGroup with 0-2 images
     */
    private ViewGroup getLastRow() {
        ViewGroup lastRow = (ViewGroup) cardsLayout.getChildAt(cardsLayout.getChildCount() - 1);
        if (lastRow == null || lastRow.getChildCount() == 3) {
            lastRow = row.get();
            cardsLayout.addView(lastRow);
        }
        return lastRow;
    }


    /**
     * detach from presenter
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        controller.dropView();
    }

    public EditText getSearchView() {
        return search;
    }
}
