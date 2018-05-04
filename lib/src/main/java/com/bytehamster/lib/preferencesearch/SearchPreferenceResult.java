package com.bytehamster.lib.preferencesearch;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SearchPreferenceResult {
    private String key;
    private int file;

    SearchPreferenceResult(String key, int file) {
        this.key = key;
        this.file = file;
    }

    /**
     * Returns the key of the preference pressed
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the file in which the result was found
     * @return The file in which the result was found
     */
    public int getResourceFile() {
        return file;
    }

    /**
     * Scrolls the PreferenceFragment to the position of the found preference
     * @param fragment The fragment to scroll
     */
    public void scrollTo(final PreferenceFragmentCompat fragment) {
        final ListView listView = findListView(fragment);
        if (listView == null) {
            Log.e(this.getClass().getSimpleName(), "ListView not found");
            return;
        }
        listView.post(new Runnable() {
            @Override
            public void run() {
                Preference preference = fragment.findPreference(key);
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
     * @param fragment The fragment to use to search for the preference
     */
    public void setIcon(PreferenceFragmentCompat fragment) {
        fragment.findPreference(key).setIcon(R.drawable.ic_arrow_right);
    }

    private ListView findListView(PreferenceFragmentCompat fragment) {
        if (fragment.getView() != null) {
            return fragment.getView().findViewById(android.R.id.list);
        }
        return null;
    }
}
