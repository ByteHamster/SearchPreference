package com.bytehamster.preferencesearch;


import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.bytehamster.lib.preferencesearch.SearchConfiguration;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import com.bytehamster.lib.preferencesearch.SearchPreferenceActionView;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;

/**
 * This file demonstrates how to use the library without actually displaying a PreferenceFragment
 */
public class SearchViewExample extends AppCompatActivity implements SearchPreferenceResultListener {
    private static final String KEY_SEARCH_QUERY = "search_query";
    private static final String KEY_SEARCH_ENABLED = "search_enabled";
    private SearchPreferenceActionView searchPreferenceActionView;
    private MenuItem searchPreferenceMenuItem;
    private String savedInstanceSearchQuery;
    private boolean savedInstanceSearchEnabled;
    private PrefsFragment prefsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            savedInstanceSearchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            savedInstanceSearchEnabled = savedInstanceState.getBoolean(KEY_SEARCH_ENABLED);
        }

        prefsFragment = new PrefsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchPreferenceMenuItem = menu.findItem(R.id.search);
        searchPreferenceActionView = (SearchPreferenceActionView) searchPreferenceMenuItem.getActionView();
        SearchConfiguration searchConfiguration = searchPreferenceActionView.getSearchConfiguration();
        searchConfiguration.index(R.xml.preferences);

        searchConfiguration.useAnimation(
                findViewById(android.R.id.content).getWidth() - getSupportActionBar().getHeight()/2,
                -getSupportActionBar().getHeight()/2,
                findViewById(android.R.id.content).getWidth(),
                findViewById(android.R.id.content).getHeight(),
                getResources().getColor(R.color.colorPrimary));

        searchPreferenceActionView.setActivity(this);

        final MenuItem searchPreferenceMenuItem = menu.findItem(R.id.search);
        searchPreferenceMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchPreferenceActionView.cancelSearch();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        if (savedInstanceSearchEnabled) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    // If we do not use a handler here, it will not be possible
                    // to use the menuItem after dismissing the searchView
                    searchPreferenceMenuItem.expandActionView();
                    searchPreferenceActionView.setQuery(savedInstanceSearchQuery, false);
                }
            });
        }
        return true;
    }

    @Override
    public void onSearchResultClicked(@NonNull final SearchPreferenceResult result) {
        searchPreferenceActionView.cancelSearch();
        searchPreferenceMenuItem.collapseActionView();
        result.highlight(prefsFragment);
    }

    @Override
    public void onBackPressed() {
        if (!searchPreferenceActionView.cancelSearch()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SEARCH_QUERY, searchPreferenceActionView.getQuery().toString());
        outState.putBoolean(KEY_SEARCH_ENABLED, !searchPreferenceActionView.isIconified());
        searchPreferenceActionView.cancelSearch();
        super.onSaveInstanceState(outState);
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);

            SearchPreference searchPreference = (SearchPreference) findPreference("searchPreference");
            searchPreference.setVisible(false);
        }
    }
}
