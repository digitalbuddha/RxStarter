package com.digitalbuddha.rx.cache

import rx.Observable;
import rx.schedulers.Schedulers

import static com.digitalbuddha.rx.util.ObservableUtils.observabler

/**
 * Created by MikeN on 1/25/15.
 */
public
abstract class ObservableStore<T extends Serializable, V> extends AbstractStore<T, V> implements ObservableStoreInterface<T, V> {
    public ObservableStore() {
        objectName = this.toString()
        initialize()
    }

    public all(final V request) {
        fresh(request).startWith(getCachedValue(request))
    }

    public Observable<BaseModel<V, T>> cached(final V request) {
        observabler(getCachedValue(request))
    }


    public Observable<BaseModel<V, T>> fresh(final V request) {
        if (!isInFlightNetwork(request)) {
            response(request).subscribeOn(Schedulers.io())
        } else {
            inFlightResponse(request)
        }
    }

    public Observable<BaseModel<V, T>> get(final V request) {
        observabler(getCachedValue(request))
                .flatMap({
            cachedValue == null ? fresh(request) : observabler(cachedValue);
        }).subscribeOn(Schedulers.io())
    }
}
