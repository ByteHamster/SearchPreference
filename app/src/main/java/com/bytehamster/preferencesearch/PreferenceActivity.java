package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.bytehamster.lib.preferencesearch.PreferenceSearchResult;
import com.bytehamster.lib.preferencesearch.SearchPreference;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefsFragment details = new PrefsFragment();
        details.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, details).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {
        SearchPreference searchPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            searchPreference = (SearchPreference) findPreference("searchPreference");
            searchPreference.openActivityOnResultClick(PreferenceActivity.class);
            searchPreference.setHistoryEnabled(true);

            searchPreference.setBreadcrumbsEnabled(false);
            searchPreference.addResourceFileToIndex(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();

            PreferenceSearchResult result = new PreferenceSearchResult(this);
            if (result.hasResult()) {
                Log.d("PreferenceActivity", "Found " + result + " in " + result.getResourceFile());

                getPreferenceScreen().removePreference(searchPreference);
                result.scrollTo();
                result.setIcon();
            }
        }
    }
}
