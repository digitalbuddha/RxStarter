package com.rx.demo.ui.activity;

import android.os.Bundle;

import com.digitalbuddha.rx.demo.R;

import static butterknife.ButterKnife.findById;


public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.search_view, findById(this, android.R.id.content));
    }
}
