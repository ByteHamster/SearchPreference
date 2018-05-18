package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class SearchPreference extends Preference implements View.OnClickListener {
    private SearchConfiguration searchConfiguration = new SearchConfiguration();

    @SuppressWarnings("unused")
    public SearchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.searchpreference_preference);
    }

    @SuppressWarnings("unused")
    public SearchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.searchpreference_preference);
    }

    @SuppressWarnings("unused")
    public SearchPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.searchpreference_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        EditText searchText = (EditText) holder.findViewById(R.id.search);
        searchText.setFocusable(false);
        searchText.setInputType(InputType.TYPE_NULL);
        searchText.setOnClickListener(this);

        holder.findViewById(R.id.search_card).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        getSearchConfiguration().showSearchFragment();
    }

    /**
     * Returns the search configuration object for this preference
     * @return The search configuration
     */
    public SearchConfiguration getSearchConfiguration() {
        return searchConfiguration;
    }

    /**
     * Sets the current activity that also receives callbacks
     * @param activity The Activity that receives callbacks. Must implement SearchPreferenceResultListener.
     * @deprecated Use getSearchConfiguration().setActivity() instead.
     */
    public void setActivity(AppCompatActivity activity) {
        getSearchConfiguration().setActivity(activity);
    }

    /**
     * Show a history of recent search terms if nothing was typed yet. Default is true
     * @param historyEnabled True if history should be enabled
     * @deprecated Use getSearchConfiguration().setHistoryEnabled() instead.
     */
    public void setHistoryEnabled(boolean historyEnabled) {
        getSearchConfiguration().setHistoryEnabled(historyEnabled);
    }

    /**
     * Allow to enable and disable fuzzy searching. Default is true
     * @param fuzzySearchEnabled True if search should be fuzzy
     * @deprecated Use getSearchConfiguration().setFuzzySearchEnabled() instead.
     */
    public void setFuzzySearchEnabled(boolean fuzzySearchEnabled) {
        getSearchConfiguration().setFuzzySearchEnabled(fuzzySearchEnabled);
    }

    /**
     * Show breadcrumbs in the list of search results, containing of
     * the prefix given in addResourceFileToIndex, PreferenceCategory and PreferenceScreen.
     * Default is false
     * @param breadcrumbsEnabled True if breadcrumbs should be shown
     * @deprecated Use getSearchConfiguration().setBreadcrumbsEnabled() instead.
     */
    public void setBreadcrumbsEnabled(boolean breadcrumbsEnabled) {
        getSearchConfiguration().setBreadcrumbsEnabled(breadcrumbsEnabled);
    }

    /**
     * Sets the container to use when loading the fragment
     * @param containerResId Resource id of the container
     * @deprecated Use getSearchConfiguration().setFragmentContainerViewId() instead.
     */
    public void setFragmentContainerViewId(@IdRes int containerResId) {
        getSearchConfiguration().setFragmentContainerViewId(containerResId);
    }

    /**
     * Begin adding a file to the index
     * @deprecated Use getSearchConfiguration().index() instead.
     */
    public SearchConfiguration.ResourceAdder index() {
        return getSearchConfiguration().index();
    }
}
