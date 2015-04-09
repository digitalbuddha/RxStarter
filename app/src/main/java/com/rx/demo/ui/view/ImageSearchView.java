package com.rx.demo.ui.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.model.ImageRequest;
import com.rx.demo.model.Result;
import com.rx.demo.ui.activity.DemoBaseActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.ViewObservable;
import rx.schedulers.Schedulers;


public class ImageSearchView extends ScrollView {
    @Inject
    ImageSearchController controller;
    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.cards)
    LinearLayout cardsLayout;


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
        initSearchBox();
    }

    private void initSearchBox() {
        controller.initQueue();

        ViewObservable.bindView(this, controller.rxSearchTerm(search)
                .doOnNext(results -> clearCardsLayout())
                .observeOn(Schedulers.io())
                .flatMap(s -> controller.getFirstPage(new ImageRequest(s)))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                }))
                .subscribe(this::addCardView);


        cardsLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (controller.isLastCardVisible(cardsLayout)) {
                if (controller.getQe().size() != 0) {
                    displayNext(6);
                } else {
                    displayNoMoreResults();
                }
            }
        });

    }

    private void displayNext(int numToDisplay) {
        int elementsToShow = numToDisplay < controller.getQe().size() ? numToDisplay : controller.getQe().size();
        for (int i = 0; i < elementsToShow; i++) {
            addCardView(controller.getQe().remove());
        }
    }

    private void clearCardsLayout() {
        cardsLayout.removeAllViews();
        controller.clearImageQueue();
    }

    private void displayNoMoreResults() {
        // Toast.makeText(getContext(), "out of results", Toast.LENGTH_SHORT).show();
    }

    private void addCardView(Result image) {
        UserCard userCard = (UserCard) inflate(getContext(), R.layout.user_card, null);
        userCard.bindUserData(image, userCard);
        cardsLayout.addView(userCard);
    }


    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }
}
