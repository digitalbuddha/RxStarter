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

    private Observable<User> randomUserObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userObservable = api.users()
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        randomUserObservable = userObservable.map(users -> {
            Collections.shuffle(users);
            return users.get(0);
        });

        randomUserObservable.subscribe(this::updateFirstUser);
        randomUserObservable.subscribe(this::updateSecondUser);
        randomUserObservable.subscribe(this::updateThirdUser);

        //next we want to set up reload of each use when the corresponding X is clicked
        clicks(view(R.id.close1))
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUserObservable)
                .retry(1)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateFirstUser);

        clicks(view(R.id.close2))
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> randomUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateSecondUser);

        clicks(view(R.id.close3))
                .flatMap(onClickEvent -> randomUserObservable)
                        //slide on map
                .flatMap(onClickEvent -> randomUserObservable)
                .doOnError(throwable -> {
                    //do something
                })
                .subscribe(this::updateThirdUser);

        Observable<OnClickEvent> refreshClick = clicks(findViewById(R.id.btnRefresh));
        refreshClick.flatMap(onClickEvent -> randomUserObservable).subscribe(this::updateFirstUser);
        refreshClick.flatMap(onClickEvent -> randomUserObservable).subscribe(this::updateSecondUser);
        refreshClick.flatMap(onClickEvent -> randomUserObservable).subscribe(this::updateThirdUser);
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
