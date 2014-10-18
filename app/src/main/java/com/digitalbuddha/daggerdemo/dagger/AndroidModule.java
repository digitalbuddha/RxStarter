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
package com.digitalbuddha.daggerdemo.dagger;

import android.content.Context;
import android.location.LocationManager;

import com.digitalbuddha.daggerdemo.DemoApplication;
import com.digitalbuddha.daggerdemo.rest.Github;
import com.path.android.jobqueue.JobManager;

import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module(library = true)
public class AndroidModule {
    private final DemoApplication application;
    private RestAdapter restAdapter;

    public AndroidModule(DemoApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @ForApplication} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }


    @Provides
    @Singleton
    RestAdapter provideRestAdapter() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();
        return restAdapter;
    }

    @Provides
    @Singleton
    Github provideGithub(RestAdapter restAdapter) {
        return restAdapter.create(Github.class);
    }

    @Provides
    @Singleton
    JobManager provideJobManager() {
        return new JobManager(provideApplicationContext());
    }


    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }
}
