package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;

/**
 * This file demonstrates some additional features that might not be needed when setting it up for the first time
 */
public class EnhancedExample extends AppCompatActivity implements SearchPreferenceResultListener {
    private PrefsFragment prefsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsFragment = new PrefsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment).commit();
    }

    @Override
    public void onSearchResultClicked(SearchPreferenceResult result) {
        prefsFragment.onSearchResultClicked(result);
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {
        SearchPreference searchPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);

            searchPreference = (SearchPreference) findPreference("searchPreference");
            searchPreference.setActivity((AppCompatActivity) getActivity());
            searchPreference.addResourceFileToIndex(R.xml.preferences, "Main file");
            searchPreference.addResourceFileToIndex(R.xml.preferences2, "Second file");
            searchPreference.setBreadcrumbsEnabled(true);
            searchPreference.setHistoryEnabled(true);
        }

        private void onSearchResultClicked(SearchPreferenceResult result) {
            if (result.getResourceFile() == R.xml.preferences) {
                getPreferenceScreen().removePreference(searchPreference); // Do not allow to click search multiple times
                result.scrollTo(this);
                findPreference(result.getKey()).setTitle("RESULT: " + findPreference(result.getKey()).getTitle());
            } else {
                // Result was found in the other file
                getPreferenceScreen().removeAll();
                addPreferencesFromResource(R.xml.preferences2);
                result.scrollTo(this);
                result.setIcon(this);
            }
        }
    }
}
