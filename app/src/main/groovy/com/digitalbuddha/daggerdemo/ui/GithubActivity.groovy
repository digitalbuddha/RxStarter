package com.digitalbuddha.daggerdemo.ui

import android.content.Context
import android.os.Bundle
import com.digitalbuddha.daggerdemo.dagger.DemoBaseActivity
import com.digitalbuddha.daggerdemo.dagger.ForActivity
import com.digitalbuddha.daggerdemo.model.Repo
import com.digitalbuddha.daggerdemo.store.GitHubStore
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
                .flatMap({ gitHubStore.get("digitalbuddha") }).observeOn(mainThread())
                .subscribe({ makeToast it.payload },//onNext
                { makeText(context, it.getCause().toString(), LENGTH_SHORT) show() })//onError
    }

    private void makeToast(ArrayList<Repo> it) {
        def description = it.get(0).clone_url
        makeText(context, description, LENGTH_SHORT).show()
    }
}
