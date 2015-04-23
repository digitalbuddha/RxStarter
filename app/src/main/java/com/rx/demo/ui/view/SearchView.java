package com.rx.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.digitalbuddha.rx.demo.R;
import com.rx.demo.ui.activity.BaseActivity;
import com.rx.demo.ui.view.presenter.ImageSearchPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchView extends ScrollView {
    @InjectView(R.id.searchBox)
    EditText search;
    @InjectView(R.id.cards)
    public
    LinearLayout cardsLayout;
    @Inject
    ImageSearchPresenter presenter;


    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ((BaseActivity) context).inject(this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        presenter.takeView(this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        presenter.drawNewRowIfNeeded();
    }

    /**
     * Inflate a new row
     * Bind data to row
     * Add row to container
     * <p>
     *
     */
    public void addRow() {
        ViewGroup row = getLastResultsRow();
        while (row.getChildCount() < 3) {
          inflate(getContext(), R.layout.image_card, row);
        }
        presenter.drawNewRowIfNeeded();
    }

    /**
     * if less than 3 images in last row return it
     * else return a newly added linear layout
     *
     * @return ViewGroup with 0-2 images
     */
    private ViewGroup getLastResultsRow() {
        ViewGroup bottomRow = (ViewGroup) cardsLayout.getChildAt(cardsLayout.getChildCount() - 1);
        if (bottomRow == null || bottomRow.getChildCount() == 3) {
            inflate(getContext(), R.layout.row_view, cardsLayout);
            bottomRow = (ViewGroup) cardsLayout.getChildAt(cardsLayout.getChildCount() - 1);
        }
        return bottomRow;
    }


    /**
     * detach from presenter
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView();
    }

    public EditText getSearchView() {
        return getSearch();
    }

    public EditText getSearch() {
        return search;
    }

    public void updateSearchView(String searchTerm) {
        search.setText(searchTerm);
        presenter.clearResults();
    }
}
