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
import com.rx.demo.module.RowContainer;
import com.rx.demo.ui.activity.DemoBaseActivity;

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
    @Inject
    ImageSearchController controller;

    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.cards)
    LinearLayout cardsLayout;

    @Inject
    Handler handler;
    @Inject
    PublishSubject<Object> rowBus;

    @Inject @RowContainer
    Provider<LinearLayout> row;

    @Inject
    Queue<Result> qe;


    public ImageSearchView(Context context) {
        this(context, null);
    }

    public ImageSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ((DemoBaseActivity) context).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        rowBus.debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> addRows());
        controller.takeView(this);
    }


    public void addRow(List<Result> images) {

        for (Result image : images) {
            UserCard userCard = (UserCard) inflate(getContext(), R.layout.user_card, null);
            userCard.bindUserData(image, userCard);
            getLastRow().addView(userCard);
        }

        handler.postDelayed(this::addRows, 16);
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
            if (qe.size() > 0)
                images.add(qe.remove());
        }
        addRow(images);
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
