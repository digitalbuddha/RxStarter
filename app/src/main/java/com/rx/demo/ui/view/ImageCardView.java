package com.rx.demo.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.model.Result;

public class ImageCardView extends LinearLayout {


    public ImageCardView(Context context) {
        this(context, null);
    }

    public ImageCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void bindUserData(Result image) {
        if(image!=null) {
            Uri uri = Uri.parse(image.getUnescapedUrl());
            SquareDraweeView draweeView = (SquareDraweeView) findViewById(R.id.avatar);
            draweeView.setImageURI(uri);
        }

    }
}
