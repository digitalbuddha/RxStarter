package com.digitalbuddha.rx.cache;


import rx.Observable;

/**
 * Created by swapnilp on 10/30/14.
 */
public interface ObservableLoader<R,S> {

    Observable<S> load(R r);
}
