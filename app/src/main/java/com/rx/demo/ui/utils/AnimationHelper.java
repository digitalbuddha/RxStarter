package com.rx.demo.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by paddy on 2/22/15.
 */
public class AnimationHelper {

    public void dismissCard(View card) {

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(card, "translationY", -card.getHeight()),
                ObjectAnimator.ofFloat(card, "alpha", 1, 0)
                );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                AnimatorSet set = new AnimatorSet();
                set.playTogether(ObjectAnimator.ofFloat(card, "translationY", 0));
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                        card.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                set.setDuration(0).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setInterpolator(new AccelerateInterpolator(1.1f));
        set.setDuration(600).start();
    }

    public void showCard(View card) {

        AnimatorSet reset = new AnimatorSet();
        reset.playTogether(
                ObjectAnimator.ofFloat(card, "translationY", 0, -1000),
                ObjectAnimator.ofFloat(card, "alpha", 0)
                );
        reset.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                card.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(card, "translationY", -card.getHeight(), 0),
                        ObjectAnimator.ofFloat(card, "alpha", 0, 1)
                        );
                set.setInterpolator(new AccelerateInterpolator(1.1f));
                set.setDuration(600).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        reset.setDuration(0).start();
    }
}
