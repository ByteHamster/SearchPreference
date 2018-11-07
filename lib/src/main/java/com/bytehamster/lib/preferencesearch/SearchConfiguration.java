package com.bytehamster.lib.preferencesearch;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.bytehamster.lib.preferencesearch.ui.RevealAnimationSetting;

import java.util.ArrayList;

public class SearchConfiguration {
    private static final String ARGUMENT_INDEX_ITEMS = "items";
    private static final String ARGUMENT_FUZZY_ENABLED = "fuzzy";
    private static final String ARGUMENT_HISTORY_ENABLED = "history_enabled";
    private static final String ARGUMENT_SEARCH_BAR_ENABLED = "search_bar_enabled";
    private static final String ARGUMENT_BREADCRUMBS_ENABLED = "breadcrumbs_enabled";
    private static final String ARGUMENT_REVEAL_ANIMATION_SETTING = "reveal_anim_setting";

    private ArrayList<SearchIndexItem> itemsToIndex = new ArrayList<>();
    private boolean historyEnabled = true;
    private boolean breadcrumbsEnabled = false;
    private boolean fuzzySearchEnabled = true;
    private boolean searchBarEnabled = true;
    private AppCompatActivity activity;
    private int containerResId = android.R.id.content;
    private RevealAnimationSetting revealAnimationSetting = null;

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
                .replace(containerResId, fragment, SearchPreferenceFragment.TAG)
                .addToBackStack("SearchPreferenceFragment")
                .commit();
        return fragment;
    }

    private Bundle toBundle() {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGUMENT_INDEX_ITEMS, itemsToIndex);
        arguments.putBoolean(ARGUMENT_HISTORY_ENABLED, historyEnabled);
        arguments.putParcelable(ARGUMENT_REVEAL_ANIMATION_SETTING, revealAnimationSetting);
        arguments.putBoolean(ARGUMENT_FUZZY_ENABLED, fuzzySearchEnabled);
        arguments.putBoolean(ARGUMENT_BREADCRUMBS_ENABLED, breadcrumbsEnabled);
        arguments.putBoolean(ARGUMENT_SEARCH_BAR_ENABLED, searchBarEnabled);
        return arguments;
    }

    static SearchConfiguration fromBundle(Bundle bundle) {
        SearchConfiguration config = new SearchConfiguration();
        config.itemsToIndex = bundle.getParcelableArrayList(ARGUMENT_INDEX_ITEMS);
        config.historyEnabled = bundle.getBoolean(ARGUMENT_HISTORY_ENABLED);
        config.revealAnimationSetting = bundle.getParcelable(ARGUMENT_REVEAL_ANIMATION_SETTING);
        config.fuzzySearchEnabled = bundle.getBoolean(ARGUMENT_FUZZY_ENABLED);
        config.breadcrumbsEnabled = bundle.getBoolean(ARGUMENT_BREADCRUMBS_ENABLED);
        config.searchBarEnabled = bundle.getBoolean(ARGUMENT_SEARCH_BAR_ENABLED);
        return config;
    }

    /**
     * Sets the current activity that also receives callbacks
     * @param activity The Activity that receives callbacks. Must implement SearchPreferenceResultListener.
     */
    public void setActivity(@NonNull AppCompatActivity activity) {
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
     * Display a reveal animation
     * @param origin Where the animation should start
     * @param container Container that should be covered
     */
    public void useAnimation(View origin, View container, @ColorInt int color) {
        revealAnimationSetting = new RevealAnimationSetting(
                (int) (origin.getX() + origin.getWidth() / 2),
                (int) (origin.getY() + origin.getHeight() / 2),
                container.getWidth(),
                container.getHeight(),
                color);
    }


    /**
     * Display a reveal animation
     * @param centerX Origin of the reveal animation
     * @param centerY Origin of the reveal animation
     * @param width Size of the main container
     * @param height Size of the main container
     * @param color Accent color to use
     */
    public void useAnimation(int centerX, int centerY, int width, int height, @ColorInt int color) {
        revealAnimationSetting = new RevealAnimationSetting(centerX, centerY, width, height, color);
    }

    /**
     * Adds a new file to the index
     * @param resId The preference file to index
     */
    public SearchIndexItem index(@XmlRes int resId) {
        SearchIndexItem item = new SearchIndexItem(resId, this);
        itemsToIndex.add(item);
        return item;
    }

    ArrayList<SearchIndexItem> getFiles() {
        return itemsToIndex;
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

    RevealAnimationSetting getRevealAnimationSetting() {
        return revealAnimationSetting;
    }

    /**
     * Adds a given R.xml resource to the search index
     */
    public static class SearchIndexItem implements Parcelable {
        private String breadcrumb = "";
        private final @XmlRes int resId;
        private final SearchConfiguration searchConfiguration;

        /**
         * Includes the given R.xml resource in the index
         * @param resId The resource to index
         */
        private SearchIndexItem(@XmlRes int resId, SearchConfiguration searchConfiguration) {
            this.resId = resId;
            this.searchConfiguration = searchConfiguration;
        }

        /**
         * Adds a breadcrumb
         * @param breadcrumb The breadcrumb to add
         * @return For chaining
         */
        public SearchIndexItem addBreadcrumb(@StringRes int breadcrumb) {
            assertNotParcel();
            return addBreadcrumb(searchConfiguration.activity.getString(breadcrumb));
        }

        /**
         * Adds a breadcrumb
         * @param breadcrumb The breadcrumb to add
         * @return For chaining
         */
        public SearchIndexItem addBreadcrumb(String breadcrumb) {
            assertNotParcel();
            this.breadcrumb = Breadcrumb.concat(this.breadcrumb, breadcrumb);
            return this;
        }

        /**
         * Throws an exception if the item does not have a searchConfiguration (thus, is restored from a parcel)
         */
        private void assertNotParcel() {
            if (searchConfiguration == null) {
                throw new IllegalStateException("SearchIndexItems that are restored from parcel can not be modified.");
            }
        }

        @XmlRes int getResId() {
            return resId;
        }

        String getBreadcrumb() {
            return breadcrumb;
        }

        public static final Creator<SearchIndexItem> CREATOR = new Creator<SearchIndexItem>() {
            @Override
            public SearchIndexItem createFromParcel(Parcel in) {
                return new SearchIndexItem(in);
            }

            @Override
            public SearchIndexItem[] newArray(int size) {
                return new SearchIndexItem[size];
            }
        };

        private SearchIndexItem(Parcel parcel){
            this.breadcrumb = parcel.readString();
            this.resId = parcel.readInt();
            this.searchConfiguration = null;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.breadcrumb);
            dest.writeInt(this.resId);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }
}
