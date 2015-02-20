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
import java.util.Random;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.events.OnClickEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import static rx.android.observables.ViewObservable.clicks;


public class MainActivity extends DemoBaseActivity {

    @Inject
    public Github api;
    @Activity
    @Inject
    Context context;

    private Observer<User> firstUserObserver;
    private Observer<User> secondUserObserver;
    private Observer<User> thirdUserObserver;
    private Func1<OnClickEvent, Observable<ArrayList<User>>> clickToResponse;
    private Func1<ArrayList<User>, User> randomUser;
    private Observable<ArrayList<User>> refreshAllObservable;
    private Observable<ArrayList<User>> usersObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions_layout);
        ButterKnife.inject(this);
        //Slides on consuming observables
        createObservers();

        createUserObservable();

        setupFunctions();

        setupClickStreams();

        subscribeWithAllObservers(refreshAllObservable);

        subscribeWithAllObservers(usersObservable);
    }

    private void createUserObservable() {
        //slide on creating observables, then show how retrofit can do it for you
        usersObservable = api.users().cache()  //slides on cache and other aggregates
                .observeOn(AndroidSchedulers.mainThread())  //slide on schedulers/threading
                .subscribeOn(Schedulers.io());  //metion immutibility
    }


    private void setupFunctions() {
        //slide on functions, mapping specifically
        clickToResponse = onClickEvent -> usersObservable;
        randomUser = users -> users.get(getRandomIndex(users.size()));
    }


    private void setupClickStreams() {
        //slide everything is a stream including click events show double click buffering as well
        refreshAllObservable = clicks(findViewById(R.id.btnRefresh))
                .flatMap(clickToResponse);  //slide difference map vs flatmap

        clicks(view(R.id.close1))
                .flatMap(clickToResponse)
                .map(randomUser)
                .subscribe(firstUserObserver);

        clicks(view(R.id.close2))
                .flatMap(clickToResponse)
                .map(randomUser)
                .subscribe(secondUserObserver);

        clicks(view(R.id.close3))
                .flatMap(clickToResponse)
                .map(randomUser)
                .subscribe(thirdUserObserver);
    }

    private void subscribeWithAllObservers(Observable<ArrayList<User>> observable) {
        ConnectableObservable<ArrayList<User>> connectableObservable = observable.publish();
        //Slide on doOnNext, onError etc.
        connectableObservable.doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //lets handle all errors the same way by displaying some message
            }
        });

        //get a single random user from the response and then have each of
        //the three screen elements subscribe to it thus updating the screens with new data
        connectableObservable.map(randomUser).subscribe(firstUserObserver);
        connectableObservable.map(randomUser).subscribe(secondUserObserver);
        connectableObservable.map(randomUser).subscribe(thirdUserObserver);
        connectableObservable.connect();
    }

    private void createObservers() {


        firstUserObserver = new Observer<User>() {
           //slide
            @Override
            public void onCompleted() {

            }
            //slide
            @Override
            public void onError(Throwable e) {

            }
            //slide
            @Override
            public void onNext(User user) {
                ((TextView) view(R.id.name1)).setText(user.login);
                Picasso.with(context)
                        .load(user.avatar_url)
                        .into((ImageView) view(R.id.avatar1));

            }
        };

        secondUserObserver = new Observer<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                ((TextView) view(R.id.name2)).setText(user.login);
                Picasso.with(context)
                        .load(user.avatar_url)
                        .into((ImageView) view(R.id.avatar2));
            }
        };

        thirdUserObserver = new Observer<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                ((TextView) view(R.id.name3)).setText(user.login);
                Picasso.with(context)
                        .load(user.avatar_url)
                        .into((ImageView) view(R.id.avatar3));
            }
        };
    }

    public static int getRandomIndex(int size) {
        return new Random().nextInt(size);
    }
}
