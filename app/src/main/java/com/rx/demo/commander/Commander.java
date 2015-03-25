package com.rx.demo.commander;

import rx.Observable;

public abstract class Commander<V> extends AbstractCommander<Object, V> {

    public static final Object EMPTY_REQUEST = new Object();

    public V load() throws Exception {
        return load(EMPTY_REQUEST);
    }

    public Observable<V> all() {
        return super.all(EMPTY_REQUEST);
    }

    public Observable<V> cached() {
        return super.cached(EMPTY_REQUEST);
    }

    public Observable<V> fresh() {
        return super.fresh(EMPTY_REQUEST);
    }

    public Observable<V> get() {
        return super.get(EMPTY_REQUEST);
    }
}
