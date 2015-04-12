package com.rx.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.ui.activity.BaseActivity;
import com.rx.demo.ui.activity.SearchActivity;
import com.rx.demo.ui.view.presenter.ImageSearchPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

public class HistoryView extends ListView {
    @Inject
    ImageSearchPresenter presenter;

    @Inject
    SearchActivity activity;

    ArrayList<String> history = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public HistoryView(Context context) {
        this(context, null);
    }

    public HistoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ((BaseActivity) context).inject(this);
    }


    @Override
    protected void onFinishInflate() {
        presenter.getHistoryViewBus()
                .subscribe(this::addToHistory);

        adapter = new ArrayAdapter<>(getContext(),
                R.layout.history_row,R.id.list_content, history);
        setAdapter(adapter);

        setOnItemClickListener((adapterView, view, i, l) -> {
            presenter.changeSearchTerm(history.get(i));
            activity.flipViews();
        });
    }


    private void addToHistory(String s) {

       if(history.isEmpty()||!history.get(history.size()-1).equals(s))
        history.add(s);
        adapter.notifyDataSetChanged();
    }

}
