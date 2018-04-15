# PreferenceSearch
Search inside Android Preferences

<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/main.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/history.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/suggestions.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/result.png" />

## How to add to your app

Add PreferenceSearch to your dependencies:

    // ToDo

Add search bar to your `preferences.xml` file:

    <com.bytehamster.lib.preferencesearch.SearchPreference
        android:key="searchPreference" />
        
Define search index and react to search results in your `PreferenceFragment`:

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SearchPreference searchPreference = (SearchPreference) findPreference("searchPreference");
            searchPreference.openActivityOnResultClick(SimpleExample.class);
            searchPreference.addResourceFileToIndex(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();

            SearchPreferenceResult result = new SearchPreferenceResult(this, getActivity().getIntent().getExtras());
            if (result.hasData()) {
                // A search result was clicked
                result.scrollTo();
                result.setIcon();
            }
        }
    }
