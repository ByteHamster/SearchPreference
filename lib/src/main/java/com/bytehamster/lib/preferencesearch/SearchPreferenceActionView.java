package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.View;

public class SearchPreferenceActionView extends SearchView {
    private SearchPreferenceFragment searchFragment;
    private SearchConfiguration searchConfiguration = new SearchConfiguration();
    private AppCompatActivity activity;

    public SearchPreferenceActionView(Context context) {
        super(context);
        initView();
    }

    public SearchPreferenceActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchPreferenceActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        searchConfiguration.setSearchBarEnabled(false);
        setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFragment.setSearchTerm(newText);
                return true;
            }
        });
        setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && (searchFragment == null || !searchFragment.isVisible())) {
                    searchFragment = searchConfiguration.showSearchFragment();
                    searchFragment.setHistoryClickListener(new SearchPreferenceFragment.HistoryClickListener() {
                        @Override
                        public void onHistoryEntryClicked(String entry) {
                            setQuery(entry, false);
                        }
                    });
                }
            }
        });
    }

    public SearchConfiguration getSearchConfiguration() {
        return searchConfiguration;
    }

    public boolean onBackPressed() {
        if (!isIconified()) {
            setQuery("", false);
            setIconified(true);
            if (searchFragment.isVisible()) {
                activity.getSupportFragmentManager().popBackStack();
            }
            return true;
        }

        return false;
    }

    public void setActivity(AppCompatActivity activity) {
        searchConfiguration.setActivity(activity);
        this.activity = activity;
    }

    public void close() {
        setQuery("", false);
        setIconified(true);
        if (searchFragment.isVisible()) {
            activity.getSupportFragmentManager().popBackStack();
        }
    }
}
