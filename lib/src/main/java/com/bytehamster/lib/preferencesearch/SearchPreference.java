package com.bytehamster.lib.preferencesearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.annotation.XmlRes;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

public class SearchPreference extends Preference implements View.OnClickListener {
    private ArrayList<Integer> filesToIndex = new ArrayList<>();
    private ArrayList<String> breadcrumbsToIndex = new ArrayList<>();
    private boolean historyEnabled = true;
    private boolean breadcrumbsEnabled = true;
    private Class classToBeCalled;

    @SuppressWarnings("unused")
    public SearchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public SearchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public SearchPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (li == null) {
            return null;
        }
        View root = li.inflate(R.layout.search_preference, parent, false);

        EditText searchText = root.findViewById(R.id.search);
        searchText.setFocusable(false);
        searchText.setInputType(InputType.TYPE_NULL);
        searchText.setOnClickListener(this);

        root.findViewById(R.id.search_card).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(getContext(), PreferenceSearchActivity.class);
        i.putExtra(PreferenceSearchActivity.EXTRA_INDEX_FILES, filesToIndex);
        i.putExtra(PreferenceSearchActivity.EXTRA_INDEX_BREADCRUMBS, breadcrumbsToIndex);
        i.putExtra(PreferenceSearchActivity.EXTRA_HISTORY_ENABLED, historyEnabled);
        i.putExtra(PreferenceSearchActivity.EXTRA_CLASS_TO_BE_CALLED, classToBeCalled);
        i.putExtra(PreferenceSearchActivity.EXTRA_BREADCRUMBS_ENABLED, breadcrumbsEnabled);
        getContext().startActivity(i);
    }

    /**
     * Makes the search index include the given R.xml resource
     * @param resId The resource to index
     */
    public void addResourceFileToIndex(@XmlRes int resId) {
        addResourceFileToIndex(resId, "");
    }

    /**
     * Makes the search index include the given R.xml resource
     * @param resId The resource to index
     * @param breadcrumb Prefix to add to breadcrumbs when displaying search results from this file
     */
    public void addResourceFileToIndex(@XmlRes int resId, String breadcrumb) {
        filesToIndex.add(resId);
        breadcrumbsToIndex.add(breadcrumb);
    }

    /**
     * Show a history of recent search terms if nothing was typed yet
     * @param historyEnabled True if history should be enabled
     */
    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    /**
     * Specifies which Activity should be called when a search result was pressed
     * @param classToBeCalled The Activity to be called
     */
    public void openActivityOnResultClick(Class<? extends Activity> classToBeCalled) {
        this.classToBeCalled = classToBeCalled;
    }

    /**
     * Show breadcrumbs in the list of search results, containing of
     * the prefix given in addResourceFileToIndex, PreferenceCategory and PreferenceScreen
     * @param breadcrumbsEnabled True if breadcrumbs should be shown
     */
    public void setBreadcrumbsEnabled(boolean breadcrumbsEnabled) {
        this.breadcrumbsEnabled = breadcrumbsEnabled;
    }
}
