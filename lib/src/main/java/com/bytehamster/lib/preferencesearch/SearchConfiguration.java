package com.bytehamster.lib.preferencesearch;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchConfiguration {
    private ArrayList<Integer> filesToIndex = new ArrayList<>();
    private ArrayList<String> breadcrumbsToIndex = new ArrayList<>();
    private boolean historyEnabled = true;
    private boolean breadcrumbsEnabled = false;
    private boolean fuzzySearchEnabled = true;
    private AppCompatActivity activity;
    private int containerResId = android.R.id.content;

    public void showSearchFragment() {
        if (activity == null) {
            throw new IllegalStateException("setActivity() not called");
        }

        Bundle arguments = new Bundle();
        arguments.putSerializable(SearchPreferenceFragment.ARGUMENT_INDEX_FILES, filesToIndex);
        arguments.putSerializable(SearchPreferenceFragment.ARGUMENT_INDEX_BREADCRUMBS, breadcrumbsToIndex);
        arguments.putBoolean(SearchPreferenceFragment.ARGUMENT_HISTORY_ENABLED, historyEnabled);
        arguments.putBoolean(SearchPreferenceFragment.ARGUMENT_FUZZY_ENABLED, fuzzySearchEnabled);
        arguments.putBoolean(SearchPreferenceFragment.ARGUMENT_BREADCRUMBS_ENABLED, breadcrumbsEnabled);

        SearchPreferenceFragment fragment = new SearchPreferenceFragment();
        fragment.setArguments(arguments);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(containerResId, fragment)
                .addToBackStack("SearchPreferenceFragment")
                .commit();
    }

    /**
     * Sets the current activity that also receives callbacks
     * @param activity The Activity that receives callbacks. Must implement SearchPreferenceResultListener.
     */
    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        if (!(activity instanceof SearchPreferenceResultListener)) {
            throw new IllegalArgumentException("Activity must implement SearchPreferenceResultListener");
        }
    }

    /**
     * Show a history of recent search terms if nothing was typed yet. Default is true
     * @param historyEnabled True if history should be enabled
     */
    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    /**
     * Allow to enable and disable fuzzy searching. Default is true
     * @param fuzzySearchEnabled True if search should be fuzzy
     */
    public void setFuzzySearchEnabled(boolean fuzzySearchEnabled) {
        this.fuzzySearchEnabled = fuzzySearchEnabled;
    }

    /**
     * Show breadcrumbs in the list of search results, containing of
     * the prefix given in addResourceFileToIndex, PreferenceCategory and PreferenceScreen.
     * Default is false
     * @param breadcrumbsEnabled True if breadcrumbs should be shown
     */
    public void setBreadcrumbsEnabled(boolean breadcrumbsEnabled) {
        this.breadcrumbsEnabled = breadcrumbsEnabled;
    }

    /**
     * Sets the container to use when loading the fragment
     * @param containerResId Resource id of the container
     */
    public void setFragmentContainerViewId(@IdRes int containerResId) {
        this.containerResId = containerResId;
    }

    /**
     * Begin adding a file to the index
     */
    public ResourceAdder index() {
        return new ResourceAdder(this);
    }

    /**
     * Adds a given R.xml resource to the search index
     */
    public static class ResourceAdder {
        private String breadcrumb = null;
        private final SearchConfiguration searchConfiguration;

        ResourceAdder(SearchConfiguration searchConfiguration) {
            this.searchConfiguration = searchConfiguration;
        }

        /**
         * Includes the given R.xml resource in the index
         * @param resId The resource to index
         */
        public void addFile (@XmlRes int resId) {
            if (breadcrumb == null) {
                breadcrumb = "";
            }

            searchConfiguration.filesToIndex.add(resId);
            searchConfiguration.breadcrumbsToIndex.add(breadcrumb);
        }

        /**
         * Adds a breadcrumb
         * @param breadcrumb The breadcrumb to add
         * @return For chaining
         */
        public ResourceAdder addBreadcrumb(@StringRes int breadcrumb) {
            return addBreadcrumb(searchConfiguration.activity.getString(breadcrumb));
        }

        /**
         * Adds a breadcrumb
         * @param breadcrumb The breadcrumb to add
         * @return For chaining
         */
        public ResourceAdder addBreadcrumb(String breadcrumb) {
            this.breadcrumb = Breadcrumb.concat(this.breadcrumb, breadcrumb);
            return this;
        }
    }
}
