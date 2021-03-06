/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rx.demo.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.rx.demo.DemoApplication;
import com.rx.demo.util.SubscriptionManager;

import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Base activity which sets up a per-activity object graph and performs injection.
 */
public abstract class BaseActivity extends Activity {
    @Inject
    SubscriptionManager subscriptionManager;
    ObjectGraph activityGraph;

    /**
     * creates a activity scoped object graph which adds to global graph
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        activityGraph = ((DemoApplication)getApplication()).getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
    }

    protected abstract List<Object> getModules();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        subscriptionManager.unsubscribeAll();
        super.onDestroy();
    }



    public void inject(View view) {
        activityGraph.inject(view);
    }
}
