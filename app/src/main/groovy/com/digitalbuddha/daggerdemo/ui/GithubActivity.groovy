package com.digitalbuddha.daggerdemo.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnClick
import com.digitalbuddha.daggerdemo.activitygraphs.R
import com.digitalbuddha.daggerdemo.dagger.DemoBaseActivity
import com.digitalbuddha.daggerdemo.dagger.ForActivity
import com.digitalbuddha.daggerdemo.model.Repo
import com.digitalbuddha.daggerdemo.rest.Github
import groovy.transform.CompileStatic
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

import javax.inject.Inject

@CompileStatic
//Composition over Inheritence
public class GithubActivity extends DemoBaseActivity {
    @InjectView(R.id.editText)
    EditText userName
    @InjectView(R.id.button)
    Button button;
    @Inject
    public Github api;

    @Inject
    @ForActivity
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate savedInstanceState
        setContentView R.layout.activity_post
        SwissKnife.inject this //view injection entry point
    }

    @OnClick(R.id.button)
    //cleaner than listeners
    public void getNumberOfReposForUser() {
        api.repos("digitalbuddha")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe
                {   //onNext
                    makeToast(it)
                }
                {   //onError
                    Toast.makeText(context, it.getCause().toString(), Toast.LENGTH_SHORT).show()

                }
    }

    private void makeToast(List<Repo> it) {
        String description = it.get(0).getClone_url()
        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
    }

}
