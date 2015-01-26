package com.digitalbuddha.rx.cache;

/**
 * Created by swapnilp on 12/16/14.
 */
public interface ObjectLoadValidator<T> {
    boolean validateRequest(T request);
}
