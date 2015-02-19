package com.digitalbuddha.rx.store

import com.digitalbuddha.rx.cache.ObservableStore
import com.digitalbuddha.rx.model.User
import com.digitalbuddha.rx.rest.Github
import groovy.transform.CompileStatic

import javax.inject.Inject
@CompileStatic
public class GitHubStore extends ObservableStore<ArrayList<User>, Double> {
    @Inject
    protected Github api

    @Override
    public ArrayList<User> load(Double offset) throws Exception {
        api.repos(offset)
    }
}
