package com.digitalbuddha.rx.cache
/**
 * Created by MikeN on 1/25/15.
 */
public interface ObservableStoreInterface<T extends Serializable, V> {
    def all(V request);

    def cached(V request);

    def fresh(V request);

    def get(V request);
}
