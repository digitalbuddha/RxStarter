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

import android.view.LayoutInflater;

import com.rx.demo.ui.activity.DemoBaseActivity;
import com.rx.demo.ui.activity.MainActivity;
import com.rx.demo.ui.view.ImageSearchView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = { MainActivity.class, ImageSearchView.class},
        addsTo = AndroidModule.class,
        library = true)
public class ActivityModule {
    private final DemoBaseActivity activity;

    public ActivityModule(DemoBaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    DemoBaseActivity provideActivity() {
        return activity;
    }

    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater()
    {
        return activity.getLayoutInflater();
    }
}
