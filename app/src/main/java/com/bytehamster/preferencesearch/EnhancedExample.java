package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;

/**
 * This file demonstrates some additional features that might not be needed when setting it up for the first time
 */
public class EnhancedExample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {
        SearchPreference searchPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            searchPreference = (SearchPreference) findPreference("searchPreference");
            searchPreference.openActivityOnResultClick(EnhancedExample.class);
            searchPreference.addResourceFileToIndex(R.xml.preferences, "Main file");
            searchPreference.addResourceFileToIndex(R.xml.preferences2, "Second file");
            searchPreference.setBreadcrumbsEnabled(true);
            searchPreference.setHistoryEnabled(true);
        }

        @Override
        public void onStart() {
            super.onStart();

            SearchPreferenceResult result = new SearchPreferenceResult(this, getActivity().getIntent().getExtras());
            if (result.hasData()) {
                if (result.getResourceFile() == R.xml.preferences) {
                    getPreferenceScreen().removePreference(searchPreference); // Do not allow to click search multiple times
                    result.scrollTo();
                    findPreference(result.getKey()).setTitle("RESULT: " + findPreference(result.getKey()).getTitle());
                } else {
                    // Result was found in the other file
                    getPreferenceScreen().removeAll();
                    addPreferencesFromResource(R.xml.preferences2);
                    result.scrollTo();
                    result.setIcon();
                }
            }
        }
    }
}
