package com.rx.demo.commander;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static rx.Observable.*;


//T = request type, V = response type
public abstract class RxStore<T, V> {
    private final Map<String, V> cachedResponses;
    protected Map<String, Observable<V>> inFlightRequests = new HashMap<>();
    protected PublishSubject<V> updateObservable = PublishSubject.create();
    private Gson gson=new Gson();

    public RxStore() {
        cachedResponses = Collections.synchronizedMap(new HashMap<>());
        inFlightRequests = Collections.synchronizedMap(new HashMap<>());
    }


    public Observable<V> fresh(final T request) {
        return isInFlightNetwork(request) ? inFlightResponse(request) : response(request);
    }

    public Observable<V> get(final T request) {
        Log.e(this.getClass().getName(),"rx get");
        V cachedValue = getCachedValue(request);

        //returning a cached fresh response to prevent operators such as repeat from hitting network more than once.
        return cachedValue == null ? fresh(request).cache() : just(cachedValue);
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

    protected void updateCache(T request)
    {
        fresh(request).subscribeOn(Schedulers.io()).subscribe();
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
        return cachedResponses.get(json(request));
    }

    protected Observable<V> registerResponse(final T request, final Observable<V> response) {
        return response
                .doOnSubscribe(() -> inFlightRequests.put(json(request), response))
                .doOnCompleted(() -> inFlightRequests.remove(json(request)))
                .doOnNext(updateObservable::onNext);
    }


    public Observable<V> onNextObservable() {
        return updateObservable;
    }

    public ArrayList<V> getCachedResponses() {
        return new ArrayList<V>(cachedResponses.values());
    }
}


