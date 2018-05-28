package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchPreferenceFragment extends Fragment implements SearchPreferenceAdapter.SearchClickListener {
    private static final String SHARED_PREFS_FILE = "preferenceSearch";
    private static final int MAX_HISTORY = 5;
    private PreferenceParser searcher;
    private List<PreferenceItem> results;
    private List<HistoryItem> history;
    private SharedPreferences prefs;
    private SearchViewHolder viewHolder;
    private SearchConfiguration searchConfiguration;
    private SearchPreferenceAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        searcher = new PreferenceParser(getContext());

        searchConfiguration = SearchConfiguration.fromBundle(getArguments());
        ArrayList<Integer> files = searchConfiguration.getFiles();
        ArrayList<String> breadcrumbs = searchConfiguration.getBreadcrumbs();
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
        if (searchConfiguration.isHistoryEnabled()) {
            viewHolder.moreButton.setVisibility(View.VISIBLE);
        }
        viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), viewHolder.moreButton);
                popup.getMenuInflater().inflate(R.menu.searchpreference_more, popup.getMenu());
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

        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewHolder.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new SearchPreferenceAdapter();
        adapter.setSearchConfiguration(searchConfiguration);
        adapter.setOnItemClickListener(this);
        viewHolder.recyclerView.setAdapter(adapter);

        viewHolder.searchView.addTextChangedListener(textWatcher);

        if (!searchConfiguration.isSearchBarEnabled()) {
            viewHolder.cardView.setVisibility(View.GONE);
        }
        return rootView;
    }

    private void loadHistory() {
        history = new ArrayList<>();
        if (!searchConfiguration.isHistoryEnabled()) {
            return;
        }

        int size = prefs.getInt("history_size", 0);
        for (int i = 0; i < size; i++) {
            String title = prefs.getString("history_" + i, null);
            history.add(new HistoryItem(title));
        }
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("history_size", history.size());
        for (int i = 0; i < history.size(); i++) {
            editor.putString("history_" + i, history.get(i).getTerm());
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
        HistoryItem newItem = new HistoryItem(entry);
        if (!history.contains(newItem)) {
            if (history.size() >= MAX_HISTORY) {
                history.remove(history.size() - 1);
            }
            history.add(0, newItem);
            saveHistory();
            updateSearchResults(viewHolder.searchView.getText().toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSearchResults(viewHolder.searchView.getText().toString());

        if (searchConfiguration.isSearchBarEnabled()) {
            showKeyboard();
        }
    }

    private void showKeyboard() {
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

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setSearchTerm(CharSequence term) {
        viewHolder.searchView.setText(term);
    }

    private void updateSearchResults(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            showHistory();
            return;
        }

        results = searcher.searchFor(keyword, searchConfiguration.isFuzzySearchEnabled());
        adapter.setContent(new ArrayList<ListItem>(results));

        if (results.isEmpty()) {
            viewHolder.noResults.setVisibility(View.VISIBLE);
            viewHolder.recyclerView.setVisibility(View.GONE);
        } else {
            viewHolder.noResults.setVisibility(View.GONE);
            viewHolder.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showHistory() {
        viewHolder.noResults.setVisibility(View.GONE);
        viewHolder.recyclerView.setVisibility(View.VISIBLE);

        adapter.setContent(new ArrayList<ListItem>(history));
    }

    @Override
    public void onItemClicked(ListItem item, int position) {
        if (item.getType() == HistoryItem.TYPE) {
            CharSequence text = ((HistoryItem) item).getTerm();
            viewHolder.searchView.setText(text);
            viewHolder.searchView.setSelection(text.length());
        } else {
            addHistoryEntry(viewHolder.searchView.getText().toString());
            hideKeyboard();

            try {
                final SearchPreferenceResultListener callback = (SearchPreferenceResultListener) getActivity();
                PreferenceItem r = results.get(position);
                String screen = null;
                if (!r.keyBreadcrumbs.isEmpty()) {
                    screen = r.keyBreadcrumbs.get(r.keyBreadcrumbs.size() - 1);
                }
                SearchPreferenceResult result = new SearchPreferenceResult(r.key, r.resId, screen);
                callback.onSearchResultClicked(result);
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
        private RecyclerView recyclerView;
        private TextView noResults;
        private CardView cardView;

        SearchViewHolder(View root) {
            searchView = root.findViewById(R.id.search);
            clearButton = root.findViewById(R.id.clear);
            recyclerView = root.findViewById(R.id.list);
            moreButton = root.findViewById(R.id.more);
            noResults = root.findViewById(R.id.no_results);
            cardView = root.findViewById(R.id.search_card);
        }
    }
}
