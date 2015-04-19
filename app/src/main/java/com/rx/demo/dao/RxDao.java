package com.rx.demo.dao;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

import static rx.Observable.create;
import static rx.Observable.just;


//T = request type, V = response type
public abstract class RxDao<T, V> {
    private final Map<String, V> cachedResponses;
    protected Map<String, Observable<V>> inFlightRequests = new HashMap<>();
    protected PublishSubject<V> onNextObservable = PublishSubject.create();
    protected PublishSubject<V> updateObservable = PublishSubject.create();
    private Gson gson = new Gson();

    public RxDao() {
        cachedResponses = Collections.synchronizedMap(new HashMap<>());
        inFlightRequests = Collections.synchronizedMap(new HashMap<>());
    }

    public Observable<V> fresh(final T request) {
        return isInFlightNetwork(request) ? inFlightResponse(request) : response(request);
    }

    public Observable<V> cached(final T request) {
        return just(getCachedValue(request));
    }

    public Observable<V> all(final T request) {
        return fresh(request).startWith(cached(request));
    }

    public Observable<V> get(final T request) {
        Log.e(this.getClass().getName(), "rx get");
        V cachedValue = getCachedValue(request);

        Observable<V> result = cachedValue == null ? fresh(request) : cached(request);
        return result;
    }

    boolean isInFlightNetwork(T request) {
        return request != null && inFlightRequests.containsKey(json(request));
    }

    public abstract V load(T request) throws Exception;

    private void loadResponse(final T request) throws Exception {
        V result = load(request);
        cachedResponses.put(json(request), result);
    }

    private String json(T request) {
        return gson.toJson(request);
    }

    protected Observable<V> response(final T request) {
        final Observable<V> response = create(subscriber -> {
            try {
                subscriber.onStart();
                loadResponse(request);
                subscriber.onNext(getCachedValue(request));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
        return registerResponse(request, response);
    }

    Observable<V> inFlightResponse(T request) {
        return inFlightRequests.get(json(request));
    }

    private V getCachedValue(T request) {
        V v = cachedResponses.get(json(request));
        if (v != null) {
            Log.e(this.getClass().getName(), "rx cache get");
            onNextObservable.onNext(v);
        }
        //TODO: make a defensive copy of the cached value in case V is mutable

        return v;

    }

    protected Observable<V> registerResponse(final T request, final Observable<V> response) {
        return response
                .doOnSubscribe(() -> inFlightRequests.put(json(request), response))
                .doOnCompleted(() -> inFlightRequests.remove(json(request))).doOnNext(updateObservable::onNext);
    }


    public Observable<V> onNextObservable() {
        return onNextObservable.asObservable();
    }

    public Observable<V> onUpdateObservable() {
        return updateObservable.asObservable();
    }
}


