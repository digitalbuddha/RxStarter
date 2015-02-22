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
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends DemoBaseActivity {
    @Inject
    public Github api;
    @Activity
    @Inject
    Context context;
    int index;

    private Observable<User> nextUserObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Action1<List<User>> updateAll = users -> {
            updateFirstUser(users.get(index++));
            updateSecondUser(users.get(index++));
            updateThirdUser(users.get(index++));
        };

        Observable<ArrayList<User>> usersObservable = api.users()
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        nextUserObservable = usersObservable
                .map(users -> users.get(index++));

        usersObservable
                .subscribe(updateAll);

        ViewObservable.clicks(findViewById(R.id.btnRefresh))
                .flatMap(onClickEvent -> usersObservable)
                .subscribe(updateAll);

        ViewObservable.clicks(view(R.id.close1))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateFirstUser);

        ViewObservable.clicks(view(R.id.close2))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(this::updateSecondUser);

        ViewObservable.clicks(view(R.id.close3))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUserObservable)
                .doOnError(throwable -> {
                    //do something
                })
                .subscribe(this::updateThirdUser);
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
