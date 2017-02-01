package com.jobsearchtry.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Qualification;

public class SpinnerAdapter extends BaseAdapter {

	private final Activity activity;
	private ArrayList<Qualification> qualificationList = new ArrayList<>();

	public SpinnerAdapter(Activity a, int spinnerItemText, ArrayList<Qualification>
			qualificationList) {
		activity = a;
		this.qualificationList = qualificationList;
	}

	public int getCount() {
		return qualificationList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView spinneritemqualification;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) activity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.spinner_item_text, parent,
					false);
			holder = new ViewHolder();
			holder.spinneritemqualification = (TextView) convertView
					.findViewById(R.id.spinneritemqualification);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.spinneritemqualification.setText(qualificationList.get(position)
				.getQuali_name());
		return convertView;
	}
}