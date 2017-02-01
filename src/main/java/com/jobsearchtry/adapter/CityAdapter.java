package com.jobsearchtry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.City;

import android.widget.Filter;

import java.util.ArrayList;

public class CityAdapter extends ArrayAdapter<City> implements Filterable {
	private final ArrayList<City> items;
	private final ArrayList<City> itemsAll;
	private final ArrayList<City> suggestions;

	public CityAdapter(Context context, int viewResourceId, ArrayList<City> items) {
		super(context, viewResourceId, items);
		this.items = items;
		this.itemsAll = (ArrayList<City>) items.clone();
		this.suggestions = new ArrayList<>();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context
					.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.spinner_item_text, parent,false);
		}
		TextView customerNameLabel = (TextView) v.findViewById(R.id.spinneritemqualification);
		if (customerNameLabel != null) {
			customerNameLabel.setText(items.get(position).getCiti_name());
		}
		return v;
	}


	@Override
	public Filter getFilter() {
		return nameFilter;
	}

	private final Filter nameFilter = new Filter() {
		@Override
		public String convertResultToString(Object resultValue) {
			return ((City) resultValue).getCiti_name();
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (constraint != null) {
				suggestions.clear();
				for (City customer : itemsAll) {
					if (customer.getCiti_name().toLowerCase().startsWith(constraint.toString()
                            .toLowerCase())) {
						suggestions.add(customer);
					}
				}
				FilterResults filterResults = new FilterResults();
				filterResults.values = suggestions;
				filterResults.count = suggestions.size();
				return filterResults;
			} else {
				return new FilterResults();
			}
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			ArrayList<City> filteredList = (ArrayList<City>) results.values;
			if (results != null && results.count > 0) {
				clear();
				for (City c : filteredList) {
					add(c);
				}
				notifyDataSetChanged();
			}
		}
	};
}
