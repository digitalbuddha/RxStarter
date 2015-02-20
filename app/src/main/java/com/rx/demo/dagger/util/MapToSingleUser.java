package com.rx.demo.dagger.util;

import com.rx.demo.dagger.model.User;

import java.util.ArrayList;

import rx.functions.Func1;

import static com.rx.demo.dagger.ui.MainActivity.getRandomIndex;


/**
 * Created by Nakhimovich on 2/19/15.
 */
public class MapToSingleUser implements Func1<ArrayList<User>, User> {
    @Override
    public User call(ArrayList<User> users) {
        return users.get(getRandomIndex(users.size()));
    }
}
