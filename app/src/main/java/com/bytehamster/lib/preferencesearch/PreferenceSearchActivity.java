package com.bytehamster.lib.preferencesearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.bytehamster.preferencesearch.PreferenceActivity;
import com.bytehamster.preferencesearch.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferenceSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final String EXTRA_INDEX_FILES = "files";
    private PreferenceSearcher searcher;
    private ArrayList<PreferenceSearcher.SearchResult> results;

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

        updateSearchResults("");
        ((ListView) findViewById(R.id.list)).setOnItemClickListener(this);
        ((EditText) findViewById(R.id.search)).addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSearchResults(String keyword) {
        try {
            results = searcher.searchFor(keyword);
            ArrayList<Map<String, String>> results2 = new ArrayList<>();
            for (PreferenceSearcher.SearchResult result : results) {
                Map<String, String> m = new HashMap<>();
                m.put("title", result.title);
                m.put("summary", result.summary);
                results2.add(m);
            }
            SimpleAdapter sa = new SimpleAdapter(this, results2, android.R.layout.simple_list_item_2,
                    new String[]{"title", "summary"}, new int[]{android.R.id.text1, android.R.id.text2});
            ((ListView) findViewById(R.id.list)).setAdapter(sa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        PreferenceSearcher.SearchResult r = results.get(position);

        Intent i = new Intent(this, PreferenceActivity.class);
        i.putExtra(PreferenceSearchResult.ARGUMENT_KEY, r.key);
        i.putExtra(PreferenceSearchResult.ARGUMENT_FILE, r.resId);
        startActivity(i);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            updateSearchResults(editable.toString());
        }
    };
}
