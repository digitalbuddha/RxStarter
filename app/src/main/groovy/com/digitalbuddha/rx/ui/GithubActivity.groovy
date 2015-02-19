package com.digitalbuddha.rx.ui
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.digitalbuddha.daggerdemo.activitygraphs.R
import com.digitalbuddha.rx.dagger.DemoBaseActivity
import com.digitalbuddha.rx.model.User
import com.digitalbuddha.rx.store.GitHubStore
import com.digitalbuddha.rx.util.MapToSingleUser
import groovy.transform.CompileStatic
import rx.functions.Action1

import javax.inject.Inject

import static rx.android.observables.ViewObservable.clicks

@CompileStatic
public class GithubActivity extends DemoBaseActivity {
    @Inject
    protected GitHubStore gitHubStore

    @InjectView(R.id.name1)
    TextView name1;
    @InjectView(R.id.name2)
    TextView name2;
    @InjectView(R.id.name3)
    TextView name3;
    @InjectView(R.id.refresh)
    Button refresh
    @InjectView(R.id.close1)
    Button close1
    @InjectView(R.id.close2)
    Button close2
    @InjectView(R.id.close3)
    Button close3

    //observer that reloads each suggestion
    Action1 reloadFirstUserObserver
    Action1 reloadSecondUserObserver
    Action1 reloadThirdUserObserver
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate savedInstanceState
        contentView = R.layout.suggestions_layout
        SwissKnife.inject this
        //define what actions to take when subscribing
        setupObservers()

        //create an observable for the api request call
        def requestObservable = gitHubStore.get(Math.floor(Math.random() * 500))

        //create another observable from request which will have only the payload
        def responseObservable = requestObservable.map({ it.payload })

        //when the screen loads, kick off an initial subscription to
        // the observable responsible for reloading all 3 suggestions
        subscribeWithAllObservers(responseObservable)

        //on click of refresh all map to the response
        def refreshAllObservable = clicks(refresh)
                .flatMap({ responseObservable })
        //
        subscribeWithAllObservers(refreshAllObservable)

        //setup each of the click listeners for the X next to each suggestion
        //if a user clicks the X we should load a new suggestion
        clicks(close1)
                .flatMap({ responseObservable })
                .map(new MapToSingleUser())
                .subscribe(reloadFirstUserObserver)
        clicks(close2)
                .flatMap({ responseObservable })
                .map(new MapToSingleUser())
                .subscribe(reloadSecondUserObserver)
        clicks(close3)
                .flatMap({ responseObservable })
                .map(new MapToSingleUser())
                .subscribe(reloadThirdUserObserver)
    }

    private void setupObservers() {
        reloadFirstUserObserver = { User user -> name1.setText(user.login) //bind to first
        }
        reloadSecondUserObserver = { User user -> name2.setText(user.login)//bind to second

        }
        reloadThirdUserObserver = { User user -> name3.setText(user.login)//bind to third
        }
    }

    private void subscribeWithAllObservers(rx.Observable<ArrayList<User>> observable) {
        //publish allows you to control when an observable starts firing with .connect()
        //this is useful for cases when you'd like to have multiple subscribers prior
        // to the observable becoming hot
        def connectableObservable=observable.publish()
        //get a single random user from the response and then have each of
        //the three screen elements subscribe to it thus updating the screens with new data
        connectableObservable.map(new MapToSingleUser()).subscribe(reloadFirstUserObserver)
        connectableObservable.map(new MapToSingleUser()).subscribe(reloadSecondUserObserver)
        connectableObservable.map(new MapToSingleUser()).subscribe(reloadThirdUserObserver)
        connectableObservable.connect()
    }

    public static getRandomIndex(int size) {
        new Random().nextInt(size);
    }

}
