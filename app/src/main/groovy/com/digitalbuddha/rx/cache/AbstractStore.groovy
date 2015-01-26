package com.digitalbuddha.rx.cache
import com.digitalbuddha.rx.util.ObservableUtils
import com.digitalbuddha.rx.util.SerializationUtils
import rx.subjects.PublishSubject

import static java.lang.System.currentTimeMillis
/**
 * Created by MikeN on 1/25/15.
 */
public abstract class AbstractStore<T extends Serializable, V> implements ObjectLoader<T, V> {

    def String objectName;
    def BaseModel<V, T> cachedValue;
    def PublishSubject<BaseModel<V, T>> endlessSubject;
    def HashMap<V, rx.Observable<BaseModel<V, T>>> inFlightNetworkObservables = new HashMap<>();
    def ObjectType objectType = ObjectType.FULL;


    protected rx.Observable<BaseModel<V, T>> inFlightResponse(request) {
        return inFlightNetworkObservables.get(request);
    }

    def isInFlightNetwork(request) {
        return request != null && inFlightNetworkObservables.containsKey(request);
    }


    protected loadResponse = { final V request ->
        def updateTime = currentTimeMillis()
        def result = load(request)
        if (result != null) {
            if (cachedValue == null) {
                cachedValue = new BaseModel<>()
            }
            cachedValue.lastUpdated = updateTime
            cachedValue.payload = result
            cachedValue.resultCode = ResultCode.SUCCESS
        }
    }


    protected  rx.Observable<BaseModel<V, T>> response(final V request) {
        rx.Observable<BaseModel<V,T>> response = ObservableUtils.observabler(request)
                .doOnNext({ loadResponse(request) })
                .map({ getCachedValue(request) })
        registerResponse(request, response);
    }

    protected rx.Observable<BaseModel<V, T>> registerResponse(final V request, final rx.Observable<BaseModel<V, T>> response) {
        response.doOnSubscribe({ inFlightNetworkObservables.put(request, response) })
                .doOnCompleted({ inFlightNetworkObservables.remove(request) })
                .doOnNext({ endlessSubject.onNext(it) });
    }



    protected BaseModel<V, T>  getCachedValue(V request) {

        if (objectType == ObjectType.FULL) {
            return SerializationUtils.clone(cachedValue)
        } else if (objectType == ObjectType.PARTIAL) {
            if (cachedValue == null) {
                return null
            } else {
                return SerializationUtils.clone(cachedValue.partialPayload.get(request.hashCode()))
            }
        } else if (objectType == ObjectType.MIXED_AGGREGATE) {
            if (cachedValue == null || !cachedValue.partialPayload.containsKey(request.hashCode())) {
                return null
            } else {
                return SerializationUtils.clone(cachedValue)
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize()
    }


    public  initialize = { ->
        endlessSubject = PublishSubject.create()
        this
    }

    public updateCache = { final V request ->
        response(request).subscribe()
    }


}
