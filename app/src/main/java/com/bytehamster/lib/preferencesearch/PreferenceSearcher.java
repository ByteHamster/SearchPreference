package com.bytehamster.lib.preferencesearch;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PreferenceSearcher {
    private static final List<String> BLACKLIST = Arrays.asList(SearchPreference.class.getName(), "PreferenceCategory");
    private Activity activity;
    private ArrayList<SearchResult> allEntries = new ArrayList<>();

    PreferenceSearcher(Activity activity) {
        this.activity = activity;
    }

    void addResourceFile(int resId) {
        allEntries.addAll(parseFile(resId));
    }

    private ArrayList<SearchResult> parseFile(int resId) {
        java.util.ArrayList<SearchResult> results = new ArrayList<>();
        XmlPullParser xpp = activity.getResources().getXml(resId);

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (!BLACKLIST.contains(xpp.getName())) {
                        SearchResult result = new SearchResult();
                        result.resId = resId;
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            String valSanitized = xpp.getAttributeValue(i);
                            if (valSanitized.startsWith("@")) {
                                try {
                                    int id = Integer.parseInt(valSanitized.substring(1));
                                    valSanitized = activity.getString(id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            switch (xpp.getAttributeName(i)) {
                                case "title":
                                    result.title = valSanitized;
                                    break;
                                case "summary":
                                    result.summary = valSanitized;
                                    break;
                                case "key":
                                    result.key = valSanitized;
                                    break;
                            }
                        }

                        Log.d("PreferenceSearcher", "Found: " + xpp.getName() + "/" + result);
                        if (result.hasData()) {
                            results.add(result);
                        }
                    }
                }
                xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    ArrayList<SearchResult> searchFor(final String keyword) {
        ArrayList<SearchResult> results = new ArrayList<>();

        if (TextUtils.isEmpty(keyword)) {
            return results;
        }

        for (SearchResult res : allEntries) {
            if (res.contains(keyword)) {
                results.add(res);
            }
        }
        return results;
    }

    class SearchResult {
        String title, summary, key;
        int resId;

        private boolean hasData() {
            return title != null || summary != null;
        }

        private boolean contains(String keyword) {
            return stringContains(title, keyword) || stringContains(summary, keyword);
        }

        @Override
        public String toString() {
            return "SearchResult: " + title + " " + summary + " " + key;
        }
    }

    private boolean stringContains(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return simplify(s1).contains(simplify(s2));
    }

    private String simplify (String s) {
        return s.toLowerCase().replace(" ", "");
    }
}
