package com.rx.demo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.di.ImageSearchModule;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static butterknife.ButterKnife.findById;


public class SearchActivity extends BaseActivity {
    private boolean showingBack;

    @Inject Handler handler;
    private FlipAnimation flipAnimation;
    private FlipAnimation backFlip;
    private View left;
    private View right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.main_view);
        ViewGroup parent = (ViewGroup) getLayoutInflater().inflate(R.layout.history_view, findById(this, R.id.container));
        left = parent.getChildAt(0);
        getLayoutInflater().inflate(R.layout.search_view, findById(this, R.id.container));
        right = parent.getChildAt(1);
        findViewById(R.id.flip).setOnClickListener(v -> {
            flipViews();
        });

    }

    public void flipViews() {
        flipAnimation = new FlipAnimation(right, left);
        backFlip = new FlipAnimation(right,left);
        handler.removeCallbacks(rotate);
        handler.postDelayed(rotate, 100);
    }

    private Runnable rotate = new Runnable() {

        @Override
        public void run() {
            if (!showingBack) {
                left.startAnimation(flipAnimation);
                right.startAnimation(flipAnimation);
                findViewById(R.id.flip).setVisibility(View.INVISIBLE);
                showingBack = true;
            } else {
                findViewById(R.id.flip).setVisibility(View.VISIBLE);
                showingBack = false;
                backFlip.reverse();
                left.startAnimation(backFlip);
                right.startAnimation(backFlip);
            }
        }
    };

    protected List<Object> getModules() {
        return Arrays.asList(new ImageSearchModule(this));
    }

    @Override
    public void onBackPressed() {
        if(showingBack)
        {
            flipViews();
        }
        else
        {
            super.onBackPressed();

        }
    }
}
