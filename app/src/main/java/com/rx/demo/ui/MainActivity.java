package com.rx.demo.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.dagger.Activity;
import com.rx.demo.dagger.DemoBaseActivity;
import com.rx.demo.model.User;
import com.rx.demo.model.ViewModel;
import com.rx.demo.rest.Github;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends DemoBaseActivity {
    @Inject
    public Github api;
    @Activity
    @Inject
    Context context;
    int index;

    private Observable<User> nextUserObservable;
    private List<ViewModel> viewIds;
    private Observable<ArrayList<User>> usersObservable;
    private Observable<User> nextThreeObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewIds();

        createObservables();

        nextThreeObservable.subscribe(this::updateUser);

        ViewObservable.clicks(findViewById(R.id.btnRefresh))
                .flatMap(onClickEvent -> nextThreeObservable)
                .subscribe(this::updateUser);

        ViewObservable.clicks(view(R.id.close1))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(user -> updateUserAtPosition(user, 0));

        ViewObservable.clicks(view(R.id.close2))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUserObservable)
                .onErrorReturn(throwable -> new User())
                .subscribe(user -> updateUserAtPosition(user, 1));

        ViewObservable.clicks(view(R.id.close3))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUserObservable)
                .doOnError(throwable -> {
                    //do something
                })
                .subscribe(user -> updateUserAtPosition(user, 2));
    }

    private void createObservables() {
        usersObservable = api.users()
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        nextUserObservable = usersObservable
                .map(users -> users.get(index++));

        nextThreeObservable = usersObservable
                .map(users -> new ArrayList<>(Arrays.asList(
                        users.get(index),
                        users.get(index + 1),
                        users.get(index + 2))))
                .flatMap(Observable::from);
    }

    private void initViewIds() {
        viewIds = new ArrayList<>();
        viewIds.add(new ViewModel(R.id.name1, R.id.avatar1));
        viewIds.add(new ViewModel(R.id.name2, R.id.avatar2));
        viewIds.add(new ViewModel(R.id.name3, R.id.avatar3));
    }

    private void updateUser( User user) {
        int viewToChange = index % 3;
        updateUserAtPosition(user, viewToChange);
        index++;
    }

    private void updateUserAtPosition(User user, int viewToChange) {
        ViewModel viewModel = viewIds.get(viewToChange);
        bindData(viewModel.getNameId(),viewModel.getAvatarID(),user);
    }

    private void bindData(int textviewID, int imageViewId, User user) {
        ((TextView) view(textviewID)).setText(user.login);
        Picasso.with(context).load(user.avatar_url).into((ImageView) view(imageViewId));
    }
}
