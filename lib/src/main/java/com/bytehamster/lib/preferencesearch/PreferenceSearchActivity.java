package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreferenceSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final String EXTRA_INDEX_FILES = "files";
    static final String EXTRA_HISTORY_ENABLED = "history_enabled";
    static final String EXTRA_CLASS_TO_BE_CALLED = "class_to_be_called";
    private static final String SHARED_PREFS_FILE = "preferenceSearch";
    private PreferenceSearcher searcher;
    private ArrayList<PreferenceSearcher.SearchResult> results;
    private ArrayList<String> history;
    private ImageView clearButton;
    private ImageView moreButton;
    private EditText searchView;
    private ListView listView;
    private boolean showingHistory = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        searcher = new PreferenceSearcher(this);

        ArrayList<Integer> files = getIntent().getIntegerArrayListExtra(EXTRA_INDEX_FILES);
        for (Integer file : files) {
            searcher.addResourceFile(file);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadHistory();
        searchView = findViewById(R.id.search);
        clearButton = findViewById(R.id.clear);
        listView = findViewById(R.id.list);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });

        moreButton = findViewById(R.id.more);
        if (getIntent().getBooleanExtra(EXTRA_HISTORY_ENABLED, true)) {
            moreButton.setVisibility(View.VISIBLE);
        }
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(PreferenceSearchActivity.this, moreButton);
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
        listView.setOnItemClickListener(this);
        searchView.addTextChangedListener(textWatcher);
    }

    private void loadHistory() {
        history = new ArrayList<>();

        if (getIntent().getBooleanExtra(EXTRA_HISTORY_ENABLED, true)) {
            Set<String> set = prefs.getStringSet("history", new HashSet<String>());
            history.addAll(set);
        }
    }

    private void clearHistory() {
        searchView.setText("");
        history.clear();
        saveHistory();
        updateSearchResults();
    }

    private void addHistoryEntry(String entry) {
        if (!history.contains(entry)) {
            history.add(entry);
            saveHistory();
            updateSearchResults();
        }
    }

    private void saveHistory() {
        Set<String> set = new HashSet<>(history);
        prefs.edit().putStringSet("history", set).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSearchResults();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSearchResults() {
        String keyword = searchView.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            showHistory();
            return;
        }

        try {
            results = searcher.searchFor(keyword);
            ArrayList<Map<String, String>> results2 = new ArrayList<>();
            for (PreferenceSearcher.SearchResult result : results) {
                Map<String, String> m = new HashMap<>();
                m.put("title", result.title);
                m.put("summary", result.summary);
                results2.add(m);
            }
            SimpleAdapter sa = new SimpleAdapter(this, results2, R.layout.search_result_item,
                    new String[]{"title", "summary"}, new int[]{R.id.title, R.id.summary});
            listView.setAdapter(sa);
            showingHistory = false;

            if (results.isEmpty()) {
                findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                findViewById(R.id.no_results).setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHistory() {
        try {
            ArrayList<Map<String, String>> results2 = new ArrayList<>();
            for (String entry : history) {
                Map<String, String> m = new HashMap<>();
                m.put("title", entry);
                results2.add(m);
            }
            SimpleAdapter sa = new SimpleAdapter(this, results2, R.layout.search_history_item,
                    new String[]{"title"}, new int[]{R.id.title});
            listView.setAdapter(sa);
            showingHistory = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (showingHistory) {
            searchView.setText(((TextView) view.findViewById(R.id.title)).getText());
        } else {
            addHistoryEntry(searchView.getText().toString());
            PreferenceSearcher.SearchResult r = results.get(position);
            Class toBeCalled = (Class) getIntent().getSerializableExtra(EXTRA_CLASS_TO_BE_CALLED);
            Intent i = new Intent(this, toBeCalled);
            i.putExtra(PreferenceSearchResult.ARGUMENT_KEY, r.key);
            i.putExtra(PreferenceSearchResult.ARGUMENT_FILE, r.resId);
            startActivity(i);
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
            updateSearchResults();
            clearButton.setVisibility(editable.toString().isEmpty() ? View.GONE : View.VISIBLE);
        }
    };
}
