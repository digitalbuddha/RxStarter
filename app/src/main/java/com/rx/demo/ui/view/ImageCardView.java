package com.rx.demo.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.model.Result;
import com.rx.demo.ui.activity.BaseActivity;

import java.util.Queue;

import javax.inject.Inject;

public class ImageCardView extends LinearLayout {
    @Inject
    Queue<Result> que;

    public ImageCardView(Context context) {
        this(context, null);
    }

    public ImageCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ((BaseActivity) context).inject(this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        bindImageData();

    }

    public void bindImageData() {
        Result image = que.remove();
        if (image != null) {
            Uri uri = Uri.parse(image.getUnescapedUrl());
            SquareDraweeView draweeView = (SquareDraweeView) findViewById(R.id.avatar);
            draweeView.setImageURI(uri);
        }

    }
}
