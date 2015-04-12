package com.rx.demo.di.annotation;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Nakhimovich on 4/10/15.
 */
@Qualifier
@Retention(RUNTIME)
public @interface HistoryViewBus {
}
