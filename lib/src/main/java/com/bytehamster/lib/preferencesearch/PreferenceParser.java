package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class PreferenceParser {
    private static final int MAX_RESULTS = 10;
    private static final List<String> BLACKLIST = Arrays.asList(SearchPreference.class.getName(), "PreferenceCategory");
    private static final List<String> CONTAINERS = Arrays.asList("PreferenceCategory", "PreferenceScreen");
    private Context context;
    private ArrayList<PreferenceItem> allEntries = new ArrayList<>();

    PreferenceParser(Context context) {
        this.context = context;
    }

    void addResourceFile(int resId, String breadcrumb) {
        allEntries.addAll(parseFile(resId, breadcrumb));
    }

    private ArrayList<PreferenceItem> parseFile(int resId, String firstBreadcrumb) {
        java.util.ArrayList<PreferenceItem> results = new ArrayList<>();
        XmlPullParser xpp = context.getResources().getXml(resId);

        try {
            ArrayList<String> breadcrumbs = new ArrayList<>();
            if (!TextUtils.isEmpty(firstBreadcrumb)) {
                breadcrumbs.add(firstBreadcrumb);
            }
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    PreferenceItem result = parseSearchResult(xpp);
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
        String result = "";
        for (String crumb : breadcrumbs) {
            if (!TextUtils.isEmpty(crumb)) {
                result = Breadcrumb.concat(result, crumb);
            }
        }
        return result;
    }

    private PreferenceItem parseSearchResult(XmlPullParser xpp) {
        PreferenceItem result = new PreferenceItem();
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
        Log.d("PreferenceParser", "Found: " + xpp.getName() + "/" + result);
        return result;
    }

    private String readStringArray(String s) {
        if (s.startsWith("@")) {
            try {
                int id = Integer.parseInt(s.substring(1));
                String[] elements = context.getResources().getStringArray(id);
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
                return context.getString(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    List<PreferenceItem> searchFor(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return new ArrayList<>();
        }
        ArrayList<PreferenceItem> results = new ArrayList<>();

        for (PreferenceItem item : allEntries) {
            if (item.matches(keyword)) {
                results.add(item);
            }
        }

        Collections.sort(results, new Comparator<PreferenceItem>() {
            @Override
            public int compare(PreferenceItem i1, PreferenceItem i2) {
                return floatCompare(i2.getScore(keyword), i1.getScore(keyword));
            }
        });

        if (results.size() > MAX_RESULTS) {
            return results.subList(0, MAX_RESULTS);
        } else {
            return results;
        }
    }

    @SuppressWarnings("UseCompareMethod")
    private static int floatCompare(float x, float y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
