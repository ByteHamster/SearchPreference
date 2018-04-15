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
    private static final List<String> CONTAINERS = Arrays.asList("PreferenceCategory", "PreferenceScreen");
    private Activity activity;
    private ArrayList<SearchResult> allEntries = new ArrayList<>();

    PreferenceSearcher(Activity activity) {
        this.activity = activity;
    }

    void addResourceFile(int resId, String breadcrumb) {
        allEntries.addAll(parseFile(resId, breadcrumb));
    }

    private ArrayList<SearchResult> parseFile(int resId, String firstBreadcrumb) {
        java.util.ArrayList<SearchResult> results = new ArrayList<>();
        XmlPullParser xpp = activity.getResources().getXml(resId);

        try {
            ArrayList<String> breadcrumbs = new ArrayList<>();
            if (!TextUtils.isEmpty(firstBreadcrumb)) {
                breadcrumbs.add(firstBreadcrumb);
            }
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    SearchResult result = parseSearchResult(xpp);
                    result.resId = resId;

                    if (!BLACKLIST.contains(xpp.getName()) && result.hasData()) {
                        result.breadcrumbs = joinBreadcrumbs(breadcrumbs);
                        results.add(result);
                    }
                    if (CONTAINERS.contains(xpp.getName())) {
                        breadcrumbs.add(result.title == null ? "" : result.title);
                    }
                } else if (xpp.getEventType() == XmlPullParser.END_TAG && CONTAINERS.contains(xpp.getName())) {
                    breadcrumbs.remove(breadcrumbs.size() - 1);
                }

                xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private String joinBreadcrumbs(ArrayList<String> breadcrumbs) {
        StringBuilder result = new StringBuilder();
        for (String crumb : breadcrumbs) {
            if (!TextUtils.isEmpty(crumb)) {
                if (!TextUtils.isEmpty(result.toString())) {
                    result.append(" > ");
                }
                result.append(crumb);
            }
        }
        return result.toString();
    }

    private SearchResult parseSearchResult(XmlPullParser xpp) {
        SearchResult result = new SearchResult();
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            switch (xpp.getAttributeName(i)) {
                case "title":
                    result.title = readString(xpp.getAttributeValue(i));
                    break;
                case "summary":
                    result.summary = readString(xpp.getAttributeValue(i));
                    break;
                case "key":
                    result.key = readString(xpp.getAttributeValue(i));
                    break;
                case "entries":
                    result.entries = readStringArray(xpp.getAttributeValue(i));
                    break;
            }
        }
        Log.d("PreferenceSearcher", "Found: " + xpp.getName() + "/" + result);
        return result;
    }

    private String readStringArray(String s) {
        if (s.startsWith("@")) {
            try {
                int id = Integer.parseInt(s.substring(1));
                String[] elements = activity.getResources().getStringArray(id);
                return TextUtils.join(",", elements);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    private String readString(String s) {
        if (s.startsWith("@")) {
            try {
                int id = Integer.parseInt(s.substring(1));
                return activity.getString(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    ArrayList<SearchResult> searchFor(final String keyword) {
        ArrayList<SearchResult> results = new ArrayList<>();

        for (SearchResult res : allEntries) {
            if (res.contains(keyword)) {
                results.add(res);
            }
        }
        return results;
    }

    class SearchResult {
        String title, summary, key, entries, breadcrumbs;
        int resId;

        private boolean hasData() {
            return title != null || summary != null;
        }

        private boolean contains(String keyword) {
            return stringContains(title, keyword) || stringContains(summary, keyword)
                    || stringContains(entries, keyword) || stringContains(breadcrumbs, keyword);
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
