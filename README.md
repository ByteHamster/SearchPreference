# SearchPreference

This is a library for Android apps that allows to search inside Preference xml files. The library provides a subclass of `Preference` that can be integrated into existing apps easily.

<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/main.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/history.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/suggestions.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/result.png" />

## Adding to your app

Add PreferenceSearch to your `app/build.gradle`:

    dependencies {
        implementation 'com.github.ByteHamster:SearchPreference:v1.0.3'
    }

Add PreferenceSearch to your `build.gradle`:

    allprojects {
        repositories {
            // ...
            maven { url 'https://jitpack.io' }
        }
    }

Add search bar to your `preferences.xml` file:

    <com.bytehamster.lib.preferencesearch.SearchPreference
        android:key="searchPreference" />
        
Define search index in your `PreferenceFragment`:


    public static class PrefsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SearchPreference searchPreference = (SearchPreference) findPreference("searchPreference");
            SearchConfiguration config = searchPreference.getSearchConfiguration();
            config.setActivity((AppCompatActivity) getActivity());
            config.index(R.xml.preferences);
        }
    }

And react to search results in your Activity:

    public class MyActivity extends AppCompatActivity implements SearchPreferenceResultListener {
        private PrefsFragment prefsFragment;

        @Override
        public void onSearchResultClicked(SearchPreferenceResult result) {
            result.closeSearchPage(this);
            result.highlight(prefsFragment);
        }
    }
