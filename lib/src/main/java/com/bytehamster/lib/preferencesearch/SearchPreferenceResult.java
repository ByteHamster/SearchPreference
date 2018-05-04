package com.bytehamster.lib.preferencesearch;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


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
     * Highlight the preference that was found
     * @param prefsFragment Fragment that contains the preference
     */
    public void highlight(PreferenceFragmentCompat prefsFragment) {
        final Preference prefResult = prefsFragment.findPreference(getKey());
        prefsFragment.scrollToPreference(prefResult);

        final Drawable oldIcon = prefResult.getIcon();
        final boolean oldSpaceReserved = prefResult.isIconSpaceReserved();

        prefResult.setIcon(R.drawable.ic_arrow_right);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefResult.setIcon(oldIcon);
                prefResult.setIconSpaceReserved(oldSpaceReserved);
            }
        }, 1000);
    }
}
