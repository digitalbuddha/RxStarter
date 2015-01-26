package com.digitalbuddha.rx.cache;


import com.digitalbuddha.rx.util.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by MikeN on 1/25/15.
 */
public abstract class AbstractStore<T extends Serializable, V> implements ObjectLoader<T, V> {

    private String objectName;
    protected BaseModel<V, T> cachedValue;
    protected PublishSubject<BaseModel<V, T>> endlessSubject;
    protected HashMap<V, Observable<BaseModel<V, T>>> inFlightFetches = new HashMap<>();
    protected HashMap<V, Observable<BaseModel<V, T>>> inFlightNetworkObservables = new HashMap<>();
    public StoreStrategy storeStrategy = StoreStrategy.IN_MEMORY_STORAGE_ONLY;
    public ObjectType objectType = ObjectType.FULL;
    public ObjectLoader<T, V> loader;


    Observable<BaseModel<V, T>> inFlightResponse(V request) {
        return inFlightNetworkObservables.get(request);
    }

    boolean isInFlightNetwork(V request) {
        return request != null && inFlightNetworkObservables.containsKey(request);
    }


    protected void loadResponse(final V request) throws Exception {
        long updateTime = System.currentTimeMillis();
        T result = load(request);
        if (result != null) {
            if (cachedValue == null) {
                cachedValue = new BaseModel<>();
            }
            cachedValue.lastUpdated = updateTime;
            cachedValue.payload = result;
            cachedValue.resultCode = ResultCode.SUCCESS;
        }

    }

    public HashMap<V, Observable<BaseModel<V, T>>> getInFlightFetches() {
        return inFlightFetches;
    }

    public String getObjectName() {
        return objectName;
    }

    public HashMap<V, Observable<BaseModel<V, T>>> getInFlightNetworkObservables() {
        return inFlightNetworkObservables;
    }

    public AbstractStore<T, V> setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }


    public AbstractStore<T, V> setObjectType(ObjectType objectType) {

        this.objectType = objectType;
        return this;
    }


    public AbstractStore<T, V> setStoreStrategy(StoreStrategy storeStrategy) {

        this.storeStrategy = storeStrategy;
        return this;
    }


    protected Observable<BaseModel<V, T>> response(final V request) {
        final Observable<BaseModel<V, T>> response = Observable.create(new Observable.OnSubscribe<BaseModel<V, T>>() {
            @Override
            public void call(Subscriber<? super BaseModel<V, T>> subscriber) {
                try {
                    subscriber.onStart();
                    loadResponse(request);
                    subscriber.onNext(getCachedValue(request));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        });
        return registerResponse(request, response);
    }

    protected Observable<BaseModel<V, T>> registerResponse(final V request, final Observable<BaseModel<V, T>> response) {
        return response.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                inFlightNetworkObservables.put(request, response);

            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                inFlightNetworkObservables.remove(request);
            }
        }).doOnNext(new Action1<BaseModel<V, T>>() {
            @Override
            public void call(BaseModel<V, T> vtBaseModel) {
                endlessSubject.onNext(vtBaseModel);
            }
        });
    }

    private Observable<BaseModel<V, T>> inFlightAll(V request) {
        return inFlightFetches.get(request);
    }

    protected BaseModel<V, T> getCachedValue(V request) {

        if (objectType == ObjectType.FULL) {
            return SerializationUtils.clone(cachedValue);
        } else if (objectType == ObjectType.PARTIAL) {
            if (cachedValue == null) {
                return null;
            } else {
                return SerializationUtils.clone(cachedValue.partialPayload.get(request.hashCode()));
            }
        } else if (objectType == ObjectType.MIXED_AGGREGATE) {
            if (cachedValue == null || !cachedValue.partialPayload.containsKey(request.hashCode())) {
                return null;
            } else {
                return SerializationUtils.clone(cachedValue);
            }
        }
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public PublishSubject getEndlessSubject() {

        return endlessSubject;
    }

    public AbstractStore<T, V> initialize() {
        endlessSubject = PublishSubject.create();
        return this;
    }

    public void updateCache(final V request) throws Exception {
        response(request).subscribe();
    }


}
