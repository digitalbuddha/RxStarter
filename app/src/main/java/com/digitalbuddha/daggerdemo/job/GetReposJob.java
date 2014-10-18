package com.digitalbuddha.daggerdemo.job;

import android.content.Context;

import com.digitalbuddha.daggerdemo.dagger.ForActivity;
import com.digitalbuddha.daggerdemo.models.Repo;
import com.digitalbuddha.daggerdemo.rest.Github;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by MikeN on 9/8/14.
 */
public class GetReposJob extends Job {
    @Inject
    @ForActivity
    Context context;

    @Inject
    public Github api;
    public String userName;
    public List<Repo> repos;

    public GetReposJob() {
        super(new Params(2)
                .requireNetwork()
                .groupBy("Repos"));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        repos = api.repos(userName);
        EventBus.getDefault().post(this);
    }

    //
    @Override
    protected void onCancel() {
        throw new RuntimeException();
    }

    //don't retry on error
    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
