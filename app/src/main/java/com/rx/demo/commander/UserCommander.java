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
public class UserCommander extends Commander<Object, ArrayList<User>> {
    @Inject
    Github api;

    public UserCommander() {
        super();
    }

    @Override
    public ArrayList<User> load(Object request) throws Exception {
        return api.users();
    }
}
