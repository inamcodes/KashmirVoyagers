package com.inam.kashtrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CashEntryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ENTRY = 1;

    private List<Object> rows = new ArrayList<>();

    /**
     * Takes a list of entries (already sorted DESC by timestamp from Room) and
     * groups them under date headers, computing per-day Out/In totals.
     */
    public static List<Object> buildRows(List<CashEntry> entries) {
        List<Object> rows = new ArrayList<>();
        int i = 0;
        while (i < entries.size()) {
            CashEntry first = entries.get(i);
            String label = DateUtils.formatDate(first.getTimestamp());

            List<CashEntry> group = new ArrayList<>();
            int j = i;
            while (j < entries.size()
                    && DateUtils.formatDate(entries.get(j).getTimestamp()).equals(label)) {
                group.add(entries.get(j));
                j++;
            }

            double out = 0;
            double in = 0;
            for (CashEntry e : group) {
                if ("OUT".equals(e.getType())) {
                    out += e.getAmount();
                } else {
                    in += e.getAmount();
                }
            }

            rows.add(new DateHeader(label, group.size(), out, in));
            rows.addAll(group);
            i = j;
        }
        return rows;
    }

    public void submitList(List<Object> newRows) {
        this.rows = newRows;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof DateHeader ? TYPE_HEADER : TYPE_ENTRY;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_cash_entry, parent, false);
            return new EntryViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);
        Context context = holder.itemView.getContext();

        if (holder instanceof HeaderViewHolder) {
            DateHeader header = (DateHeader) row;
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.tvDate.setText(header.dateLabel);
            h.tvCount.setText(header.count + (header.count == 1 ? " Entry" : " Entries"));
            h.tvOut.setText("Out Rs " + DateUtils.formatAmount(header.out));
            h.tvIn.setText("In Rs " + DateUtils.formatAmount(header.in));
        } else {
            CashEntry entry = (CashEntry) row;
            EntryViewHolder h = (EntryViewHolder) holder;
            boolean isIn = "IN".equals(entry.getType());

            h.tvTime.setText(DateUtils.formatTime(entry.getTimestamp()));
            h.tvDescription.setText(entry.getDescription());
            h.tvAmount.setText((isIn ? "" : "-") + DateUtils.formatAmount(entry.getAmount()));
            h.tvAmount.setTextColor(ContextCompat.getColor(context,
                    isIn ? R.color.green_in : R.color.red_out));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCount, tvOut, tvIn;

        HeaderViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
            tvCount = v.findViewById(R.id.tvCount);
            tvOut = v.findViewById(R.id.tvOut);
            tvIn = v.findViewById(R.id.tvIn);
        }
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvDescription, tvAmount;

        EntryViewHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tvTime);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvAmount = v.findViewById(R.id.tvAmount);
        }
    }
}
