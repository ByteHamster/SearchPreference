package com.bytehamster.lib.preferencesearch;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.recyclerview.widget.RecyclerView;


public class SearchPreferenceResult {
    private final String key;
    private final int file;
    private final String screen;

    SearchPreferenceResult(String key, int file, String screen) {
        this.key = key;
        this.file = file;
        this.screen = screen;
    }

    /**
     * Returns the key of the preference pressed
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the file in which the result was found
     * @return The file in which the result was found
     */
    public int getResourceFile() {
        return file;
    }

    /**
     * Returns the screen in which the result was found
     * @return The screen in which the result was found
     */
    public String getScreen() {
        return screen;
    }

    /**
     * Highlight the preference that was found
     * @param prefsFragment Fragment that contains the preference
     */
    public void highlight(final PreferenceFragmentCompat prefsFragment) {
        new Handler().post(() -> doHighlight(prefsFragment));
    }

    private void doHighlight(final PreferenceFragmentCompat prefsFragment) {
        final Preference prefResult = prefsFragment.findPreference(getKey());

        if (prefResult == null) {
            Log.e("doHighlight", "Preference not found on given screen");
            return;
        }
        final RecyclerView recyclerView = prefsFragment.getListView();
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof PreferenceGroup.PreferencePositionCallback) {
            PreferenceGroup.PreferencePositionCallback callback = (PreferenceGroup.PreferencePositionCallback) adapter;
            final int position = callback.getPreferenceAdapterPosition(prefResult);
            if (position != RecyclerView.NO_POSITION) {
                recyclerView.scrollToPosition(position);
                recyclerView.postDelayed(() -> {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
                    if (holder != null) {
                        Drawable background = holder.itemView.getBackground();
                        if (Build.VERSION.SDK_INT >= 21 && background instanceof RippleDrawable) {
                            forceRippleAnimation((RippleDrawable) background);
                            return;
                        }
                    }
                    highlightFallback(prefsFragment, prefResult);
                }, 200);
                return;
            }
        }
        highlightFallback(prefsFragment, prefResult);
    }

    /**
     * Alternative (old) highlight method if ripple does not work
     */
    private void highlightFallback(PreferenceFragmentCompat prefsFragment, final Preference prefResult) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = prefsFragment.getActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = prefsFragment.getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                android.R.attr.textColorPrimary});
        int color = arr.getColor(0, 0xff3F51B5);
        arr.recycle();

        final Drawable oldIcon = prefResult.getIcon();
        final boolean oldSpaceReserved = prefResult.isIconSpaceReserved();
        Drawable arrow = AppCompatResources.getDrawable(prefsFragment.getContext(), R.drawable.searchpreference_ic_arrow_right);
        arrow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        prefResult.setIcon(arrow);
        prefsFragment.scrollToPreference(prefResult);
        new Handler().postDelayed(() -> {
            prefResult.setIcon(oldIcon);
            prefResult.setIconSpaceReserved(oldSpaceReserved);
        }, 1000);
    }

    @TargetApi(21)
    protected void forceRippleAnimation(RippleDrawable background) {
        final RippleDrawable rippleDrawable = background;
        Handler handler = new Handler();
        rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
        handler.postDelayed(() -> rippleDrawable.setState(new int[]{}), 1000);
    }

    /**
     * Closes the search results page
     * @param activity The current activity
     */
    public void closeSearchPage(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag(SearchPreferenceFragment.NAME)).commit();
    }
}
