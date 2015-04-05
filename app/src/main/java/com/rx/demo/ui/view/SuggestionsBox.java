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
import com.rx.demo.observable.SuggestionPresenter;
import com.rx.demo.ui.activity.DemoBaseActivity;
import com.rx.demo.ui.utils.AnimationHelper;
import com.rx.demo.util.SubscriptionManager;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

import static rx.android.observables.ViewObservable.clicks;
import static rx.android.observables.ViewObservable.text;

public class SuggestionsBox extends ScrollView {
    @Inject
    SuggestionPresenter suggestionPresenter;
    @Inject
    AnimationHelper animHelper;
    @Inject
    SubscriptionManager subscriptionManager;
    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.btnRefresh)
    Button nextThree;
    @InjectView(R.id.btnNext)
    Button next;
    @InjectView(R.id.cards)
    LinearLayout cardsLayout;

    @Icicle
    AtomicInteger index;

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
        initButtons();
    }

    private void initSearchBox() {
        //display first 3 users anytime search term changes
        subscriptionManager.addSubscription(text(search)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(onTextChangeEvent -> onTextChangeEvent.text.toString())
                .filter(searchTerm -> searchTerm.length() > 0)
                .flatMap(suggestionPresenter::getFirstThreeUsers)
                .subscribe(this::ShowAnotherUser));
    }


    private void initButtons() {
        //show 3 more users
        subscriptionManager.addSubscription(clicks(nextThree)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> suggestionPresenter.getNextThreeUsers(search.getText().toString()))
                .subscribe(this::ShowAnotherUser));
        //show 1 more user
        subscriptionManager.addSubscription(clicks(next)
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap(onClickEvent -> suggestionPresenter.nextUser(search.getText().toString()))
                .subscribe(this::ShowAnotherUser));
    }


    private void ShowAnotherUser(User user) {
        UserCard userCard = (UserCard) inflate(getContext(), R.layout.user_card, null);
        //TODO move binidng to within use card class
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

    private void initCloseButton(UserCard card) {
        subscriptionManager.addSubscription(clicks(card.findViewById(R.id.close))
                .subscribe(clickEvent -> cardsLayout.removeView(card)));
    }

    //onRotate, make sure we start at same user index that we left off
    @Override
    public Parcelable onSaveInstanceState() {
        index = suggestionPresenter.getIndex();
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        suggestionPresenter.setIndex(index);
    }
}
