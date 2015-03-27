package com.rx.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.commander.UserCommander;
import com.rx.demo.model.User;
import com.rx.demo.model.UserRequest;
import com.rx.demo.model.ViewModel;
import com.rx.demo.ui.utils.AnimationHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import icepick.Icicle;
import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.android.observables.ViewObservable.clicks;


public class MainActivity extends com.rx.demo.ui.DemoBaseActivity {
    @Inject
    public UserCommander userCommander;
    @Inject
    AnimationHelper animHelper;
    @Icicle
    int index;

    private List<ViewModel> viewIds;
    @Icicle
    String searchTerm = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewIds();
        initSearchBox();
        setupClickEvents();
    }

    private void displayNext3Users() {
        nextUserObservable().repeat(3)
                .subscribe(this::displayNextUser);
    }

    private Observable<User> nextUserObservable() {
        return userCommander.get(new UserRequest(searchTerm))
                .map(userResponse -> userResponse.items)
                .map(users -> users.get(index++))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(this::displayError);
    }


    private void initSearchBox() {
        ViewObservable.text((TextView) view(R.id.search))
                .debounce(1, TimeUnit.SECONDS)
                .map(onTextChangeEvent -> onTextChangeEvent.text.toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .subscribe(searchTerm -> {
                    index = 0;
                    this.searchTerm = searchTerm;
                    displayNext3Users();
                });
    }

    private void setupClickEvents() {
        clicks(findViewById(R.id.btnRefresh))
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable().repeat(3))
                .onErrorReturn(throwable -> new User())
                .subscribe(this::displayNextUser);

        clicks(view(R.id.close1))
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable())
                .subscribe(user -> updateUserAtPosition(user, 0));


        clicks(view(R.id.close2))
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable())
                .subscribe(user -> updateUserAtPosition(user, 1));


        clicks(view(R.id.close3))
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable())
                .subscribe(user -> updateUserAtPosition(user, 2));

    }

    private void initViewIds() {
        viewIds = new ArrayList<>();
        viewIds.add(new ViewModel(R.id.card1, R.id.name1, R.id.avatar1));
        viewIds.add(new ViewModel(R.id.card2, R.id.name2, R.id.avatar2));
        viewIds.add(new ViewModel(R.id.card3, R.id.name3, R.id.avatar3));
    }

    private void displayNextUser(User user) {
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
