package com.rx.demo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ViewFlipper;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.di.UiModule;
import com.rx.demo.ui.animation.AnimationFactory;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.Icicle;

import static butterknife.ButterKnife.findById;


public class SearchActivity extends BaseActivity {

    public static final int SEARCH_VIEW_POSITION = 0;

    @InjectView(R.id.container)
    ViewFlipper flipper;

    @InjectView(R.id.history)
    View historyButton;

    @Icicle
    int selectedPosition = 0;

    /**
     * inflate search and history view and add the to container
     * set click listener for history button
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_layout);

        ButterKnife.inject(this);
        flipper = findById(this, R.id.container);
        getLayoutInflater().inflate(R.layout.search_view, flipper);
        getLayoutInflater().inflate(R.layout.history_view, flipper);
        showView(selectedPosition);
    }

    @OnClick(R.id.history)
    public void historyButtonClicked() {
        flipViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        selectedPosition = flipper.getDisplayedChild();
        Icepick.saveInstanceState(this, outState);
    }

    protected List<Object> getModules() {
        return Arrays.asList(new UiModule(this));
    }

    public void showView(int index) {
        flipper.setDisplayedChild(index);
        syncHistoryButton();
    }

    public void flipViews() {
        AnimationFactory.flipTransition(flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
        syncHistoryButton();
    }

    public void syncHistoryButton() {
        historyButton.setVisibility(flipper.getDisplayedChild() == SEARCH_VIEW_POSITION ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (flipper.getDisplayedChild() != SEARCH_VIEW_POSITION) {
            historyButton.setVisibility(View.VISIBLE);
            AnimationFactory.flipTransition(flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
        } else {
            super.onBackPressed();
        }
    }
}
