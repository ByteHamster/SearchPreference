package com.bytehamster.lib.preferencesearch;

import android.support.annotation.NonNull;

public interface SearchPreferenceResultListener {
    void onSearchResultClicked(@NonNull SearchPreferenceResult result);
}
