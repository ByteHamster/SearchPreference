package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreference;

public class PreferenceActivity extends AppCompatActivity {

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
            searchPreference.openActivityOnResultClick(PreferenceActivity.class);
            searchPreference.setHistoryEnabled(true);
            searchPreference.setBreadcrumbsEnabled(false);
            searchPreference.addResourceFileToIndex(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();

            SearchPreferenceResult result = new SearchPreferenceResult(this, getActivity().getIntent().getExtras());
            if (result.hasData()) {
                getPreferenceScreen().removePreference(searchPreference);
                result.scrollTo();
                result.setIcon();
            }
        }
    }
}
