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

    /**
     * View the result of a possible preference search
     * @param fragment The fragment to use for finding the preference
     * @param bundle The arguments containing the search result (intent.getExtras or fragment.getArguments)
     */
    public SearchPreferenceResult(PreferenceFragment fragment, Bundle bundle) {
        this.fragment = fragment;
        this.bundle = bundle;
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

    /**
     * Checks if there was a search result pressed
     * @return True if a search result was pressed
     */
    public boolean hasData() {
        return bundle != null && !TextUtils.isEmpty(bundle.getString(ARGUMENT_KEY));
    }

    /**
     * Returns the key of the preference pressed
     * @return The key
     */
    public String getKey() {
        return bundle.getString(ARGUMENT_KEY);
    }

    /**
     * Scrolls the PreferenceFragment to the position of the found preference
     */
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

    /**
     * Sets the icon of the found preference to an arrow
     */
    public void setIcon() {
        findPreference().setIcon(R.drawable.ic_arrow_right);
    }

    /**
     * Returns the file in which the result was found
     * @return The file in which the result was found
     */
    public int getResourceFile() {
        return bundle.getInt(ARGUMENT_FILE);
    }
}
