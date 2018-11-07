package com.bytehamster.preferencesearch;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.bytehamster.lib.preferencesearch.SearchConfiguration;
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
    private String savedInstanceSearchQuery;
    private boolean savedInstanceSearchEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            savedInstanceSearchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            savedInstanceSearchEnabled = savedInstanceState.getBoolean(KEY_SEARCH_ENABLED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchPreferenceActionView = (SearchPreferenceActionView) menu.findItem(R.id.search).getActionView();
        SearchConfiguration searchConfiguration = searchPreferenceActionView.getSearchConfiguration();
        searchConfiguration.index(R.xml.preferences);

        searchConfiguration.useAnimation(
                findViewById(android.R.id.content).getWidth(),
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
        Toast.makeText(this, result.getKey(), Toast.LENGTH_LONG).show();
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
}

