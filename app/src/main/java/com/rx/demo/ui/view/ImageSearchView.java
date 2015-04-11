package com.rx.demo.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.model.Result;
import com.rx.demo.ui.activity.DemoBaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ImageSearchView extends ScrollView {
    @Inject
    ImageSearchController controller;

    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.cards)
    LinearLayout cardsLayout;
    private Handler handler = new Handler();


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
        controller.takeView(this);
    }



    public void addCardView(Result image, ViewGroup lastRow) {
        UserCard userCard = (UserCard) inflate(getContext(), R.layout.user_card, null);
        userCard.bindUserData(image, userCard);
        lastRow.addView(userCard);
    }

    public void addRow(List<Result> images) {
        ViewGroup lastRow = (ViewGroup) cardsLayout.getChildAt(cardsLayout.getChildCount() - 1);
        if (lastRow == null || lastRow.getChildCount() == 3) {
            lastRow = controller.newRow();
            cardsLayout.addView(lastRow);
        }

        for (Result image : images) {
            addCardView(image, lastRow);
        }

        handler.postDelayed(this:: drawAnother,16);
    }

    private void drawAnother() {
        if(controller.isLastRowVisible())
            controller.displayNextRow();
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
