package com.bytehamster.lib.preferencesearch;

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
import com.bytehamster.preferencesearch.R;

import java.util.ArrayList;

public class SearchPreference extends Preference implements View.OnClickListener {
    private ArrayList<Integer> filesToIndex = new ArrayList<>();

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
        getContext().startActivity(i);
    }

    public void addResourceFileToIndex(@XmlRes int resId) {
        filesToIndex.add(resId);
    }
}
