package com.rx.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.model.Result;
import com.squareup.picasso.Picasso;

public class UserCard extends RelativeLayout {


    public UserCard(Context context) {
        this(context, null);
    }

    public UserCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void bindUserData(Result image, UserCard userCard) {
        Picasso.with(getContext())
                .load(image.getUnescapedUrl())
                .fit()
                .into((ImageView) userCard.findViewById(R.id.avatar));
    }
}
