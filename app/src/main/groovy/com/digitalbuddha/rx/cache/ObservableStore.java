package com.digitalbuddha.rx.cache;

import java.io.Serializable;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.digitalbuddha.rx.util.ObservableUtils.observabler;

/**
 * Created by MikeN on 1/25/15.
 */
public abstract class ObservableStore<T extends Serializable, V> extends AbstractStore<T, V> implements ObservableStoreInterface<T, V> {
    private static final String TAG = "ObservableStore";

    public ObservableStore() {
        setObjectName(this.toString());
        initialize();
    }

    public Observable<BaseModel<V, T>> all(final V request) {
        return fresh(request).startWith(getCachedValue(request));
    }

    public Observable<BaseModel<V, T>> cached(final V request) {
        return observabler(getCachedValue(request));
    }


    public Observable<BaseModel<V, T>> fresh(final V request) {
        if (!isInFlightNetwork(request)) {
            return response(request);
        } else {
            return inFlightResponse(request);
        }
    }

    public Observable<BaseModel<V, T>> get(final V request) {
        return observabler(getCachedValue(request)).flatMap(new Func1<BaseModel<V, T>, Observable<BaseModel<V, T>>>() {
            @Override
            public Observable<BaseModel<V, T>> call(BaseModel<V, T> cachedValue) {
                return cachedValue == null ? fresh(request) : observabler(cachedValue);
            }
        }).subscribeOn(Schedulers.io());
    }


}
