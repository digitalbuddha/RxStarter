package com.digitalbuddha.daggerdemo.ui;

import android.os.Bundle;
import android.widget.EditText;

import com.digitalbuddha.daggerdemo.activitygraphs.R;
import com.digitalbuddha.daggerdemo.dagger.DemoBaseActivity;
import com.digitalbuddha.daggerdemo.job.GetReposJob;
import com.digitalbuddha.daggerdemo.utils.JsonParser;
import com.path.android.jobqueue.JobManager;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


//Composition over Inheritence
public class GithubActivity extends DemoBaseActivity {
    @Inject
    Provider<GetReposJob> repoJob; //Provider will give new instane everytime .get() is called
    @Inject
    public JobManager jobManager;
    @InjectView(R.id.editText)
    EditText userName;
    @Inject ActivityTitleController activityTitleController;  //Helper class for activities, for example to init action bar.
    @Inject
    JsonParser jsonParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.inject(this); //view injection entry point

        activityTitleController.setTitle("Github");
    }

    @OnClick(R.id.button) //cleaner than listeners
    public void getNumberOfReposForUser() {
        GetReposJob job = repoJob.get();//since we can inject activity context directly into task, constructor to pass context is no longer needed
        job.userName = userName.getText().toString(); //Set fields on task directly from screen, less copying values=less mistakes
        jobManager.addJobInBackground(job);

    }


    public void onEventMainThread(GetReposJob job) {
        String repos = jsonParser.convertObjectToJSON(job.repos);
        userName.setText(repos);
    }


}
