package com.bytehamster.lib.preferencesearch;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SearchPreferenceResult {
    static final String ARGUMENT_KEY = "key";
    static final String ARGUMENT_FILE = "file";

    private Bundle bundle;
    private PreferenceFragment fragment;

    public SearchPreferenceResult(PreferenceFragment fragment) {
        this.fragment = fragment;
        bundle = fragment.getArguments();
    }

    private Preference findPreference() {
        return fragment.findPreference(getKey());
    }

    private ListView findListView() {
        if (fragment.getView() != null) {
            return fragment.getView().findViewById(android.R.id.list);
        }
        return null;
    }

    public boolean hasData() {
        return bundle != null && !TextUtils.isEmpty(bundle.getString(ARGUMENT_KEY));
    }

    public String getKey() {
        return bundle.getString(ARGUMENT_KEY);
    }

    public void scrollTo() {
        final ListView listView = findListView();
        if (listView == null) {
            Log.e(this.getClass().getSimpleName(), "ListView not found");
            return;
        }
        listView.post(new Runnable() {
            @Override
            public void run() {
                Preference preference = findPreference();
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
        });
    }

    public void setIcon() {
        findPreference().setIcon(R.drawable.ic_arrow_right);
    }

    public int getResourceFile() {
        return bundle.getInt(ARGUMENT_FILE);
    }
}
