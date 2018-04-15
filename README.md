# SearchPreference
Search inside Android Preferences

[ ![Download](https://api.bintray.com/packages/bytehamster/android/SearchPreference/images/download.svg) ](https://bintray.com/bytehamster/android/SearchPreference/_latestVersion)

<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/main.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/history.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/suggestions.png" /> <img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/result.png" />

## Adding to your app

Add PreferenceSearch to your `build.gradle`:

    dependencies {
        compile 'com.bytehamster.lib:searchpreference:1.0.0'
    }

Add PreferenceSearch to your `settings.gradle`:

    allprojects {
        repositories {
            // ...
            maven {
                url  "https://dl.bintray.com/bytehamster/android"
            }
        }
    }

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

            SearchPreferenceResult result = new SearchPreferenceResult(this,
                    getActivity().getIntent().getExtras());
            if (result.hasData()) {
                // A search result was clicked
                result.scrollTo();
                result.setIcon();
            }
        }
    }

## Developer info

Releasing to bintray:

    build generatePomFileForReleasePublication bintrayUpload
