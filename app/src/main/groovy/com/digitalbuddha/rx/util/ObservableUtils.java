package com.digitalbuddha.rx.util;

import rx.Observable;

/**
 * Created by MikeN on 1/24/15.
 */
public class ObservableUtils {
    public static <R> Observable<R> observabler(R request) {
        return Observable
                .just((request));

    }
}
