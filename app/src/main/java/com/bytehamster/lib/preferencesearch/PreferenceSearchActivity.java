package com.bytehamster.lib.preferencesearch;

import android.content.Intent;
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
import com.bytehamster.preferencesearch.PreferenceActivity;
import com.bytehamster.preferencesearch.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PreferenceSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final String EXTRA_INDEX_FILES = "files";
    private PreferenceSearcher searcher;
    private ArrayList<PreferenceSearcher.SearchResult> results;
    private ArrayList<String> history;
    private ImageView clearButton;
    private ImageView moreButton;
    private EditText searchView;
    private boolean showingHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searcher = new PreferenceSearcher(this);

        ArrayList<Integer> files = getIntent().getIntegerArrayListExtra(EXTRA_INDEX_FILES);
        for (Integer file : files) {
            searcher.addResourceFile(file);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadHistory();
        searchView = findViewById(R.id.search);
        clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });

        moreButton = findViewById(R.id.more);
        moreButton.setVisibility(View.VISIBLE);
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

        updateSearchResults();
        ((ListView) findViewById(R.id.list)).setOnItemClickListener(this);
        searchView.addTextChangedListener(textWatcher);
    }

    private void loadHistory() {
        history = new ArrayList<>();
        history.add("Chec");
        history.add("Priv");
    }

    private void clearHistory() {
        searchView.setText("");
        history.clear();
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
            ((ListView) findViewById(R.id.list)).setAdapter(sa);
            showingHistory = false;
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
            ((ListView) findViewById(R.id.list)).setAdapter(sa);
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
            PreferenceSearcher.SearchResult r = results.get(position);
            Intent i = new Intent(this, PreferenceActivity.class);
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
