package com.digitalbuddha.rx.util

import groovy.transform.CompileStatic;
import rx.Observable

import static rx.Observable.*;

@CompileStatic
public class ObservableUtils {
    public static <R> Observable<R> observabler(R request) {
        just((request))
    }
}
