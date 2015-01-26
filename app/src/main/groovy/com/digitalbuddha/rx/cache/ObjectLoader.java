package com.digitalbuddha.rx.cache;


/**
 * Created by swapnilp on 10/30/14.
 */
public interface ObjectLoader<T, R> {
    T load(R request) throws Exception;

}
