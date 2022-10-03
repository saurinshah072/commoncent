package com.commoncents.ui.dashboard.assignbrokenpallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.commoncents.R;
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ResultsItem;

import java.util.ArrayList;
import java.util.List;

public class PrintPackIDAdapter extends ArrayAdapter<ResultsItem> {
    private List<ResultsItem> countryListFull;

    private TextView txtID ;


    public PrintPackIDAdapter(@NonNull Context context, @NonNull List<ResultsItem> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_packiditem, parent, false
            );
        }
        txtID =  convertView.findViewById(R.id.txtID);
        txtID.setText(countryListFull.get(position).getPackId());
        return convertView;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ResultsItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ResultsItem item : countryListFull) {
                    if (item.getPackId().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ResultsItem) resultValue).getPackId();
        }
    };
}