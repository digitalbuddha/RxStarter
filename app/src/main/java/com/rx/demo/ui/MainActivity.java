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


    private Observable<User> randomUserObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        randomUserObservable = api.users()
                .cache()
                .map(users -> {
                    Collections.shuffle(users);

                    return users.get(0);
                })
                .filter(user -> !user.login.contains("steve"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        //initial load of 3  random users
        randomUserObservable.subscribe(this::updateFirstUser);
        randomUserObservable.subscribe(this::updateSecondUser);
        randomUserObservable.subscribe(this::updateThirdUser);

        clicks(view(R.id.close1))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateFirstUser);

        clicks(view(R.id.close2))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateSecondUser);

        clicks(view(R.id.close3))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUserObservable)
                .doOnError(throwable -> {
                    //do something
                })
                .subscribe(this::updateThirdUser);

        Observable<OnClickEvent> refreshClick = clicks(findViewById(R.id.btnRefresh));
        Observable<User> userObservable = refreshClick.flatMap(onClickEvent -> randomUserObservable);
        userObservable.subscribe(this::updateFirstUser);
        userObservable.subscribe(this::updateSecondUser);
        userObservable.subscribe(this::updateThirdUser);
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
