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
package com.rx.demo.module;

import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.rx.demo.model.Result;
import com.rx.demo.ui.activity.DemoBaseActivity;
import com.rx.demo.ui.activity.MainActivity;
import com.rx.demo.ui.view.ImageSearchView;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module(
        injects = {MainActivity.class, ImageSearchView.class},
        addsTo = AndroidModule.class,
        library = true)
public class ImageSearchModule {
    private final DemoBaseActivity activity;

    public ImageSearchModule(DemoBaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    DemoBaseActivity provideActivity() {
        return activity;
    }

    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater() {
        return activity.getLayoutInflater();
    }

    @Provides
    @Singleton
    Handler provideHandler() {
        return new Handler();
    }

    @Provides
    @Singleton
    PublishSubject<Object> provideBus() {
        return PublishSubject.create();
    }

    @Provides
    @RowContainer
    LinearLayout provideImageContainer() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(params);
        return row;
    }

    @Provides
    @Singleton
    Queue<Result> providesImageQueue()
    {
       return new ConcurrentLinkedQueue<>();
    }

}
