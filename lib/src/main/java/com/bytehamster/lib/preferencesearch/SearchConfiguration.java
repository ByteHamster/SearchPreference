package com.bytehamster.lib.preferencesearch;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchConfiguration {
    private static final String ARGUMENT_INDEX_FILES = "files";
    private static final String ARGUMENT_INDEX_BREADCRUMBS = "breadcrumbs";
    private static final String ARGUMENT_FUZZY_ENABLED = "fuzzy";
    private static final String ARGUMENT_HISTORY_ENABLED = "history_enabled";
    private static final String ARGUMENT_SEARCH_BAR_ENABLED = "search_bar_enabled";
    private static final String ARGUMENT_BREADCRUMBS_ENABLED = "breadcrumbs_enabled";

    private ArrayList<Integer> filesToIndex = new ArrayList<>();
    private ArrayList<String> breadcrumbsToIndex = new ArrayList<>();
    private boolean historyEnabled = true;
    private boolean breadcrumbsEnabled = false;
    private boolean fuzzySearchEnabled = true;
    private boolean searchBarEnabled = true;
    private AppCompatActivity activity;
    private int containerResId = android.R.id.content;

    SearchConfiguration() {

    }

    /**
     * Creates a new search configuration
     * @param activity The Activity that receives callbacks. Must implement SearchPreferenceResultListener.
     */
    public SearchConfiguration(AppCompatActivity activity) {
        setActivity(activity);
    }

    /**
     * Shows the fragment
     * @return A reference to the fragment
     */
    public SearchPreferenceFragment showSearchFragment() {
        if (activity == null) {
            throw new IllegalStateException("setActivity() not called");
        }

        Bundle arguments = this.toBundle();
        SearchPreferenceFragment fragment = new SearchPreferenceFragment();
        fragment.setArguments(arguments);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(containerResId, fragment)
                .addToBackStack("SearchPreferenceFragment")
                .commit();
        return fragment;
    }

    private Bundle toBundle() {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_INDEX_FILES, filesToIndex);
        arguments.putSerializable(ARGUMENT_INDEX_BREADCRUMBS, breadcrumbsToIndex);
        arguments.putBoolean(ARGUMENT_HISTORY_ENABLED, historyEnabled);
        arguments.putBoolean(ARGUMENT_FUZZY_ENABLED, fuzzySearchEnabled);
        arguments.putBoolean(ARGUMENT_BREADCRUMBS_ENABLED, breadcrumbsEnabled);
        arguments.putBoolean(ARGUMENT_SEARCH_BAR_ENABLED, searchBarEnabled);
        return arguments;
    }

    static SearchConfiguration fromBundle(Bundle bundle) {
        SearchConfiguration config = new SearchConfiguration();
        config.filesToIndex = bundle.getIntegerArrayList(ARGUMENT_INDEX_FILES);
        config.breadcrumbsToIndex = bundle.getStringArrayList(ARGUMENT_INDEX_BREADCRUMBS);

        if (config.filesToIndex == null || config.breadcrumbsToIndex == null) {
            throw new AssertionError("Missing extras");
        } else if (config.filesToIndex.size() != config.breadcrumbsToIndex.size()) {
            throw new AssertionError("Extra sizes do not match");
        }
        config.historyEnabled = bundle.getBoolean(ARGUMENT_HISTORY_ENABLED);
        config.fuzzySearchEnabled = bundle.getBoolean(ARGUMENT_FUZZY_ENABLED);
        config.breadcrumbsEnabled = bundle.getBoolean(ARGUMENT_BREADCRUMBS_ENABLED);
        config.searchBarEnabled = bundle.getBoolean(ARGUMENT_SEARCH_BAR_ENABLED);
        return config;
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
     * Show the search bar above the list. When setting this to false, you have to use {@see SearchPreferenceFragment#setSearchTerm(String) setSearchTerm} instead
     * Default is true
     * @param searchBarEnabled True if search bar should be shown
     */
    public void setSearchBarEnabled(boolean searchBarEnabled) {
        this.searchBarEnabled = searchBarEnabled;
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

    ArrayList<Integer> getFiles() {
        return filesToIndex;
    }

    ArrayList<String> getBreadcrumbs() {
        return breadcrumbsToIndex;
    }

    boolean isHistoryEnabled() {
        return historyEnabled;
    }

    boolean isBreadcrumbsEnabled() {
        return breadcrumbsEnabled;
    }

    boolean isFuzzySearchEnabled() {
        return fuzzySearchEnabled;
    }

    boolean isSearchBarEnabled() {
        return searchBarEnabled;
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
