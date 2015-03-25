package com.rx.demo.commander;

import com.rx.demo.model.User;
import com.rx.demo.rest.Github;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Nakhimovich on 3/25/15.
 */
@Singleton
public class UserCommander extends Commander<ArrayList<User>> {
    private final String accessToken="6b060e39a5cf06b980ec4f1d2042b525026c35d2";

    @Inject
    Github api;

    public UserCommander() {
        super();
    }

    @Override
    public ArrayList<User> load(Object request) throws Exception {
        return api.users("token " + accessToken);
    }
}
