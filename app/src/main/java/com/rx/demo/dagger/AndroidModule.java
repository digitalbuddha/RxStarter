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

import android.content.Context;

import com.rx.demo.DemoApplication;
import com.rx.demo.commander.UserCommander;
import com.rx.demo.rest.Github;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(library = true,
        injects = {UserCommander.class
        })
public class AndroidModule {
    DemoApplication application;
    RestAdapter restAdapter;

    public AndroidModule(DemoApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
         return application;
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter() {
       return restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();
    }

    @Provides
    @Singleton
    Github provideGithub(RestAdapter restAdapter) {
       return  restAdapter.create(Github.class);
    }

}
