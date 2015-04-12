package com.rx.demo.ui.activity;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Nakhimovich on 4/11/15.
 */
public class FlipAnimation extends Animation {
    private Camera camera;

    private View fromView;
    private View toView;

    private float centerX;
    private float centerY;

    private boolean forward = true;


    /**
     * Creates a 3D flip animation between two views.
     *
     * @param fromView First view in the transition.
     * @param toView   Second view in the transition.
     */
    public FlipAnimation(View fromView, View toView) {
        this.fromView = fromView;
        this.toView = toView;

        setDuration(500);
        setFillAfter(false);
        setInterpolator(new AccelerateDecelerateInterpolator());
        //setInterpolator(new LinearInterpolator());
    }

    public void reverse() {

        if (forward) {
            View switchView = toView;
            toView = fromView;
            fromView = switchView;
        }
        forward = false;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        centerX = width / 2;
        centerY = height / 2;
        camera = new Camera();
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        // Angle around the y-axis of the rotation at the given time
        // calculated both in radians and degrees.
        final double radians = Math.PI * interpolatedTime;
        float degrees = (float) (180.0 * radians / Math.PI);

        //Scale the view down a bit once the animation begins, so that it fits
        //inside the frame it was given
        if (interpolatedTime <= 0.1f) {
            fromView.setScaleX(1 - interpolatedTime*2);
            fromView.setScaleY(1 - interpolatedTime*2);
            toView.setScaleX(1 - interpolatedTime*2);
            toView.setScaleY(1 - interpolatedTime*2);
        }

        if (interpolatedTime == 0.5f) {
            fromView.setVisibility(View.INVISIBLE);
            toView.setVisibility(View.VISIBLE);

        }

        // Once we reach the midpoint in the animation, we need to hide the
        // source view and show the destination view. We also need to change
        // the angle by 180 degrees so that the destination does not come in
        // flipped around
        if (interpolatedTime >= 0.5f) {
            degrees -= 180.f;
            toView.bringToFront();
            //these two lines are necessary to make it work below kitkat
            ((View) toView.getParent()).requestLayout();
            ((View) toView.getParent()).invalidate();

        }
        //return the view back to its original size
        //assuming scale was 1
        if (interpolatedTime >= 0.95f) {
            fromView.setScaleX(interpolatedTime);
            fromView.setScaleY(interpolatedTime);
            toView.setScaleX(interpolatedTime);
            toView.setScaleY(interpolatedTime);
        }

        if (forward)
            degrees = -degrees; // determines direction of rotation when flip
        // begins

        final Matrix matrix = t.getMatrix();
        camera.save();
        camera.translate(0, 0, Math.abs(degrees) * 2);
        camera.getMatrix(matrix);
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

    }
}
