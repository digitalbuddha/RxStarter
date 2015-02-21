package com.rx.demo.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.dagger.Activity;
import com.rx.demo.dagger.DemoBaseActivity;
import com.rx.demo.model.User;
import com.rx.demo.rest.Github;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.events.OnClickEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.android.observables.ViewObservable.clicks;


public class MainActivity extends DemoBaseActivity {
    @Inject
    public Github api;
    @Activity
    @Inject
    Context context;

    private Observable<ArrayList<User>> userObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //as soon as screen loads, we would like to load a random user into each user box
        randomUser().subscribe(this::updateFirstUser);
        randomUser().subscribe(this::updateSecondUser);
        randomUser().subscribe(this::updateThirdUser);

        //next we want to set up reload of each use when the corresponding X is clicked
        clicks(view(R.id.close1))
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUser())
                .retry(1)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateFirstUser);

        clicks(view(R.id.close2))
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUser())
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateSecondUser);

        clicks(view(R.id.close3))
                .flatMap(onClickEvent -> getUsersObservable())
                        //slide on map
                .flatMap(onClickEvent -> randomUser())
                .doOnError(throwable -> {
                    //do something
                })
                .subscribe(this::updateThirdUser);

        Observable<OnClickEvent> refreshClick = clicks(findViewById(R.id.btnRefresh));
        refreshClick.flatMap(onClickEvent -> randomUser()).subscribe(this::updateFirstUser);
        refreshClick.flatMap(onClickEvent -> randomUser()).subscribe(this::updateSecondUser);
        refreshClick.flatMap(onClickEvent -> randomUser()).subscribe(this::updateThirdUser);
    }







    private Observable<ArrayList<User>> getUsersObservable() {
        //slide on creating observables, then show how retrofit can do it for you
        if (userObservable==null) {
            userObservable = api.users()
                    .cache()  //slides on cache and blocking
                    .observeOn(AndroidSchedulers.mainThread())//slide on schedulers/threading
                    .subscribeOn(Schedulers.io());
        }
        return userObservable;
    }

    private Observable<User> randomUser() {
        return getUsersObservable()
                //slide on Observable.from
                .map(users -> {
                    Collections.shuffle(users);
                    return users.get(0);
                });

    }

    private void updateThirdUser(User user) {
        bindData(R.id.name3, R.id.avatar3, user);
    }

    private void updateSecondUser(User user) {
        bindData(R.id.name2, R.id.avatar2, user);
    }

    private void updateFirstUser(User user) {
        bindData(R.id.name1, R.id.avatar1, user);
    }

    private void bindData(int textviewID, int imageViewId, User user) {
        ((TextView) view(textviewID)).setText(user.login);
        Picasso.with(context).load(user.avatar_url).into((ImageView) view(imageViewId));
    }
}
