package com.bytehamster.lib.preferencesearch;

import android.content.Context;
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
}
