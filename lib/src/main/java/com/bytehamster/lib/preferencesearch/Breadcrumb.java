package com.bytehamster.lib.preferencesearch;

import android.support.annotation.Nullable;
import android.text.TextUtils;

class Breadcrumb {
    private Breadcrumb() {

    }

    /**
     * Joins two breadcrumbs
     * @param s1 First breadcrumb, might be null
     * @param s2 Second breadcrumb
     * @return Both breadcrumbs joined
     */
    static String concat(@Nullable String s1, String s2) {
        if (TextUtils.isEmpty(s1)) {
            return s2;
        }
        return s1 + " > " + s2;
    }
}
