package com.rx.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.model.Result;
import com.squareup.picasso.Picasso;

public class ImageCard extends RelativeLayout {


    public ImageCard(Context context) {
        this(context, null);
    }

    public ImageCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void bindUserData(Result image, ImageCard imageCard) {
           Picasso.with(getContext())
                   .load(image.getUnescapedUrl())
                   .into((ImageView) imageCard.findViewById(R.id.avatar));

    }
}
