package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.View;
import com.bytehamster.lib.preferencesearch.ui.AnimationUtils;

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
                if (searchFragment != null) {
                    searchFragment.setSearchTerm(newText);
                }
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

    /**
     * Hides the search fragment
     * @return true if it was hidden, so the calling activity should not go back itself.
     */
    public boolean cancelSearch() {
        setQuery("", false);

        boolean didSomething = false;
        if (!isIconified()) {
            setIconified(true);
            didSomething = true;
        }
        if (searchFragment != null && searchFragment.isVisible()) {
            if (getSearchConfiguration().getRevealAnimationSetting() != null) {
                AnimationUtils.startCircularExitAnimation(getContext(), searchFragment.getView(),
                        getSearchConfiguration().getRevealAnimationSetting(),
                        new AnimationUtils.OnDismissedListener() {
                    @Override
                    public void onDismissed() {
                        removeFragment();
                    }
                });
            } else {
                removeFragment();
            }

            didSomething = true;
        }
        return didSomething;
    }

    private void removeFragment() {
        if (searchFragment.isVisible()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            fm.popBackStack(SearchPreferenceFragment.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void setActivity(AppCompatActivity activity) {
        searchConfiguration.setActivity(activity);
        this.activity = activity;
    }
}
