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
package com.rx.demo.dagger;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import groovy.transform.CompileStatic;

/**
 * Base activity which sets up a per-activity object graph and performs injection.
 */
@CompileStatic
public abstract class DemoBaseActivity extends FragmentActivity {
    ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        activityGraph = ((DemoApplication)getApplication()).getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Arrays.<Object> asList(new ActivityModule(this));
    }

    protected View view(int id) {
       return findViewById(id);
    }
}
