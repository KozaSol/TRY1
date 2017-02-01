package com.jobsearchtry.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.SkillCategory;

import java.util.ArrayList;

public class SkillCategoryAdapter extends BaseAdapter {
	private final Activity activity;
	private ArrayList<SkillCategory> skillcategoryList = new ArrayList<>();

	public SkillCategoryAdapter(Activity a, int spinnerItemText, ArrayList<SkillCategory>
			skillcategoryList) {
		activity = a;
		this.skillcategoryList = skillcategoryList;
	}

	public int getCount() {
		return skillcategoryList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView spinneritemskill;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) activity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.spinner_item_text, parent,
					false);
			holder = new ViewHolder();
			holder.spinneritemskill = (TextView) convertView
					.findViewById(R.id.spinneritemqualification);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.spinneritemskill.setText(skillcategoryList.get(position)
				.getSkillcategoryname());
		return convertView;
	}

}
