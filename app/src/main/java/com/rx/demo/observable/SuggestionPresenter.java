package com.rx.demo.observable;

import com.rx.demo.commander.UserCommander;
import com.rx.demo.model.User;
import com.rx.demo.model.UserRequest;
import com.rx.demo.transformer.WorkOnIoResultOnMain;
import com.rx.demo.ui.activity.DemoBaseActivity;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
@Singleton
public class SuggestionPresenter {
    @Inject
    public UserCommander userCommander;
    @Inject
    DemoBaseActivity activity;

    AtomicInteger index = new AtomicInteger();

    public Observable<User> nextUser(String s) {
        return userCommander
                .get(new UserRequest(s))
                .map(userResponse -> userResponse.items.get(index.incrementAndGet()))
                .compose(new WorkOnIoResultOnMain())
                .doOnError(activity::displayError);
    }

    public Observable<User> getNextThreeUsers(String s) {
        return nextUser(s)
                .repeat(3);
    }

    public Observable<User> getFirstThreeUsers(String s) {
        index.set(0);
        return getNextThreeUsers(s);

    }

    public AtomicInteger getIndex() {
        return index;
    }

    public void setIndex(AtomicInteger index) {
        this.index = index;
    }
}