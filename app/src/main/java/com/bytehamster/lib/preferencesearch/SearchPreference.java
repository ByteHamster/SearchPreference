package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bytehamster.preferencesearch.R;

public class SearchPreference extends Preference implements View.OnClickListener {
    public SearchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SearchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = li.inflate(R.layout.search_preference, parent, false);

        ((EditText) root.findViewById(R.id.search)).setInputType(InputType.TYPE_NULL);
        root.findViewById(R.id.search).setOnClickListener(this);
        root.findViewById(R.id.search_card).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        getContext().startActivity(new Intent(getContext(), PreferenceSearchActivity.class));
    }
}
