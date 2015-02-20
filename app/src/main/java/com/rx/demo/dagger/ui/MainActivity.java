package com.rx.demo.dagger.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.dagger.dagger.Activity;
import com.rx.demo.dagger.dagger.DemoBaseActivity;
import com.rx.demo.dagger.model.User;
import com.rx.demo.dagger.rest.Github;
import com.rx.demo.dagger.util.MapToSingleUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.events.OnClickEvent;
import rx.android.schedulers.AndroidSchedulers;
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
    private Observable<ArrayList<User>> refreshAllObservable;
    private Observable<ArrayList<User>> usersObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions_layout);
        ButterKnife.inject(this);
        //first we set up observers for each user we want to display,
        //this is where view binding is done
        initObservers();
        //next we setup the refresh observable and the response observable that it is based on
        setupObservables();
        //we subscribe with the observers to both the refresh button and the initial load
        subscribeWithAllObservers(refreshAllObservable);
        subscribeWithAllObservers(usersObservable);
        //finally we set up subscriptions for the cancel buttons
        setupCloseClicks();
    }



    private void setupObservables() {
        usersObservable = api.users().cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        initClickToResponse();

        refreshAllObservable = clicks(findViewById(R.id.btnRefresh))
                                    .flatMap(clickToResponse);
    }

    private void initClickToResponse() {
        clickToResponse = new Func1<OnClickEvent, Observable<ArrayList<User>>>() {
            @Override
            public Observable<ArrayList<User>> call(OnClickEvent onClickEvent) {
                return usersObservable;
            }
        };
    }

    private void setupCloseClicks() {
        clicks(view(R.id.close1))
                .flatMap(clickToResponse)
                .map(new MapToSingleUser())
                .subscribe(firstUserObserver);

        clicks(view(R.id.close2))
                .flatMap(clickToResponse)
                .map(new MapToSingleUser())
                .subscribe(secondUserObserver);

        clicks(view(R.id.close3))
                .flatMap(clickToResponse)
                .map(new MapToSingleUser())
                .subscribe(thirdUserObserver);
    }

    private void subscribeWithAllObservers(Observable<ArrayList<User>> observable) {
        ConnectableObservable<ArrayList<User>> connectableObservable = observable.publish();
        //get a single random user from the response and then have each of
        //the three screen elements subscribe to it thus updating the screens with new data
        connectableObservable.map(new MapToSingleUser()).subscribe(firstUserObserver);
        connectableObservable.map(new MapToSingleUser()).subscribe(secondUserObserver);
        connectableObservable.map(new MapToSingleUser()).subscribe(thirdUserObserver);
        connectableObservable.connect();
    }

    private void initObservers() {
        firstUserObserver = new Observer<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                ((TextView)view(R.id.name1)).setText(user.login);
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
                ((TextView)view(R.id.name2)).setText(user.login);
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
                ((TextView)view(R.id.name3)).setText(user.login);
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
