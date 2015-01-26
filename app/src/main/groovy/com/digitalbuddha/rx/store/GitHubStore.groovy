package com.digitalbuddha.rx.store

import com.digitalbuddha.rx.cache.ObservableStore
import com.digitalbuddha.rx.model.Repo
import com.digitalbuddha.rx.rest.Github
import groovy.transform.CompileStatic

import javax.inject.Inject
@CompileStatic
public class GitHubStore extends ObservableStore<ArrayList<Repo>, String> {
    @Inject
    protected Github github
    @Inject
    GitHubStore() {
    }


    @Override
    public ArrayList<Repo> load(String user) throws Exception {
        github.repos(user)
    }
}
