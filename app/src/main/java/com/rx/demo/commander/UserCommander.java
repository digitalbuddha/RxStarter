package com.rx.demo.commander;

import com.rx.demo.model.UserRequest;
import com.rx.demo.model.UserResponse;
import com.rx.demo.rest.Github;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Nakhimovich on 3/25/15.
 */
@Singleton
public class UserCommander extends Commander<UserRequest, UserResponse> {

    @Inject
    Github api;

    public UserCommander() {
        super();
    }

    @Override
    public UserResponse load(UserRequest request) throws Exception {
        UserResponse response = api.users(request.getName());
        return response;
    }
}
