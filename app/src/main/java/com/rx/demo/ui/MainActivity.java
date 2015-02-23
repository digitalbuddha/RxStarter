package com.rx.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.dagger.DemoBaseActivity;
import com.rx.demo.model.User;
import com.rx.demo.model.ViewModel;
import com.rx.demo.rest.Github;
import com.rx.demo.ui.utils.AnimationHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

    private int index;

    private Observable<User> nextUser;
    private List<ViewModel> viewIds;
    private Observable<User> next3Users;
    private AnimationHelper animHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewIds();
        animHelper = new AnimationHelper();
        createObservables();

        next3Users.subscribe(this::updateUser);

        subscribeToClicks();
    }


    private void createObservables() {
        Observable<ArrayList<User>> usersObservable = api.users()
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        nextUser = usersObservable
                .map(users -> users.get(index++));

        next3Users = Observable.merge(nextUser, nextUser, nextUser);
    }

    private void subscribeToClicks() {
        ViewObservable.clicks(findViewById(R.id.btnRefresh))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> next3Users)
                .subscribe(this::updateUser);

        ViewObservable.clicks(view(R.id.close1))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUser)
                .onErrorReturn(throwable -> new User())
                .subscribe(user -> updateUserAtPosition(user, 0));
        View card1 = view(viewIds.get(0).getCardId());
        animHelper.dismissCard(card1);

        ViewObservable.clicks(view(R.id.close2))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUser)
                .onErrorReturn(throwable -> new User())
                .subscribe(user -> updateUserAtPosition(user, 1));
        View card2 = view(viewIds.get(1).getCardId());
        animHelper.dismissCard(card2);

        ViewObservable.clicks(view(R.id.close3))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(onClickEvent -> nextUser)
                .doOnError(throwable -> {
                    //do something
                })
                .subscribe(user -> updateUserAtPosition(user, 2));
        View card3 = view(viewIds.get(2).getCardId());
        animHelper.dismissCard(card3);
    }

    private void initViewIds() {
        viewIds = new ArrayList<>();
        viewIds.add(new ViewModel(R.id.card1, R.id.name1, R.id.avatar1));
        viewIds.add(new ViewModel(R.id.card2, R.id.name2, R.id.avatar2));
        viewIds.add(new ViewModel(R.id.card3, R.id.name3, R.id.avatar3));
    }

    private void updateUser(User user) {
        int viewToChange = index % 3;
        updateUserAtPosition(user, viewToChange);
    }

    private void updateUserAtPosition(User user, int viewToChange) {

        ViewModel viewModel = viewIds.get(viewToChange);
        View card = view(viewModel.getCardId());
        animHelper.showCard(card);
        ((TextView) view(viewModel.getNameId())).setText(user.login);
        Picasso.with(context).load(user.avatar_url).into((ImageView) view(viewModel.getAvatarID()));
    }
}
