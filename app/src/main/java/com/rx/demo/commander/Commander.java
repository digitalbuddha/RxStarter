package com.rx.demo.commander;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.rx.demo.util.ObservableUtils.observabler;


//T = request type, V = response type
public abstract class Commander<T, V> {
    private final Map<String, V> cachedResponses;
    protected Map<String, Observable<V>> inFlightRequests = new HashMap<>();
    protected PublishSubject<V> updateObservable = PublishSubject.create();
    public static List<Commander> commanderList = new ArrayList<>();
    private Gson gson=new Gson();

    public Commander() {
        cachedResponses = Collections.synchronizedMap(new HashMap<>());
        inFlightRequests = Collections.synchronizedMap(new HashMap<>());

        commanderList.add(this);
    }

    public Observable<V> all(final T request) {
        return fresh(request).startWith(getCachedValue(request));
    }

    public Observable<V> cached(final T request) {
        return observabler(getCachedValue(request));
    }


    public Observable<V> fresh(final T request) {
        return isInFlightNetwork(request) ? inFlightResponse(request) : response(request);
    }

    public Observable<V> get(final T request) {
        V cachedValue = getCachedValue(request);
        return cachedValue == null ? fresh(request) : observabler(cachedValue);
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
        final Observable<V> response = Observable.create(subscriber -> {
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


    public Observable<V> getUpdateObservable() {
        return updateObservable;
    }

}


