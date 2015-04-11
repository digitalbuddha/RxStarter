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
import com.rx.demo.model.Result;
import com.rx.demo.di.RowContainer;
import com.rx.demo.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
    PublishSubject<Object> rowBus;
    @Inject
    @RowContainer
    Provider<LinearLayout> row;
    @Inject
    Queue<Result> que;


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

        getNotifiedWhenRowIsNeeded();
        controller.takeView(this);

    }

    private void getNotifiedWhenRowIsNeeded() {
        rowBus.debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> addRows());
    }


    public void addRow(List<Result> images) {

        for (Result image : images) {
            ImageCard imageCard = (ImageCard) inflate(getContext(), R.layout.user_card, null);
            imageCard.bindUserData(image, imageCard);
            getLastRow().addView(imageCard);
        }
        //putting a delay before drawing rows prevents jankiness with adding views while scrolling
        handler.postDelayed(this::addRows, 200);
    }

    private ViewGroup getLastRow() {
        ViewGroup lastRow = (ViewGroup) cardsLayout.getChildAt(cardsLayout.getChildCount() - 1);
        if (lastRow == null || lastRow.getChildCount() == 3) {
            lastRow = row.get();
            cardsLayout.addView(lastRow);
        }
        return lastRow;
    }

    public void addRows() {
        if (controller.isLastRowVisible()) {
            Log.e(this.getClass().getSimpleName(), "last row is visible on screen, load next rows");
            displayNextRow();
            displayNextRow();
        }
    }


    protected void displayNextRow() {
        List<Result> images = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (que.size() > 0)
                images.add(que.remove());
        }
        if(images.size()>0) addRow(images);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        controller.dropView();
    }

    public EditText getSearchView() {
        return search;
    }
}
