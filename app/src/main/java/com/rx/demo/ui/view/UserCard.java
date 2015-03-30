package com.rx.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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
}
