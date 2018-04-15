package com.bytehamster.preferencesearch;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Hans-Peter Lehmann
 * @version 1.0
 */
public class SearchPreference extends Preference {
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
        root.findViewById(R.id.search).setFocusableInTouchMode(false);
        root.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
        root.findViewById(R.id.search).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getContext().startActivity(new Intent(getContext(), MainActivity.class));
                return true;
            }
        });
        return root;
    }
}
