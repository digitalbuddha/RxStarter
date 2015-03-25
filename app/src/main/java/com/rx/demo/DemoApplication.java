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
package com.rx.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.rx.demo.dagger.AndroidModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class DemoApplication extends Application {

    public static final String DB_NAME = "SAMPLE_DB";

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }


    ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationGraph = ObjectGraph.create(getModules().toArray());
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) this.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;


    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new AndroidModule(this));
    }
}
