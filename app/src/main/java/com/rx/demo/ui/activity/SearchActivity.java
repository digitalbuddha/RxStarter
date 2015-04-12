package com.rx.demo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ViewFlipper;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.di.ImageSearchModule;
import com.rx.demo.ui.animation.AnimationFactory;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;


public class SearchActivity extends BaseActivity {


    public static final int SEARCH_VIEW_POSITION = 0;
    @InjectView(R.id.container)
    ViewFlipper flipper;
    @InjectView(R.id.history)
    View historyButton;

    /**
     * inflate search and history view and add the to container
     * set click listener for history button
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_layout);
        ButterKnife.inject(this);
        flipper = findById(this, R.id.container);
        getLayoutInflater().inflate(R.layout.search_view, flipper);
        getLayoutInflater().inflate(R.layout.history_view, flipper);
    }

    @OnClick(R.id.history)
    public void historyButtonClicked() {
        flipViews();

    }

    protected List<Object> getModules() {
        return Arrays.asList(new ImageSearchModule(this));
    }

    public void flipViews() {
        if (flipper.getDisplayedChild() == SEARCH_VIEW_POSITION) {
            historyButton.setVisibility(View.INVISIBLE);
            AnimationFactory.flipTransition(flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);

        } else {
            historyButton.setVisibility(View.VISIBLE);
            AnimationFactory.flipTransition(flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
        }
    }

    @Override
    public void onBackPressed() {
        if (flipper.getDisplayedChild() != SEARCH_VIEW_POSITION) {
            historyButton.setVisibility(View.VISIBLE);
            flipper.showNext();
        } else {
            super.onBackPressed();
        }
    }
}
