package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchPreferenceFragment extends Fragment implements AdapterView.OnItemClickListener {
    static final String ARGUMENT_INDEX_FILES = "files";
    static final String ARGUMENT_INDEX_BREADCRUMBS = "breadcrumbs";
    static final String ARGUMENT_HISTORY_ENABLED = "history_enabled";
    static final String ARGUMENT_BREADCRUMBS_ENABLED = "breadcrumbs_enabled";

    private static final String SHARED_PREFS_FILE = "preferenceSearch";
    private static final int MAX_HISTORY = 5;
    private PreferenceParser searcher;
    private ArrayList<PreferenceParser.ParseResult> results;
    private ArrayList<String> history;
    private boolean showingHistory = false;
    private SharedPreferences prefs;
    private SearchViewHolder viewHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        searcher = new PreferenceParser(getContext());

        ArrayList<Integer> files = getArguments().getIntegerArrayList(ARGUMENT_INDEX_FILES);
        ArrayList<String> breadcrumbs = getArguments().getStringArrayList(ARGUMENT_INDEX_BREADCRUMBS);
        if (files == null || breadcrumbs == null || files.size() != breadcrumbs.size()) {
            throw new AssertionError("Got incorrect arguments");
        }
        for (int i = 0; i < files.size(); i++) {
            searcher.addResourceFile(files.get(i), breadcrumbs.get(i));
        }
        loadHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.searchpreference_fragment, container, false);
        viewHolder = new SearchViewHolder(rootView);


        viewHolder.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.searchView.setText("");
            }
        });
        if (getArguments().getBoolean(ARGUMENT_HISTORY_ENABLED, true)) {
            viewHolder.moreButton.setVisibility(View.VISIBLE);
        }
        viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), viewHolder.moreButton);
                popup.getMenuInflater().inflate(R.menu.more_search, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.clear_history) {
                            clearHistory();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        viewHolder.listView.setOnItemClickListener(this);
        viewHolder.searchView.addTextChangedListener(textWatcher);
        return rootView;
    }

    private void loadHistory() {
        history = new ArrayList<>();
        if (!getArguments().getBoolean(ARGUMENT_HISTORY_ENABLED, true)) {
            return;
        }

        int size = prefs.getInt("history_size", 0);
        for (int i = 0; i < size; i++) {
            history.add(prefs.getString("history_" + i, null));
        }
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("history_size", history.size());
        for (int i = 0; i < history.size(); i++) {
            editor.putString("history_" + i, history.get(i));
        }
        editor.apply();
    }

    private void clearHistory() {
        viewHolder.searchView.setText("");
        history.clear();
        saveHistory();
        updateSearchResults("");
    }

    private void addHistoryEntry(String entry) {
        if (!history.contains(entry)) {
            if (history.size() >= MAX_HISTORY) {
                history.remove(history.size() - 1);
            }
            history.add(0, entry);
            saveHistory();
            updateSearchResults(viewHolder.searchView.getText().toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSearchResults(viewHolder.searchView.getText().toString());

        viewHolder.searchView.post(new Runnable() {
            @Override
            public void run() {
                viewHolder.searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(viewHolder.searchView, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }

    private void updateSearchResults(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            showHistory();
            return;
        }

        results = searcher.searchFor(keyword);
        ArrayList<Map<String, String>> results2 = new ArrayList<>();
        for (PreferenceParser.ParseResult result : results) {
            Map<String, String> m = new HashMap<>();
            m.put("title", result.title);
            m.put("summary", result.summary);
            m.put("breadcrumbs", result.breadcrumbs);
            results2.add(m);
        }

        SimpleAdapter sa;
        if (getArguments().getBoolean(ARGUMENT_BREADCRUMBS_ENABLED, true)) {
            sa = new SimpleAdapter(getContext(), results2, R.layout.searchpreference_list_item_result_breadcrumbs,
                    new String[]{"title", "summary", "breadcrumbs"}, new int[]{R.id.title, R.id.summary, R.id.breadcrumbs});
        } else {
            sa = new SimpleAdapter(getContext(), results2, R.layout.searchpreference_list_item_result,
                    new String[]{"title", "summary"}, new int[]{R.id.title, R.id.summary});
        }
        viewHolder.listView.setAdapter(sa);
        showingHistory = false;

        if (results.isEmpty()) {
            viewHolder.noResults.setVisibility(View.VISIBLE);
            viewHolder.listView.setVisibility(View.GONE);
        } else {
            viewHolder.noResults.setVisibility(View.GONE);
            viewHolder.listView.setVisibility(View.VISIBLE);
        }
    }

    private void showHistory() {
        viewHolder.noResults.setVisibility(View.GONE);
        viewHolder.listView.setVisibility(View.VISIBLE);

        ArrayList<Map<String, String>> results2 = new ArrayList<>();
        for (String entry : history) {
            Map<String, String> m = new HashMap<>();
            m.put("title", entry);
            results2.add(m);
        }
        SimpleAdapter sa = new SimpleAdapter(getContext(), results2, R.layout.searchpreference_list_item_history,
                new String[]{"title"}, new int[]{R.id.title});
        viewHolder.listView.setAdapter(sa);
        showingHistory = true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        if (showingHistory) {
            CharSequence text = ((TextView) view.findViewById(R.id.title)).getText();
            viewHolder.searchView.setText(text);
            viewHolder.searchView.setSelection(text.length());
        } else {
            addHistoryEntry(viewHolder.searchView.getText().toString());
            getFragmentManager().popBackStack();

            try {
                final SearchPreferenceResultListener callback = (SearchPreferenceResultListener) getActivity();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        PreferenceParser.ParseResult r = results.get(position);
                        SearchPreferenceResult result = new SearchPreferenceResult(r.key, r.resId);
                        callback.onSearchResultClicked(result);
                    }
                });
            } catch (ClassCastException e) {
                throw new ClassCastException(getActivity().toString() + " must implement SearchPreferenceResultListener");
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            updateSearchResults(editable.toString());
            viewHolder.clearButton.setVisibility(editable.toString().isEmpty() ? View.GONE : View.VISIBLE);
        }
    };

    private class SearchViewHolder {
        private ImageView clearButton;
        private ImageView moreButton;
        private EditText searchView;
        private ListView listView;
        private TextView noResults;

        SearchViewHolder(View root) {
            searchView = root.findViewById(R.id.search);
            clearButton = root.findViewById(R.id.clear);
            listView = root.findViewById(R.id.list);
            moreButton = root.findViewById(R.id.more);
            noResults = root.findViewById(R.id.no_results);
        }
    }
}
