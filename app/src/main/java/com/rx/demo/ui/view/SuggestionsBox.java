package com.rx.demo.ui.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.commander.UserCommander;
import com.rx.demo.model.User;
import com.rx.demo.model.UserRequest;
import com.rx.demo.model.ViewModel;
import com.rx.demo.ui.activity.DemoBaseActivity;
import com.rx.demo.ui.utils.AnimationHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.android.observables.ViewObservable.clicks;

public class SuggestionsBox extends LinearLayout {
    @Inject
    public UserCommander userCommander;
    @Inject
    AnimationHelper animHelper;
    @Inject
    DemoBaseActivity activity;

    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.btnRefresh)
    Button refresh;

    @InjectView(R.id.close1)
    ImageView close1;
    @InjectView(R.id.close2)
    ImageView close2;
    @InjectView(R.id.close3)
    ImageView close3;

    @Icicle
    int index;

    @Icicle
    String searchTerm = "";

    private List<ViewModel> viewIds;

    public SuggestionsBox(Context context) {
        this(context, null);
    }

    public SuggestionsBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuggestionsBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ((DemoBaseActivity) context).inject(this);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        initViewModels();
        initSearchBox();
        setupClickEvents();
    }


    private Observable<User> nextUserObservable() {
        return userCommander.get(new UserRequest(searchTerm))
                .map(userResponse -> userResponse.items)
                .map(users -> users.get(index++))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(activity::displayError);
    }


    private void initSearchBox() {
        ViewObservable.text(search)
                .debounce(1, TimeUnit.SECONDS)
                .map(onTextChangeEvent -> {
                    return onTextChangeEvent.text.toString();
                })
                .filter(searchTerm -> searchTerm.length() > 0)
                .subscribe(searchTerm -> {
                    index = 0;
                    this.searchTerm = searchTerm;
                    showNext3Users();
                });
    }

    private void setupClickEvents() {
        clicks(refresh)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable().repeat(3))
                .onErrorReturn(throwable -> new User())
                .subscribe(this::displayNextUser);

        clicks(close1)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable())
                .subscribe(user -> updateUserAtPosition(user, 0));


        clicks(close2)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable())
                .subscribe(user -> updateUserAtPosition(user, 1), throwable -> {

                });


        clicks(close3)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> nextUserObservable())
                .subscribe(user -> updateUserAtPosition(user, 2));

    }

    private void showNext3Users() {
        nextUserObservable().repeat(3)
                .subscribe(this::displayNextUser);
    }

    private void initViewModels() {
        viewIds = new ArrayList<>();
        viewIds.add(new ViewModel(R.id.card1, R.id.name1, R.id.avatar1));
        viewIds.add(new ViewModel(R.id.card2, R.id.name2, R.id.avatar2));
        viewIds.add(new ViewModel(R.id.card3, R.id.name3, R.id.avatar3));
    }

    public void displayNextUser(User user) {
        int viewToChange = index % 3;
        updateUserAtPosition(user, viewToChange);
    }

    public void updateUserAtPosition(User user, int viewToChange) {

        ViewModel viewModel = viewIds.get(viewToChange);
        View card = findViewById(viewModel.getCardId());
        animHelper.showCard(card);
        ((TextView) findViewById(viewModel.getNameId())).setText(user.login);
        Picasso.with(getContext()).load(user.avatar_url).into((ImageView) findViewById(viewModel.getAvatarID()));
    }




}
