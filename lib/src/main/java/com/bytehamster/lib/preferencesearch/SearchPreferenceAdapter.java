package com.bytehamster.lib.preferencesearch;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class SearchPreferenceAdapter extends RecyclerView.Adapter<SearchPreferenceAdapter.ViewHolder> {
    private List<ListItem> dataset;
    private SearchConfiguration searchConfiguration;
    private SearchClickListener onItemClickListener;


    SearchPreferenceAdapter() {
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == PreferenceItem.TYPE) {
            return new PreferenceViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.searchpreference_list_item_result, parent, false));
        } else {
            return new HistoryViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.searchpreference_list_item_history, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder h, final int position) {
        final ListItem listItem = dataset.get(position);
        if (getItemViewType(position) == HistoryItem.TYPE) {
            HistoryViewHolder holder = (HistoryViewHolder) h;
            HistoryItem item = (HistoryItem) listItem;
            holder.term.setText(item.getTerm());
        } else if (getItemViewType(position) == PreferenceItem.TYPE) {
            PreferenceViewHolder holder = (PreferenceViewHolder) h;
            PreferenceItem item = (PreferenceItem) listItem;
            holder.title.setText(item.title);

            if (TextUtils.isEmpty(item.summary)) {
                holder.summary.setVisibility(View.GONE);
            } else {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(item.summary);
            }

            if (searchConfiguration.isBreadcrumbsEnabled()) {
                holder.breadcrumbs.setText(item.breadcrumbs);
                holder.breadcrumbs.setAlpha(0.6f);
                holder.summary.setAlpha(1.0f);
            } else {
                holder.breadcrumbs.setVisibility(View.GONE);
                holder.summary.setAlpha(0.6f);
            }

        }

        h.root.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(listItem, h.getAdapterPosition());
            }
        });
        h.root.setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemDeleteClicked(h.getAdapterPosition());
            }
            return false;
        });
    }

    void setContent(List<ListItem> items) {
        dataset = new ArrayList<>(items);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position).getType();
    }

    void setSearchConfiguration(SearchConfiguration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    void setOnItemClickListener(SearchClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface SearchClickListener {
        void onItemClicked(ListItem item, int position);
        void onItemDeleteClicked(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        ViewHolder(View v) {
            super(v);
            root = v;
        }
    }

    static class HistoryViewHolder extends ViewHolder {
        TextView term;
        HistoryViewHolder(View v) {
            super(v);
            term = v.findViewById(R.id.term);
        }
    }

    static class PreferenceViewHolder extends ViewHolder {
        TextView title;
        TextView summary;
        TextView breadcrumbs;
        PreferenceViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            summary = v.findViewById(R.id.summary);
            breadcrumbs = v.findViewById(R.id.breadcrumbs);
        }
    }
}
