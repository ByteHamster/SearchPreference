package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListAdapter;
import android.widget.ListView;

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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();

            if (getArguments() != null) {
                String key = getArguments().getString("KEY");
                if (!TextUtils.isEmpty(key)) {
                    scrollToItem(key);
                    findPreference(key).setIcon(R.drawable.ic_arrow_right);
                }
            }
        }

        private void scrollToItem(String preferenceName) {
            ListView listView = getView().findViewById(android.R.id.list);
            final Preference preference = findPreference(preferenceName);
            ListAdapter adapter = listView.getAdapter();
            if (preference != null) {
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    Preference iPref = (Preference) adapter.getItem(i);
                    if (iPref == preference) {
                        listView.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
}
