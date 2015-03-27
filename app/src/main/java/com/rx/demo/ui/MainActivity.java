package com.rx.demo.ui;

import android.os.Bundle;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.commander.UserCommander;
import com.rx.demo.ui.view.SuggestionsBox;

import javax.inject.Inject;

import butterknife.InjectView;


public class MainActivity extends com.rx.demo.ui.DemoBaseActivity {
    @InjectView(R.id.suggestionBox)
    SuggestionsBox suggestionsBox;

    @Inject
    public UserCommander userCommander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
