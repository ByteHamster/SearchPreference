package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;
import com.bytehamster.lib.preferencesearch.SearchConfiguration;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import com.bytehamster.lib.preferencesearch.SearchPreferenceFragment;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;

/**
 * This file demonstrates how to use the library without actually displaying a PreferenceFragment
 */
public class NoPreferencesExample extends AppCompatActivity implements SearchPreferenceResultListener {
    SearchPreferenceFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchConfiguration config = new SearchConfiguration(this);
        config.setFragmentContainerViewId(android.R.id.content);
        config.index().addFile(R.xml.preferences);
        config.setSearchBarEnabled(false);
        config.setFuzzySearchEnabled(false);

        fragment = config.showSearchFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

        fragment.setSearchTerm("Checkbox");
    }

    @Override
    public void onSearchResultClicked(@NonNull final SearchPreferenceResult result) {
        Toast.makeText(this, result.getKey(), Toast.LENGTH_LONG).show();
    }
}
