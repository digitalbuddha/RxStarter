package com.digitalbuddha.rx.ui

import android.content.Context
import android.os.Bundle
import com.digitalbuddha.rx.store.GitHubStore
import com.digitalbuddha.rx.dagger.DemoBaseActivity
import com.digitalbuddha.rx.dagger.ForActivity
import com.digitalbuddha.rx.model.Repo
import groovy.transform.CompileStatic

import javax.inject.Inject

import static android.widget.Toast.LENGTH_SHORT
import static android.widget.Toast.makeText
import static com.digitalbuddha.daggerdemo.activitygraphs.R.id.button
import static com.digitalbuddha.daggerdemo.activitygraphs.R.layout.activity_post
import static rx.android.observables.ViewObservable.clicks
import static rx.android.schedulers.AndroidSchedulers.mainThread

@CompileStatic
public class GithubActivity extends DemoBaseActivity {
    @Inject
    protected GitHubStore gitHubStore

    @Inject
    @ForActivity
    protected Context context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate savedInstanceState
        contentView = activity_post
        initButton()
    }

    private initButton() {
        clicks(view(button))
                .flatMap({ gitHubStore.fresh("digitalbuddha") }).observeOn(mainThread())
                .subscribe({ makeToast it.payload },//onNext
                { makeText(context, it.getCause().toString(), LENGTH_SHORT) show() })//onError
    }

    private void makeToast(ArrayList<Repo> it) {
        def url = it.get(0).clone_url
        makeText(context, url, LENGTH_SHORT).show()
    }
}
