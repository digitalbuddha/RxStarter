package com.rx.demo.ui.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.rx.demo.model.User;
import com.rx.demo.observable.UserObservables;
import com.rx.demo.ui.activity.DemoBaseActivity;
import com.rx.demo.ui.utils.AnimationHelper;
import com.rx.demo.util.SubscriptionManager;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import rx.android.events.OnClickEvent;

import static rx.android.observables.ViewObservable.clicks;
import static rx.android.observables.ViewObservable.text;

public class SuggestionsBox extends ScrollView {
    @Inject
    UserObservables userObservables;
    @Inject
    AnimationHelper animHelper;
    @Inject
    SubscriptionManager subscriptionManager;
    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.btnRefresh)
    Button refresh;
    @InjectView(R.id.cards)
    LinearLayout cardsLayout;

    @Icicle
    int index;

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
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        initSearchBox();
        initRefresh();
    }

    private void initSearchBox() {
        //display first 3 users anytime search term changes
        subscriptionManager.addSubscription(text(search)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(onTextChangeEvent -> onTextChangeEvent.text.toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .doOnNext(userObservables::setSearchTerm)
                .flatMap(s -> userObservables.next3UserStream())
                .subscribe(this::displayUser));
    }


    private void initRefresh() {
        //show next 3 users on refresh click
        subscriptionManager.addSubscription(clicks(refresh)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> userObservables.next3UserStream())
                .subscribe(this::displayUser));
    }


    public void displayUser(User user) {
        createNewUserCard(user);
    }


    private void createNewUserCard(User user) {
        UserCard userCard = (UserCard) inflate(getContext(), R.layout.user_card, null);
        bindUserData(user, userCard);
        initCloseButton(userCard);
    }

    private void bindUserData(User user, UserCard userCard) {
        TextView nameView = (TextView) userCard.findViewById(R.id.name);
        nameView.setText(user.login);
        Picasso.with(getContext())
                .load(user.avatar_url)
                .into((ImageView) userCard.findViewById(R.id.avatar));
        cardsLayout.addView(userCard, 0);
        animHelper.showCard(userCard);

    }

    private void initCloseButton(UserCard oldCard) {
        subscriptionManager.addSubscription(clicks(oldCard.findViewById(R.id.close))
                .flatMap((OnClickEvent onClickEvent) -> userObservables.nextUserObservable())
                .subscribe(newUser -> {
                    cardsLayout.removeView(oldCard);
                }));
    }

    //onRotate, make sure we start at same user index that we left off
    @Override
    public Parcelable onSaveInstanceState() {
        index = userObservables.getIndex();
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        userObservables.setIndex(index);
    }
}
