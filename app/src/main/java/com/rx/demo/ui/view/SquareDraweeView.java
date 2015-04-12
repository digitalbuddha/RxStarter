package com.rx.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

public class SquareDraweeView extends SimpleDraweeView {


    public SquareDraweeView(Context context) {
        this(context, null);
    }

    public SquareDraweeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * width is defined by weight attribute, set height to same to make square images
      * @param widthSpec
     * @param heightSpec
     */
    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        setMeasuredDimension(getMeasuredWidth(),  getMeasuredWidth());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}
