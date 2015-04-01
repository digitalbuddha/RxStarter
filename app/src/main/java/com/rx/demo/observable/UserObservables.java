package com.rx.demo.observable;

import com.rx.demo.commander.UserCommander;
import com.rx.demo.model.User;
import com.rx.demo.model.UserRequest;
import com.rx.demo.transformer.WorkOnIoResultOnMain;
import com.rx.demo.ui.activity.DemoBaseActivity;

import javax.inject.Inject;

import rx.Observable;

public class UserObservables {
     @Inject
    public UserCommander userCommander;
    @Inject
    DemoBaseActivity activity;

    int index;
    private String searchTerm;


    public Observable<User> nextUser() {
        return userCommander
                .get(new UserRequest(searchTerm))
                .map(userResponse -> userResponse.items.get(index++))
                .compose(new WorkOnIoResultOnMain())
                .doOnError(activity::displayError);
    }

    public Observable<User> next3User() {
        return nextUser()
                .repeat(3);
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        resetIndex();
    }


    public void resetIndex() {
        index = 0;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}