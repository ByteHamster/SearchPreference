package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.annotation.Nullable;
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

    void addResourceFile(int resId, String breadcrumb, String ignored) {
        allEntries.addAll(parseFile(resId, breadcrumb, ignored));
    }

    private ArrayList<PreferenceItem> parseFile(int resId, String firstBreadcrumb, String ignored) {
        java.util.ArrayList<PreferenceItem> results = new ArrayList<>();
        XmlPullParser xpp = context.getResources().getXml(resId);

        try {
            ArrayList<String> breadcrumbs = new ArrayList<>();
            ArrayList<String> keyBreadcrumbs = new ArrayList<>();
            if (!TextUtils.isEmpty(firstBreadcrumb)) {
                breadcrumbs.add(firstBreadcrumb);
            }
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    PreferenceItem result = parseSearchResult(xpp);
                    result.resId = resId;

                    if (!BLACKLIST.contains(xpp.getName()) && result.hasData()) {
                        result.breadcrumbs = joinBreadcrumbs(breadcrumbs);
                        result.keyBreadcrumbs = cleanupKeyBreadcrumbs(keyBreadcrumbs);
                        if (!ignored.contains(result.key)) {
                            results.add(result);
                        }
                    }
                    if (CONTAINERS.contains(xpp.getName())) {
                        breadcrumbs.add(result.title == null ? "" : result.title);
                    }
                    if (xpp.getName().equals("PreferenceScreen")) {
                        keyBreadcrumbs.add(getAttribute(xpp, "key"));
                    }
                } else if (xpp.getEventType() == XmlPullParser.END_TAG && CONTAINERS.contains(xpp.getName())) {
                    breadcrumbs.remove(breadcrumbs.size() - 1);
                    if (xpp.getName().equals("PreferenceScreen")) {
                        keyBreadcrumbs.remove(breadcrumbs.size() - 1);
                    }
                }

                xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private ArrayList<String> cleanupKeyBreadcrumbs(ArrayList<String> keyBreadcrumbs) {
        ArrayList<String> result = new ArrayList<>();
        for (String keyBreadcrumb : keyBreadcrumbs) {
            if (keyBreadcrumb != null) {
                result.add(keyBreadcrumb);
            }
        }
        return result;
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

    private String getAttribute(XmlPullParser xpp, String attribute) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            if (xpp.getAttributeName(i).equals(attribute)) {
                return xpp.getAttributeValue(i);
            }
        }
        return null;
    }

    private PreferenceItem parseSearchResult(XmlPullParser xpp) {
        PreferenceItem result = new PreferenceItem();
        result.title = readString(getAttribute(xpp, "title"));
        result.summary = readString(getAttribute(xpp, "summary"));
        result.key = readString(getAttribute(xpp, "key"));
        result.entries = readStringArray(getAttribute(xpp, "entries"));

        Log.d("PreferenceParser", "Found: " + xpp.getName() + "/" + result);
        return result;
    }

    private String readStringArray(@Nullable String s) {
        if (s == null) {
            return null;
        }
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

    private String readString(@Nullable String s) {
        if (s == null) {
            return null;
        }
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
