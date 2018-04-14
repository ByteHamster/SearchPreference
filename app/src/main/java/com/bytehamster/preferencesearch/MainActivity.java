package com.bytehamster.preferencesearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private PreferenceSearcher searcher;
    private ArrayList<PreferenceSearcher.SearchResult> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searcher = new PreferenceSearcher(this);
        searcher.addResourceFile(R.xml.preferences);

        updateSearchResults("");
        ((ListView) findViewById(R.id.list)).setOnItemClickListener(this);

        ((EditText) findViewById(R.id.search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSearchResults(editable.toString());
            }
        });
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
                new String[] {"title", "summary"} ,new int[] {android.R.id.text1, android.R.id.text2});
            ((ListView) findViewById(R.id.list)).setAdapter(sa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        PreferenceSearcher.SearchResult r = results.get(position);
        Toast.makeText(this, r.key, Toast.LENGTH_LONG).show();
    }
}
