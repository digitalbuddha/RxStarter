package com.rx.demo.ui.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by paddy on 2/22/15.
 */
@Singleton
public class AnimationHelper {
    @Inject
    public AnimationHelper() {
    }

    public void showCard(View card) {

        AnimatorSet reset = new AnimatorSet();
        reset.playTogether(
                ObjectAnimator.ofFloat(card, "translationY", 0, -1000),
                ObjectAnimator.ofFloat(card, "alpha", 0)
        );
        reset.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {

                card.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {

                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(card, "translationY", -card.getHeight(), 0),
                        ObjectAnimator.ofFloat(card, "alpha", 0, 1)
                );
                set.setInterpolator(new AccelerateInterpolator(1.1f));
                set.setDuration(600).start();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {

            }
        });
        reset.setDuration(0).start();
    }
}
