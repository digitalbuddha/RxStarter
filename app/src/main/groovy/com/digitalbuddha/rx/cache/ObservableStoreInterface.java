package com.digitalbuddha.rx.cache;


import java.io.Serializable;

import rx.Observable;

/**
 * Created by MikeN on 1/25/15.
 */
public interface ObservableStoreInterface<T extends Serializable, V> {
    Observable<BaseModel<V, T>> allObserverable(V request);

    Observable<BaseModel<V, T>> cachedObservable(V request);

    Observable<BaseModel<V, T>> networkObservable(V request);

    Observable<BaseModel<V, T>> get(V request);
}
