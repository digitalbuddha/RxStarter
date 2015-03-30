package com.rx.demo.transformer;

import com.rx.demo.model.User;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
* Created by Nakhimovich on 3/30/15.
*/
public class WorkOnIoResultOnMain implements Observable.Transformer<User, User> {
    @Override
    public Observable<? extends User> call(Observable<? extends User> observable) {
        return observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
