package com.raising.app.util;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.List;

/**
 * Just an array adapter that disables filtering for MultiAutoCompleteTextView
 * @param <T> generic for the type of the list
 */

public class NoFilterArrayAdapter<T>
        extends ArrayAdapter<T>
{
    private Filter filter = new KNoFilter();
    public List<T> items;

    @Override
    public Filter getFilter() {
        return filter;
    }

    public NoFilterArrayAdapter(Context context, int textViewResourceId,
                         List<T> objects) {
        super(context, textViewResourceId, objects);
        items = objects;
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = items;
            result.count = items.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }
}