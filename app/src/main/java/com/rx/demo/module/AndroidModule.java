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

import com.rx.demo.commander.ImagesStore;
import com.rx.demo.rest.ImagesApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(library = true,
        injects = {ImagesStore.class})
public class AndroidModule {

    public AndroidModule() {}

    @Provides
    @Singleton
    RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint("https://ajax.googleapis.com/ajax/services/search")
                .build();
    }

    @Provides
    @Singleton
    ImagesApi provideImageApi(RestAdapter restAdapter) {
        return restAdapter.create(ImagesApi.class);
    }
}
