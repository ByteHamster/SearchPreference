package com.bytehamster.lib.preferencesearch;

import android.text.TextUtils;
import org.apache.commons.text.similarity.FuzzyScore;

import java.util.ArrayList;
import java.util.Locale;

class PreferenceItem extends ListItem {
    static final int TYPE = 2;
    private static FuzzyScore fuzzyScore = new FuzzyScore(Locale.getDefault());

    String title;
    String summary;
    String key;
    String entries;
    String breadcrumbs;
    String keywords;
    ArrayList<String> keyBreadcrumbs = new ArrayList<>();
    int resId;

    private float lastScore = 0;
    private String lastKeyword = null;

    boolean hasData() {
        return title != null || summary != null;
    }

    boolean matchesFuzzy(String keyword) {
        return getScore(keyword) > 0.3;
    }

    boolean matches(String keyword) {
        return getInfo().contains(keyword);
    }

    float getScore(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return 0;
        } else if (TextUtils.equals(lastKeyword, keyword)) {
            return lastScore;
        }
        String info = getInfo();

        float score = fuzzyScore.fuzzyScore(info, "ø" + keyword);
        float maxScore = (keyword.length() + 1) * 3 - 2; // First item can not get +2 bonus score

        lastScore = score / maxScore;
        lastKeyword = keyword;
        return lastScore;
    }

    private String getInfo() {
        StringBuilder infoBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(title)) {
            infoBuilder.append("ø").append(title);
        }
        if (!TextUtils.isEmpty(summary)) {
            infoBuilder.append("ø").append(summary);
        }
        if (!TextUtils.isEmpty(entries)) {
            infoBuilder.append("ø").append(entries);
        }
        if (!TextUtils.isEmpty(breadcrumbs)) {
            infoBuilder.append("ø").append(breadcrumbs);
        }
        if (!TextUtils.isEmpty(keywords)) {
            infoBuilder.append("ø").append(keywords);
        }
        return infoBuilder.toString();
    }


    @Override
    public String toString() {
        return "PreferenceItem: " + title + " " + summary + " " + key;
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
